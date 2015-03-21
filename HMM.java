
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by darshanubuntu on 3/17/15.
 * This class HMM contains main method which take 2 command line arguments first one being the model file and second obsevation sequences
 * The input files need to be of the same format as described in the problem statement of homework 4 part 2
 * this class first takes the input files and parses the given model and stores in set of variables.
 * and then reads observation sequence line by line and applies Viterbi algorithm to each sequence
 */
public class HMM {
    public static void main(String[] args) {
        //initializing all the required variable here
        int noOfStates =0;
        double[] initialStateProbabilities;
        double[][] transitionProbabilities;
        int noOfOutputSymbols;
        String outputAlphabets;
        double[][] outputDistributions;
        String observedSequence;
        double[][] computationMatrix;
        int[][] stateMatrix;


        try {

            //reading the Model from the first input file and storing them in relevant variables.
            BufferedReader br = new BufferedReader(new FileReader(args[0]));
            String line;
            try {
                line =br.readLine();
                Scanner scanner = new Scanner(line);
                noOfStates= scanner.nextInt();
                line=br.readLine();
                scanner = new Scanner(line);
                initialStateProbabilities = new double[noOfStates];
                for (int i = 0; i < noOfStates; i++) {
                    initialStateProbabilities[i]=scanner.nextDouble();
                }
                transitionProbabilities = new double[noOfStates][noOfStates];
                line=br.readLine();
                scanner=new Scanner(line);
                for (int i = 0; i < noOfStates; i++) {
                    for (int j = 0; j < noOfStates; j++) {
                        transitionProbabilities[i][j]=scanner.nextDouble();
                    }
                }
                line = br.readLine();
                scanner = new Scanner(line);
                noOfOutputSymbols = scanner.nextInt();
                line = br.readLine();
                outputAlphabets = line.replaceAll("\\s+", "");
                line = br.readLine();
                scanner = new Scanner(line);

                outputDistributions = new double[noOfStates][noOfOutputSymbols];
                for (int i = 0; i < noOfStates; i++) {
                    for (int j = 0; j < noOfOutputSymbols; j++) {
                        outputDistributions[i][j] = scanner.nextDouble();
                    }
                }

                br = new BufferedReader(new FileReader(args[1]));

                //reading the file with observation sequence line by line
                while((line=br.readLine())!=null){
                   String[] input = line.split(" ");
                    observedSequence = line.replaceAll("\\s+","");
                    computationMatrix = new double[noOfStates][observedSequence.length()];
                    stateMatrix = new int[noOfStates][observedSequence.length()];
                    //for each of the observed sequence compute the matrix of probability for each state

                    for (int i = 0; i < observedSequence.length(); i++) {
                        char currentChar = observedSequence.charAt(i);
                        int currentIndex = outputAlphabets.indexOf(currentChar);
                        //treat the first observation differently because the initial probabilities are used to compute
                        if(i==0){
                            for (int j = 0; j < noOfStates; j++) {
                                computationMatrix[j][i] = initialStateProbabilities[j]*outputDistributions[j][currentIndex];
                            }
                        } else {
                            //from the second observation compute probability of each state from all possible previous states and select max value
                            for (int j = 0; j < noOfStates; j++) {
                                double maxProbability = 0.0;
                                int currentMAxIndex = 0;
                                for (int k = 0; k < noOfStates; k++) {
                                    double prob = computationMatrix[k][i-1]*transitionProbabilities[k][j]*outputDistributions[j][currentIndex];
                                    if(prob>maxProbability){
                                        currentMAxIndex = k;
                                        maxProbability = prob;
                                    }
                                }
                                //store the max probability in the matrix and corresponding previous state with the max value in state matrix
                                computationMatrix[j][i] = maxProbability;
                                stateMatrix[j][i] = currentMAxIndex;

                            }

                        }
                    }
                    //once all probabilities are calculated find the final state by checking for highest probability for last observation sybol
                    double maxProbabilityForLastSymbol = 0.0;
                    int state =0;
                    for (int i = 0; i < noOfStates; i++) {
                        if(computationMatrix[i][observedSequence.length()-1] > maxProbabilityForLastSymbol){
                            maxProbabilityForLastSymbol=computationMatrix[i][observedSequence.length()-1];
                            state = i;
                        }
                    }
                    //recursively print all previous states from the final state to give Max Probable Path using Vitebri
                    printStateSequence(stateMatrix,observedSequence.length()-1,state);
                    System.out.println("S"+(state+1));
                }





            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }


    }
    //this function prints out previous states in MPP by calling itself recursively

    private static void printStateSequence(int[][] stateMatrix, int pos, int state) {
        int curState = stateMatrix[state][pos];
    if(pos > 1){
        printStateSequence(stateMatrix,pos-1,curState);

    }
        System.out.print("S"+(curState+1)+" ");

    }
}

