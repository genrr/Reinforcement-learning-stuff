package neuralnetworklibary.Learning_GA;

import java.util.Arrays;
import neuralnetworklibary.Network;

public class Agent implements Comparable<Agent> {

    private int x;
    private int y;
    private int goalx;
    private int goaly;
    private double fitness = 0;
    private Network brain;
    private char map[][];
    private int time;
    private int health = 100;
    private int coins = 0;
    private int mines = 0;
    private int[][] squaresVisited;
    private int[][] explored;
    private int visitedOnce;
    private int step = 0;
    private int coinsInmap;
    private double areaExplored = 0;

    // agentti, jolla satunnaisesti generoidut painot.
    public Agent(int x, int y, int goalx, int goaly, char map[][]) {
        this.x = x;
        this.y = y;
        this.goalx = goalx;
        setMap(map);
        this.squaresVisited = new int[map.length][map.length];
        explored = new int[map.length][map.length];
        this.goaly = goaly;
        this.brain = new Network();
    }

    public Agent(double weights[][], double bias[][], char map[][]) {
        brain = new Network(weights, bias);
        setMap(map);
        this.squaresVisited = new int[map.length][map.length];
        explored = new int[map.length][map.length];
    }

    //laskee seuraavan siirron
    public double[] computeNextState() {
        return brain.compute(generateInputVector());
    }

    //generoi syötevektorin
    public double[] generateInputVector() {
        int deltaX[] = new int[]{0, 0, 1, -1, 1, -1, 1, -1};
        int deltaY[] = new int[]{1, -1, 0, 0, 1, 1, -1, -1};
        return scanSurroundingSquares(deltaY, deltaX, brain.getIntputNeurons());
    }

    public double[] scanSurroundingSquares(int deltaY[], int deltaX[], int inputNeurons) {
        double data[] = new double[inputNeurons];
        int e = 0;
        int yy = 0;
        int xx = 0;
        int d = 1000;
        for (int j = 0; j < map.length; j++) {
            for (int k = 0; k < map.length; k++) {
                if (map[j][k] == 'c' && Math.abs(j - getX()) + Math.abs(k - getY()) < d) {
                    d = Math.abs(j - getX()) + Math.abs(k - getY());
                    yy = k;
                    xx = j;
                }
            }
        }
        data[e] = yy;
        data[e + 1] = xx;
        data[e + 2] = getY();
        data[e + 3] = getX();
        //data[i] = getHealth() - 20;
        /*for (int i = getX() - 5; i < getX() + 5; i++) {
            int dd = 100;
            for (int j = getY() - 5; j < getY() + 5; j++) {
                if (i == getX() && j == getY()) {
                    continue;
                }
                if (i >= 0 && j >= 0 && i < map.length && j < map.length) {
                    if (map[i][j] == 't' && dd > (Math.abs(i - getX() + Math.abs(j - getY())))) {
                        dd = (Math.abs(i - getX() + Math.abs(j - getY())));
                    }
                }
            }
            data[e] = d * 3;
            e++;
        }*/
        return data;
    }

    public double datafromSquare(int y, int x) {
        if (map[x][y] == 't') {
            return 1;
        }
        return 0;
    }

    public int iterateDelta(int deltaX[], int deltaY[], double data[], int i, char object) {
        for (int e = 0; e < 4; i++, e++) {
            insertData(deltaY[e], deltaX[e], data, i, e, object);
        }
        return i;
    }

    public void insertData(int dy, int dx, double data[], int i, int e, char object) {
        if (getX() + dx >= 0 && getY() + dy >= 0 && getY() + dy < map[0].length && getX() + dx < map.length) {
            if (map[getX() + dx][getY() + dy] == object) {
                data[i] = 1;
            }
        }// else {
        //  data[i] = (double) '#' * 10;
        //}
    }

    public void scalarMultiply(int vector[], double scalar) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] *= scalar;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setVisitedSquare() {
        if (squaresVisited[getX()][getY()] == 0) {
            squaresVisited[getX()][getY()] = 1;
            visitedOnce++;
        } else {
            squaresVisited[getX()][getY()]++;
        }
        updateExploredArea();
    }

    public void updateExploredArea() {
        int deltaX[] = new int[]{0, 0, 1, -1, 1, -1, 1, -1};
        int deltaY[] = new int[]{1, -1, 0, 0, 1, 1, -1, -1};
        for (int i = 0; i < deltaX.length; i++) {
            if (explored[deltaX[i] + getX()][deltaY[i] + getY()] == 0) {
                areaExplored++;
                explored[deltaX[i] + getX()][deltaY[i] + getY()] = 1;
            }
        }
    }

    public double getExploredArea() {
        return areaExplored / (map.length * map.length) * 10;
    }

    public int getVisitedSquares() {
        return visitedOnce;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setGoaly(int y) {
        this.goaly = y;
    }

    public void setGoalx(int x) {
        this.goalx = x;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void increaseCoins() {
        coins++;
    }

    public void increaseMines() {
        mines++;
    }

    public int getMines() {
        return mines;
    }

    public void resetCoins() {
        coins = 0;
    }

    public void decreaseHealth(boolean large, boolean stay) {
        if (large) {
            health -= 40;
        } else if (stay) {
            health -= 100;
        } else {
            health -= 5;
        }
    }

    public void increaseHealth() {
        health += 10;
    }

    public int getCoins() {
        return coins;
    }

    public int getHealth() {
        return health;
    }

    public Network getBrain() {
        return brain;
    }

    public void setFitness(double x) {
        fitness = x;
    }

    public void resetFitness() {
        fitness = 0;
    }

    public double getFitness() {
        return fitness;
    }

    public void setMap(char map[][]) {
        this.map = map;
    }

    public void coinsInmap(int x) {
        coinsInmap = x;
    }

    public int coinsInmap() {
        return coinsInmap;
    }

    @Override
    public int compareTo(Agent t) {
        if (t.getFitness() > getFitness()) {
            return 1;
        } else if (t.getFitness() == getFitness()) {
            return 0;
        } else {
            return -1;
        }
    }

    public void addStep() {
        step++;
    }

    public int getSteps() {
        return step;
    }

}
