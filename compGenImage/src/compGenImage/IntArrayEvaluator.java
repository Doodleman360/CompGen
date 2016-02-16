package compGenImage;

import java.awt.Color;
import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

public class IntArrayEvaluator implements FitnessEvaluator<int[]> {
    private final static int TOL = 20;

    public double getFitness(int[] candidate, List<? extends int[]> population) {
	return calcFitness(candidate);
    }

    public static double calcFitness(int[] candidate) {
	double fitness = 0;
	int imageWidth = (int) Math.sqrt(candidate.length);
	for (int i = 0; i < candidate.length - 1; i++) {
	    if (i / imageWidth != 1) {
		if (compare(candidate, i, i + 1)) {
		    fitness++;
		    if(i + 1  < candidate.length - imageWidth) {
			if(compare(candidate, i, i + imageWidth + 1)) {
				fitness++;
		    	}
		    }
		}
	    }
	    if (i < candidate.length - imageWidth) {
		if (compare(candidate, i, i+imageWidth)) {
		    fitness++;
		}
	    }
	}
	return fitness;
    }

    private static boolean compare(int[] candidate, int index1, int index2) {
	Color color1 = new Color(candidate[index1]);
	Color color2 = new Color(candidate[index2]);
	if ((Math.abs(color1.getRed() - color2.getRed()) < TOL)
		&& (Math.abs(color1.getGreen() - color2.getGreen()) < TOL)
		&& (Math.abs(color1.getBlue() - color2.getBlue()) < TOL)
		&& (Math.abs(color1.getRed() - color2.getRed()) > -TOL)
		&& (Math.abs(color1.getGreen() - color2.getGreen()) > -TOL)
		&& (Math.abs(color1.getBlue() - color2.getBlue()) > -TOL)) {
	    return true;
	} else {
	    return false;
	}
    }

    public boolean isNatural() {
	return true;
    }

}
