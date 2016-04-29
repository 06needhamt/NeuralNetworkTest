/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetworktest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

/**
 * This class represents our Neural Network
 * @author Da Vinci's Deciples
 */
public class NeuralNetworkTest {
    // These variables represent the path to the project on our machines
    // if you wish to run the project on your machine please insert
    // your path into the array below and assign the relavant index to pathToUse
    
    static final int CHRIS = 0;
    static final int TOM = 1;
    static final int ALEX = 2;
    static String[] pathToNetwork = { 
        "C:/NeurophLearn/NeuralNetworkTest/", 
        "D:/GitHub/NeuralNetworkTest/", 
        "C:/Users/Borgelman/Documents/GitHub/NeuralNetworkTest/"   
    };
    // these variables represent the input and output size of the network
    static int inputSize = 0;
    static int outputSize = 0;
    
    // these variables represent the nsmes of the network and data files
    // to use different data sets insert the name of the file here 
    static String trainName = "Classroom Occupation Data.csv";
    static String testName = "Classroom Occupation Test Data.csv";
    static String networkName = "TestNetwork.net";

    // this variable represents the path index to use
    static int pathToUse = TOM;
    
    // this variable holds the neural network
    static NeuralNetwork network;
    
    // this variable holds the training set
    static TrainingSet<SupervisedTrainingElement> trainingset;
    
    // this variable holds the testing set
    static TrainingSet<SupervisedTrainingElement> testingset;
    
    // this variable holds the network layers
    static int[] layers = {8,8,1}; 
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         
       System.out.println("Start Training");
       //trainNetwork(); trained at error rate of 0.97 DO NOT RETRAIN!!
       System.out.println("Training Complete");
       
       loadNetwork();
       System.out.println("Network Loaded");
       
       //testNetwork(); // test the network from manually inputted data
       testNetworkAuto(testName); // test the network from data from the testing set

       System.out.println("\nDone");
        
    }
    /**
     * Tests the network from a saved data set
     * @param setName the name of the data set
     */
    static void testNetworkAuto(String setName){
        double total = 0;
        ArrayList<Integer> list = new ArrayList<Integer>();
        ArrayList<String> outputLine = new ArrayList<>();
        
        for(int layer : layers)
            list.add(layer);
        
        inputSize = list.get(0);
        outputSize = list.get(list.size()-1);
        testingset = TrainingSet.createFromFile(pathToNetwork[pathToUse] + setName, inputSize, outputSize, ","); // initialise the training set
        int count = testingset.elements().size();
        double averageDevience = 0;
        String resultString = "";
 
        
        try{ // Write the Results to the file
            File file = new File("Results " + setName);
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw); // create file to write results to
            
            for(int i = 0; i < testingset.elements().size(); i ++){ // run each line of data through the network
                double expected;
                double calculated;

                network.setInput(testingset.elementAt(i).getInput()); // set the network input
                network.calculate(); // calculate network weights
                calculated = network.getOutput()[0]; // get the calculated outputs
                expected = testingset.elementAt(i).getIdealArray()[0]; // get the expected outputs
                System.out.println("Caculated Output: " + calculated); // print the calculated outputs
                System.out.println("Expected Output: " + expected); // print the expected outputs
                System.out.println("Devience: " + (calculated - expected));
                averageDevience += Math.abs(Math.abs(calculated) - Math.abs(expected)); //calculate the average devience
                total += network.getOutput()[0]; // we know there is only one output

                resultString = "";

                for(int cols = 0; cols < testingset.elementAt(i).getInputArray().length; cols ++) {
                    resultString += testingset.elementAt(i).getInputArray()[cols] + ", "; // append the inputs to the result
                }

                for(int t = 0; t < network.getOutput().length; t++) {

                    resultString += network.getOutput()[t] + ", "; // append the outputs to the result
                }
                resultString = resultString.substring(0, resultString.length()-2); // Chop off final ", "

                resultString += "\n";
            
                bw.write(resultString);
                bw.flush(); // write and fludh the rresult to the file
            }

            System.out.println();
            System.out.println("Average: " + total / count); // print the average
            System.out.println("Average Devience % : " + (averageDevience / count) * 100); //print the average devience        
            
            bw.flush();
            bw.close(); // flush and close the file
        }
        catch(IOException ex) {
            ex.printStackTrace(); // if an error occurred print the stacktrace
        }
    }
    /**
     * Tests the network with data inputted from the console.
     */
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

                input = input.replace(" ", ""); // remove spaces from the input string

                String[] stringVals = input.split(","); // parse the inputted CSV data

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
                testValuesDouble[t] = testValues.get(t).doubleValue(); // unpack the list of doubles into an array

            }
 //           Double[] testValuesDouble = testValues.toArray(new Double[testValues.size()]);     

            network.setInput(testValuesDouble); // set the network data to the inputted values

            network.calculate();  // calculate the network weights

            printOutput(network.getOutput()); // print the network's output

        } while (!input.equals(""));
    }
    /**
     * Loads the neural network from the file
     */
    static void loadNetwork() {
        network = NeuralNetwork.load(pathToNetwork[pathToUse] + networkName); // Load existing network

    }
    
    
    /**
     * Training set specifying layer sizes (first input size, last output size)
     */ 
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
    
    
    /**
     * Prints the network output
     * @param output the network output
     */
    static void printOutput(double[] output) {
        System.out.println();
        System.out.print("{");
        
        for(double out : output) {
            System.out.print(out + ", "); // delimit values with a ","
        }
        
        System.out.println("}");
    }
    
    
}
