package saveGame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class SaveGame {


    public void saveWeights(double weights[][], String file) throws IOException {
        PrintWriter w = new PrintWriter(file);
        String input = "";
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                if (j == weights[i].length) {
                    input += weights[i][j];
                } else {
                    input += weights[i][j] + ",";
                }
            }
            input += "\n";
        }
        w.println(input);
        w.close();
    }
    
    public void loadWeights(String file) throws FileNotFoundException{
        Scanner sc = new Scanner(new File("/home/aurinkoinen/NetBeansProjects/NeuralNetworkLibary/src/saveGame/w/"));
        while(sc.hasNextLine()){
            System.out.println(sc.nextLine());
        }
    }
}
