package neuralnetworklibary.Learning_GA;

import java.util.Arrays;
import java.util.Random;

public class Population {

    private int size;
    private Agent population[];
    private Agent pop[];
    private Random r;
    private int goalx;
    private int goaly;
    private int startx;
    private int starty;
    private char[][] map;
    double mutation = 0.05;
    double elitism = 50;
    private int gen = 1;
    private double con;

    public Population(int size) {
        this.size = size;
        r = new Random();
    }

    //luo satunnaisen populaation
    public void createPopulation() {
        population = new Agent[size];
        for (int i = 0; i < size; i++) {
            population[i] = new Agent(startx, starty, goalx, goaly, map);
            population[i].setMap(map);
        }
        gen = 1;
    }

    //palauttaa yksilön i.
    public Agent getInvidual(int i) {
        if (i >= population.length) {
            return null;
        }
        return population[i];
    }

    //päivittää sukupolvea
    public void updateGeneration() {
        double gn = generationFitness(goalx, goaly);
        System.out.println("gen: " + gen);
        System.out.println("gn fitness: " + gn + " avg fitness: " + 1.0 * gn / size);
        Arrays.sort(population);
        pop = new Agent[size];
        con = 1.0 * population[0].getCoins() / population[0].coinsInmap();
        System.out.println("coins: " + population[0].getCoins() + "  convergence: : " + 1.0 * population[0].getCoins() / population[0].coinsInmap() + " hp: " + population[0].getHealth() + " mines stepped: " + population[0].getMines());
        for (int i = addTopNetworks(); i < population.length; i++) {
            Agent a = tournamentSelection(kT());
            Agent b = tournamentSelection(kT());
            crossover(a.getBrain().getWeights(), b.getBrain().getWeights(), a.getBrain().getBiasweights(), b.getBrain().getBiasweights(), a.getBrain().getIntputNeurons(), 1.0 / ((a.getCoins() + b.getCoins()) / 2.0), i);
        }
        population = pop;
        gen++;
        System.out.println("");
    }

    public int addTopNetworks() {
        int lim = (int) elitism;
        for (int i = 0; i < lim; i++) {
            pop[i] = new Agent(population[i].getBrain().getWeights(), population[i].getBrain().getBiasweights(), map);
            mutateWeights(pop[i].getBrain().getWeights(), 1.0 / population[i].getCoins());
            mutateWeights(pop[i].getBrain().getBiasweights(), 1.0 / population[i].getCoins());
        }
        return lim;
    }

    public int kT() {
        if (gen >= 500) {
            return 2;
        } else {
            return 10;
        }
    }

    public Agent tournamentSelection(int k) {
        Agent best = population[r.nextInt(size)];
        for (int i = 0; i < k; i++) {
            int n = r.nextInt(size);
            if (best.getFitness() < population[n].getFitness()) {
                best = population[n];
            }
        }
        return best;
    }

    //sukupolven fitness
    public double generationFitness(int goalx, int goaly) {
        double f = 0;
        for (int i = 0; i < population.length; i++) {
            double currentFit = population[i].getHealth() * 2 + (1.0 * population[i].getCoins() / population[i].coinsInmap());
            f += currentFit;
            population[i].setFitness(currentFit);
        }
        return f;
    }

    public void mutateWeights(double w[][], double gaussianMutation) {
        for (int i = 0; i < w.length; i++) {
            for (int j = 0; j < w[i].length; j++) {
                if (r.nextDouble() < mutation) {
                    double interval = 2.0;
                    // w[i][j] += Math.random() * (interval - (-interval)) + (-interval);
                    //w[i][j] = r.nextGaussian() * gaussianMutation + w[i][j];
                    w[i][j] = r.nextGaussian() * 0.10 + w[i][j];
                }
            }
        }
    }

    public void crossover(double Aweights[][], double Bweights[][], double biasA[][], double biasB[][], int inputNeurons, double gaussianMutation, int invidual) {
        Random r = new Random();
        double weights[][] = new double[Aweights.length][0];
        double bias[][] = new double[biasA.length][0];
        int neuron = inputNeurons;
        for (int i = 0; i < Aweights.length; i++) {
            double layer[] = new double[Aweights[i].length];
            //changeWeights(Aweights, Bweights, mutation, layer, r, i);
            changeNeurons(weights, Aweights, Bweights, gaussianMutation, layer, i, neuron);
            changeBias(bias, biasA, biasB, i, gaussianMutation);
            if (i + 1 < Aweights.length) {
                neuron = Aweights[i].length / neuron;
            }
        }
        pop[invidual] = new Agent(weights, bias, map);
        mutateWeights(pop[invidual].getBrain().getWeights(), gaussianMutation);
        mutateWeights(pop[invidual].getBrain().getBiasweights(), gaussianMutation);
    }

    public void changeBias(double bias[][], double biasA[][], double biasB[][], int i, double gaussianMutation) {
        double layer[] = new double[biasA[i].length];
        int targetBias = r.nextInt(2);
        for (int j = 0; j < biasB[i].length; j++) {
            if (targetBias == 0) {
                layer[j] = biasA[i][j];
            } else {
                layer[j] = biasB[i][j];
            }
        }
        bias[i] = layer;
    }

    // vaihtaa kokonaisen neuronin painot.
    public void changeNeurons(double cWeights[][], double Aweights[][], double Bweights[][], double gaussianMutation, double layer[], int i, int neuron) {
        for (int j = 0; j < Aweights[i].length; j += neuron) {
            int fromParent = r.nextInt(2);
            for (int k = j; k < j + neuron; k++) {
                if (fromParent == 0) {
                    layer[k] = Aweights[i][k];
                } else {
                    layer[k] = Bweights[i][k];
                }
            }
        }
        cWeights[i] = layer;
    }

    public void setMap(char[][] map) {
        this.map = map;
    }

    public void setGoal(int y, int x) {
        this.goalx = x;
        this.goaly = y;
    }

    public void setStart(int y, int x) {
        this.startx = x;
        this.starty = y;
    }

    public int getGeneration() {
        return gen;
    }

    public double getBestFitness() {
        return con;
    }

}
