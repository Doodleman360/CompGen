package compGenImage;

import java.awt.Color;
import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

public class IntArrayEvaluator implements FitnessEvaluator<int[]> {
    private final int tol = 20;

    public double getFitness(int[] candidate, List<? extends int[]> population) {
	double fitness = 0;
	int imageWidth = (int) Math.sqrt(candidate.length);
	for (int i = 0; i < candidate.length - 1; i++) {
	    if (i / imageWidth != 1) {
		Color color1 = new Color(candidate[i]);
		Color color2 = new Color(candidate[i + 1]);
		if ((Math.abs(color1.getRed() - color2.getRed()) < tol)
			&& (Math.abs(color1.getGreen() - color2.getGreen()) < tol)
			&& (Math.abs(color1.getBlue() - color2.getBlue()) < tol)
			&& (Math.abs(color1.getRed() - color2.getRed()) > -tol)
			&& (Math.abs(color1.getGreen() - color2.getGreen()) > -tol)
			&& (Math.abs(color1.getBlue() - color2.getBlue()) > -tol)) {
		    fitness++;
		}
	    }
	    if (i < candidate.length - imageWidth) {
		Color color1 = new Color(candidate[i]);
		Color color2 = new Color(candidate[i + imageWidth]);
		if ((Math.abs(color1.getRed() - color2.getRed()) < tol)
			&& (Math.abs(color1.getGreen() - color2.getGreen()) < tol)
			&& (Math.abs(color1.getBlue() - color2.getBlue()) < tol)
			&& (Math.abs(color1.getRed() - color2.getRed()) > -tol)
			&& (Math.abs(color1.getGreen() - color2.getGreen()) > -tol)
			&& (Math.abs(color1.getBlue() - color2.getBlue()) > -tol)) {
		    fitness++;
		}
	    }
	}
	return fitness;
    }

    public boolean isNatural() {
	return true;
    }

}
