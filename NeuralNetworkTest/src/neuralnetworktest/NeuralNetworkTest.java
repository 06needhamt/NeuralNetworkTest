/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetworktest;

import java.util.ArrayList;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

/**
 *
 * @author cnich
 */
public class NeuralNetworkTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
                       
//       train(4, 8);
       
        //NeuralNetwork network = NeuralNetwork.load("c:/NeurophLearn/TestNetwork.net"); chris path
        NeuralNetwork network = NeuralNetwork.load("D:/GitHub/NeuralNetworkTest/NeuralNetworkTest/TestNetwork.net"); // toms path
        network.setInput(1, 0, 1, 1);
        
        network.calculate();
        
        printOutput(network.getOutput());

        System.out.println("\nDone");
        
    }
    
    // Training set specifying layer sizes (first input size, last output size)
    static void train(int... args) {
        int inputSize, outputSize;
        
        ArrayList<Integer> list = new ArrayList<Integer>();
        for(int layer : args)
            list.add(layer);
        
        inputSize = list.get(0);
        outputSize = list.get(list.size()-1);
        
        NeuralNetwork network = new MultiLayerPerceptron(list, TransferFunctionType.SIGMOID);   
        
        TrainingSet<SupervisedTrainingElement> trainingset = new TrainingSet<>(inputSize, outputSize); // Set input and output layer sizes
                
        //trainingset = TrainingSet.createFromFile("C:/NeurophLearn/TrainingSet.csv", inputSize, outputSize, ","); //chris path
        trainingset = TrainingSet.createFromFile("D:/GitHub/NeuralNetworkTest/NeuralNetworkTest/TrainingSet.csv", inputSize, outputSize, ","); // toms path
        network.learn(trainingset);
        
        //network.save("c:/NeurophLearn/TestNetwork.net"); // chris path
        network.save("D:/GitHub/NeuralNetworkTest/NeuralNetworkTest/TestNetwork.net"); // toms path
    }
    
    
    
    static void printOutput(double[] output) {
        System.out.println();
        System.out.print("{");
        
        for(double out : output) {
            System.out.print(Math.round(out) + " ");
        }
        
        System.out.println("}");
    }
    
    
}
