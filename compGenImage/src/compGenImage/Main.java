package compGenImage;

import java.text.DecimalFormat;

import org.uncommons.watchmaker.framework.EvolutionEngine;

@SuppressWarnings("unused")

public class Main {

    public static void main(String[] args) {
	onceDefault();
    }
    
    private static void onceDefault() {

	    GeneticImage genI = new GeneticImage();
	    int[] out = genI.startGeneticA();

	    double fitness = IntArrayEvaluator.calcFitness(out);

	    genI.saveImage(out, "final", genI.imageMult);
	    System.out.println("finished (" + fitness + ")");
    }
    
    private static void findBest(double start, double stop) {
	double[] best = new double[2];
	
	if(start < stop) {
	    System.out.println("START CANOT BE SMALLER THAN STOP");
	    System.exit(0);
	}

	for (double i = start; i >= stop; i = i - stop) {
	    System.out.println("\nSTARTED WITH: " + i);
	    GeneticImage genI = new GeneticImage(500, i, 1000, 5, 500);
	    int[] out = genI.startGeneticA();

	    double fitness = IntArrayEvaluator.calcFitness(out);

	    genI.saveImage(out, "", genI.imageMult);// (int) fitness + "");
	    System.out.println("finished (" + (fitness - best[0]) + ")");
	    
	    if(best[0] < fitness) {
		best[0] = fitness;
		best[1] = i;
	    }
	}
	System.out.println("Best fitness: " + (int) best[0] + " with rate: " + best[1]);
    }

}