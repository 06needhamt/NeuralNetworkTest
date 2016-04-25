/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetworktest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.DynamicBackPropagation;

/**
 *
 * @author cnich
 */
public class NeuralNetworkTest {
    static final int CHRIS = 0;
    static final int TOM = 1;
    static final int ALEX = 2;
    
    static String trainName = "Classroom Occupation Data.csv";
    static String networkName = "TestNetwork.net";
    static String[] pathToNetwork = { 
        "C:/NeurophLearn/NeuralNetworkTest/", 
        "D:/GitHub/NeuralNetworkTest/", 
        "C:/Users/Borgelman/Documents/GitHub/NeuralNetworkTest/"   
    };
    
    static int pathToUse = TOM;
    
    static NeuralNetwork network;

    static TrainingSet<SupervisedTrainingElement> trainingset;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         
       System.out.println("Start Training");
       //trainNetwork(8, 8, 1); trained at error rate of 0.97 DO NOT RETRAIN!!
       System.out.println("Training Complete");
       
       loadNetwork();
       System.out.println("Network Loaded");
       
       testNetwork();
       //testNetworkAuto();

       System.out.println("\nDone");
        
    }
    static void testNetworkAuto(){
        double total = 0;
        int count = trainingset.elements().size();
        double averageDevience = 0;
        for(int i = 0; i < trainingset.elements().size(); i ++){
            double expected;
            double calculated;
            
            network.setInput(trainingset.elementAt(i).getInput());
            network.calculate();
            calculated = network.getOutput()[0];
            expected = trainingset.elementAt(i).getIdealArray()[0];
            System.out.println("Caculated Output: " + calculated);
            System.out.println("Expected Output: " + expected);
            System.out.println("Devience: " + (calculated - expected));
            averageDevience += Math.abs(Math.abs(calculated) - Math.abs(expected));
            total += network.getOutput()[0]; // we know there is only one output
            
        }
        System.out.println();
        System.out.println("Average: " + total / count);
        System.out.println("Average Devience % : " + (averageDevience / count) * 100);
    }
    static void testNetwork() {
              
        String input = "";
        BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in));
        ArrayList<Double> testValues = new ArrayList<>();
        double[] testValuesDouble;
        
        do { // Keep accepting test strings from user until ENTER on empty input entered
            try {
                System.out.println("Enter test values or \"\": ");
                input = fromKeyboard.readLine();
                
                if(input.equals(""))
                    break;

                input = input.replace(" ", "");

                String[] stringVals = input.split(",");

                testValues.clear(); // Make sure testValues list is empty
                
                for(String val : stringVals) {
                    testValues.add(Double.parseDouble(val));                
                }
            } catch (IOException ex) {
                Logger.getLogger(NeuralNetworkTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NumberFormatException nf) {
                Logger.getLogger(NeuralNetworkTest.class.getName()).log(Level.SEVERE, null, nf);
            }

            testValuesDouble = new double[testValues.size()];

            for(int t = 0; t < testValues.size(); t++) {
                testValuesDouble[t] = testValues.get(t).doubleValue();

            }
 //           Double[] testValuesDouble = testValues.toArray(new Double[testValues.size()]);     

            network.setInput(testValuesDouble);

            network.calculate();  

            printOutput(network.getOutput());

        } while (!input.equals(""));
    }
    
    static void loadNetwork() {
        network = NeuralNetwork.load(pathToNetwork[pathToUse] + networkName); // Load existing network

    }
    
    
    // Training set specifying layer sizes (first input size, last output size)
    static void trainNetwork(int... args) {
        int inputSize, outputSize;
        
        ArrayList<Integer> list = new ArrayList<Integer>();
        for(int layer : args)
            list.add(layer);
        
        inputSize = list.get(0);
        outputSize = list.get(list.size()-1);
        
        NeuralNetwork network = new MultiLayerPerceptron(list, TransferFunctionType.GAUSSIAN);   
        
        trainingset = new TrainingSet<>(inputSize, outputSize); // Set input and output layer sizes
                
        
        trainingset = TrainingSet.createFromFile(pathToNetwork[pathToUse] + trainName, inputSize, outputSize, ","); // Read in training data
        
        //DynamicBackPropagation learningRule = new DynamicBackPropagation();
        BackPropagation learningRule = new BackPropagation();
        network.setLearningRule(learningRule);
        network.learn(trainingset); // train the netowork
        
        network.save(pathToNetwork[pathToUse] + networkName); // Save trained network
    }
    
    
    
    static void printOutput(double[] output) {
        System.out.println();
        System.out.print("{");
        
        for(double out : output) {
            System.out.print(out + ", ");
        }
        
        System.out.println("}");
    }
    
    
}
