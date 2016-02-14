package compGenImage;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionEngine;

@SuppressWarnings("serial")
class GeneticImage extends JComponent {
	// twekable
	static int C = 50; // number of spawn per generation
	static int tol = 20; // how close in color for fitness
	static double minMutateRate = 0.001; // mimimun mutation rate
	static double maxMutateRate = 0.01;
	static int fitnessToMinRate = 200;
	static int crossNumber = 2;
	// nonTwekable
	static Random rand = new Random();
	final static int HEIGHT = 50;
	final static int WIDTH = 50;
	static int imageHeight;
	static int imageWidth;
	static int[] working;
	static int[][] generation;
	static int iter = 0;

	GeneticImage(int C, int tol, double minMutateRate, double maxMutateRate, int fitnessToMinRate, int crossNumber) {
		GeneticImage.C = C;
		GeneticImage.tol = tol;
		GeneticImage.minMutateRate = minMutateRate;
		GeneticImage.maxMutateRate = maxMutateRate;
		GeneticImage.fitnessToMinRate = fitnessToMinRate;
		GeneticImage.crossNumber = crossNumber;
		
		int[] ints = new int[16777216];
		for (int i = 0; i < ints.length;i++) {
			ints[i] = i;
		}
		CandidateFactory<int[]> factory = new IntArrayFactory(ints , 2500);
		EvolutionEngine<int[]> engine;
		
		setColors();
		// readImage("art1900");
		saveImage(working, 0);
		System.out.println(iter + ": saved image");
		//geneticLoop();
	}


	void geneticLoop() {
		System.out.println("Initializing ARTificial Creativity");
		int saveFitness = 0;
		int disFitness = 0;
		int[] p1, p2;
		p1 = working;
		p2 = working;
		generation = new int[C + 2][working.length];
		while (fitness(working) < 19800) {
			double rate = newMutateRate();
			iter++;
			// if (iter % 100 == 0) {
			// System.out.println((iter) + ": " + "fitness: " + fitness(working)
			// + ", rate: " + rate);
			// }
			if (disFitness + 10 <= fitness(working)) {
				System.out.println((iter) + ": " + "fitness: " + fitness(working) + ", rate: " + rate);
				disFitness = disFitness + 10;
			}
			if (saveFitness + 500 <= fitness(working)) {
				saveFitness = saveFitness + 500;
				saveImage(working, saveFitness);
				System.out.println(iter + ": saved image");
			}

			generation[0] = p1;
			generation[1] = p2;
			int bestFit = fitness(p1);
			for (int i = 2; i < C + 2; i++) {
				generation[i] = mutate(crossover(p1, p2), rate);
				if (fitness(generation[i]) > bestFit && fitness(generation[i]) > fitness(working)) {
					bestFit = fitness(generation[i]);
					p2 = p1;
					p1 = generation[i];
				}
			}
			working = bestFit > fitness(working) ? p1 : working;
		}
		System.out.println(working + ", " + iter);
	}

	// Support functions

	public static void setColors() {
		working = new int[WIDTH * HEIGHT];
		imageHeight = HEIGHT;
		imageWidth = WIDTH;
		System.out.println("Run");
		int i = 0;
		for (int y = 0; y < imageHeight; y++) {
			for (int x = 0; x < imageWidth; x++) {
				int red = rand.nextInt(255) + 1;
				int green = rand.nextInt(255) + 1;
				int blue = rand.nextInt(255) + 1;
				working[i++] = (red << 16) | (green << 8) | blue;
			}
		}
	}

	static void readImage(String file_name) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(file_name + ".png"));
		} catch (IOException e) {
			System.out.println("---> CAN'T LOAD FILE <---");
			System.exit(0);
		}
		imageHeight = img.getHeight();
		imageWidth = img.getWidth();
	}

	static void saveImage(int[] imageData, int name) {
		try {
			BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
			// retrieve image
			image.setRGB(0, 0, imageWidth, imageHeight, imageData, 0, imageWidth);
			ImageIO.write(image, "png", new File("art" + name + ".png"));
		} catch (IOException e) {
			System.out.println("---> CAN'T SAVE FILE <---");
		}
	}

	static int fitness(int[] list) {
		int retVal = 0;
		for (int i = 0; i < list.length - 1; i++) {
			if (i / imageWidth != 1) {
				Color color1 = new Color(list[i]);
				Color color2 = new Color(list[i + 1]);
				if ((Math.abs(color1.getRed() - color2.getRed()) < tol)
						&& (Math.abs(color1.getGreen() - color2.getGreen()) < tol)
						&& (Math.abs(color1.getBlue() - color2.getBlue()) < tol)
						&& (Math.abs(color1.getRed() - color2.getRed()) > -tol)
						&& (Math.abs(color1.getGreen() - color2.getGreen()) > -tol)
						&& (Math.abs(color1.getBlue() - color2.getBlue()) > -tol)) {
					retVal++;
				}
			}
			if (i < list.length - imageWidth) {
				Color color1 = new Color(list[i]);
				Color color2 = new Color(list[i + imageWidth]);
				if ((Math.abs(color1.getRed() - color2.getRed()) < tol)
						&& (Math.abs(color1.getGreen() - color2.getGreen()) < tol)
						&& (Math.abs(color1.getBlue() - color2.getBlue()) < tol)
						&& (Math.abs(color1.getRed() - color2.getRed()) > -tol)
						&& (Math.abs(color1.getGreen() - color2.getGreen()) > -tol)
						&& (Math.abs(color1.getBlue() - color2.getBlue()) > -tol)) {
					retVal++;
				}
			}
		}
		return retVal;
	}

	static double newMutateRate() {
		double r = map(fitnessToMinRate, 0, minMutateRate, maxMutateRate, fitness(working));
		if (r < minMutateRate) {
			return minMutateRate;
		} else {
			return r;
		}
	}

	static int[] mutate(int[] imageData, double rate) {
		int[] retVal = new int[imageData.length];
		for (int i = 0; i < imageData.length; i++) {
			retVal[i] = (rand.nextDouble() <= rate) ? (rand.nextInt(16777215) + 1) : imageData[i];
		}
		return retVal;
	}

	static int[] crossover(int[] parent1, int[] parent2) {
		int cN = rand.nextInt(crossNumber);
		int[] child = parent1;
		for (int i = 0; i < cN; i++) {
			int mult = i * child.length / cN;
			for (int j = 0; j < child.length / cN; j++) {
				if (i % 2 == 0) {
					child[j + mult] = parent2[j + mult];
				}
			}
		}
		return child;
	}

	static double map(double oldMin, double oldMax, double newMin, double newMax, double oldValue) {
		double oldRange = (oldMax - oldMin);
		double newRange = (newMax - newMin);
		double newValue = (((oldValue - oldMin) * newRange) / oldRange) + newMin;
		return newValue;
	}
}