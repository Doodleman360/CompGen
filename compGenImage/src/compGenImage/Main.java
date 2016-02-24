package compGenImage;

import java.util.Scanner;

@SuppressWarnings("unused")

public class Main {

    static Scanner scanner;

    public static void main(String[] args) {
	scanner = new Scanner(System.in);

	System.out.println("1: Default\n2: Input all\n3: DM");
	int input = scanner.nextInt();
	if (input == 1) {
	    onceDefault();
	} else if (input == 2) {
	    userInputAll();
	} else if (input == 3) {
	    userInputDM();
	} else {
	    System.out.println("ERROR: Value not expected");
	}
    }

    private static void onceDefault() {
	GeneticImage genI = new GeneticImage();
	int[] out = genI.startGeneticA();

	double fitness = IntArrayEvaluator.calcFitness(out);

	genI.saveImage(out, "final", genI.imageMult);
	System.out.println("finished (" + fitness + ")");
    }

    private static void userInputDM() {
	System.out.println("mutateProb: ");
	double mutateProb = scanner.nextDouble();

	System.out.println("DIMENSIONS: ");
	int DIMENSIONS = scanner.nextInt();

	GeneticImage genI = new GeneticImage(DIMENSIONS, mutateProb);
	int[] out = genI.startGeneticA();

	double fitness = IntArrayEvaluator.calcFitness(out);

	genI.saveImage(out, "final", genI.imageMult);
	System.out.println("finished (" + fitness + ")");

    }

    private static void userInputAll() {
	System.out.println("C: ");
	int C = scanner.nextInt();

	System.out.println("mutateProb: ");
	double mutateProb = scanner.nextDouble();

	System.out.println("displayIter: ");
	int displayIter = scanner.nextInt();

	System.out.println("keepAfterTruncat: ");
	int keepAfterTruncat = scanner.nextInt();

	System.out.println("stagnation: ");
	int stagnation = scanner.nextInt();

	System.out.println("DIMENSIONS: ");
	int DIMENSIONS = scanner.nextInt();

	GeneticImage genI = new GeneticImage(C, mutateProb, displayIter, keepAfterTruncat, stagnation, DIMENSIONS);
	int[] out = genI.startGeneticA();

	double fitness = IntArrayEvaluator.calcFitness(out);

	genI.saveImage(out, "final", genI.imageMult);
	System.out.println("finished (" + fitness + ")");
    }

    private static void findBest(double start, double stop) {
	double[] best = new double[2];

	if (start < stop) {
	    System.out.println("START CANOT BE SMALLER THAN STOP");
	    System.exit(0);
	}

	for (double i = start; i >= stop; i = i - stop) {
	    System.out.println("\nSTARTED WITH: " + i);
	    GeneticImage genI = new GeneticImage(500, i, 1000, 5, 500, 1000);
	    int[] out = genI.startGeneticA();

	    double fitness = IntArrayEvaluator.calcFitness(out);

	    genI.saveImage(out, "", genI.imageMult);// (int) fitness + "");
	    System.out.println("finished (" + (fitness - best[0]) + ")");

	    if (best[0] < fitness) {
		best[0] = fitness;
		best[1] = i;
	    }
	}
	System.out.println("Best fitness: " + (int) best[0] + " with rate: " + best[1]);
    }

}