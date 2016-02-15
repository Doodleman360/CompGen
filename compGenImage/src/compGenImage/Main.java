package compGenImage;

import org.uncommons.watchmaker.framework.EvolutionEngine;

@SuppressWarnings("unused")

public class Main {

    public static void main(String[] args) {
	GeneticImage genI = new GeneticImage();
	int[] out = genI.startGeneticA();
	genI.saveImage(out, IntArrayEvaluator.calcFitness(out));
    }

}