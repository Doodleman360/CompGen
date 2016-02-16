package compGenImage;

import java.awt.Color;
import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

public class IntArrayColorEvaluator implements FitnessEvaluator<int[]> {

    public double getFitness(int[] candidate, List<? extends int[]> population) {
	return calcFitness(candidate);
    }

    public static double calcFitness(int[] candidate) {
	double fitness = 0;
	for (int i = 0; i < candidate.length; i++) {
	    Color color = new Color(candidate[i]);
	    fitness = fitness + (color.getGreen() + color.getRed() + color.getBlue());
	}
	return fitness;
    }

    public boolean isNatural() {
	return false;
    }

}
