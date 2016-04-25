/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetworktest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.encog.engine.data.BasicEngineDataSet;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.DynamicBackPropagation;
import org.encog.engine.data.EngineDataSet;

/**
 *
 * @author cnich
 */
public class NeuralNetworkTest {
    static final int CHRIS = 0;
    static final int TOM = 1;
    static final int ALEX = 2;
    
    static int inputSize = 0;
    static int outputSize = 0;
    
    static String trainName = "Classroom Occupation Data.csv";
    static String testName = "Classroom Occupation Test Data.csv";
    static String networkName = "TestNetwork.net";
    static String[] pathToNetwork = { 
        "C:/NeurophLearn/NeuralNetworkTest/", 
        "D:/GitHub/NeuralNetworkTest/", 
        "C:/Users/Borgelman/Documents/GitHub/NeuralNetworkTest/"   
    };
    
    static int pathToUse = CHRIS;
    
    static NeuralNetwork network;

    static TrainingSet<SupervisedTrainingElement> trainingset;
    static TrainingSet<SupervisedTrainingElement> testingset;
    static int[] layers = {8,8,1}; // network layers
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         
       System.out.println("Start Training");
       //trainNetwork(); trained at error rate of 0.97 DO NOT RETRAIN!!
       System.out.println("Training Complete");
       
       loadNetwork();
       System.out.println("Network Loaded");
       
       //testNetwork();
       testNetworkAuto(testName);

       System.out.println("\nDone");
        
    }
    static void testNetworkAuto(String setName){
        double total = 0;
        ArrayList<Integer> list = new ArrayList<Integer>();
        ArrayList<String> outputLine = new ArrayList<>();
        
        for(int layer : layers)
            list.add(layer);
        
        inputSize = list.get(0);
        outputSize = list.get(list.size()-1);
        testingset = TrainingSet.createFromFile(pathToNetwork[pathToUse] + setName, inputSize, outputSize, ",");
        int count = testingset.elements().size();
        double averageDevience = 0;
        String resultString = "";
 
        
        try{ // Write the Results to the file
            File file = new File("Results " + setName);
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            
            for(int i = 0; i < testingset.elements().size(); i ++){
                double expected;
                double calculated;

                network.setInput(testingset.elementAt(i).getInput());
                network.calculate();
                calculated = network.getOutput()[0];
                expected = testingset.elementAt(i).getIdealArray()[0];
                System.out.println("Caculated Output: " + calculated);
                System.out.println("Expected Output: " + expected);
                System.out.println("Devience: " + (calculated - expected));
                averageDevience += Math.abs(Math.abs(calculated) - Math.abs(expected));
                total += network.getOutput()[0]; // we know there is only one output

                resultString = "";

                for(int cols = 0; cols < testingset.elementAt(i).getInputArray().length; cols ++) {
                    resultString += testingset.elementAt(i).getInputArray()[cols] + ", ";
                }

                for(int t = 0; t < network.getOutput().length; t++) {

                    resultString += network.getOutput()[t] + ", ";
                }
                resultString = resultString.substring(0, resultString.length()-2); // Chop off final ", "

                resultString += "\n";
            
                bw.write(resultString);
                bw.flush();
            }

            System.out.println();
            System.out.println("Average: " + total / count);
            System.out.println("Average Devience % : " + (averageDevience / count) * 100);           
            
            bw.flush();
            bw.close();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
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
    static void trainNetwork() {

        ArrayList<Integer> list = new ArrayList<Integer>();
        for(int layer : layers)
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
