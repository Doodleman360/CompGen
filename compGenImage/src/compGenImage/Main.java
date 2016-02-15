package compGenImage;

import java.text.DecimalFormat;

import org.uncommons.watchmaker.framework.EvolutionEngine;

@SuppressWarnings("unused")

public class Main {

    public static void main(String[] args) {
	DecimalFormat df = new DecimalFormat("#.###");
	
	for (double i = 1; i >= 0.001; i = Double.valueOf(df.format(i)) - 0.001) {
	    
	    System.out.println("\nSTARTED NEW WITH RATE: " + Double.valueOf(df.format(i)));
	    
	    GeneticImage genI = new GeneticImage(50, Double.valueOf(df.format(i)), 1000, 2, 100);
	    int[] out = genI.startGeneticA();
	    
	    double fitness = IntArrayEvaluator.calcFitness(out);
	    
	    if(fitness > 1000) {
		
		genI.saveImage(out, (int) fitness + " " + i);
		System.out.println("Saved with " + fitness);
		
	    } else {
		
		System.out.println("Rejected with " + fitness);
		
	    }
	}
    }

}