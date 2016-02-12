package compGenImage;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

@SuppressWarnings("serial")
class Main extends JComponent {
	static int C = 100; // number of spawn per generation
	static int tol = 20; // how close in color for fitness
	static double minMutateRate = 0.001; // mimimun mutation rate
	static Random rand = new Random();
	final static int HEIGHT = 50;
	final static int WIDTH = 50;
	static int imageHeight;
	static int imageWidth;
	static int[] working;
	static int[] p1;
	static int[] p2;
	static int[][] generation; // TODO implement generation[][]
	static int iter = 0;

	public static void main(String[] args) {
		// setColors();
		readImage("art1900");
		saveImage(working);
		geneticLoop();

	}

	static void geneticLoop() {
		System.out.println("Initializing ARTificial Creativity");
		int oldFitness = 0;
		while (fitness(working) < 19800) {
			double rate = newMutateRate();
			iter++;
			if (iter % 100 == 0) {
				System.out.println((iter - 100) + ": " + "fitness: " + fitness(working) + ", rate: " + rate);
			}
			if (oldFitness + 100 <= fitness(working)) {
				saveImage(working);
				System.out.println("saved image");
				oldFitness = oldFitness + 100;
			}
			int[] bestSpawn = new int[working.length];
			int bestFit = 0;
			for (int i = 0; i < C; i++) {
				int[] spawn = mutate(working, rate);
				int fitness = fitness(spawn);
				if (fitness >= bestFit) {
					bestSpawn = spawn;
					bestFit = fitness;
				}
			}
			// System.out.println(bestFit);
			working = bestFit > fitness(working) ? bestSpawn : working;

		}
		System.out.println(working + ", " + iter);
	}
	
	//Support functions

	public static void setColors() {
		working = new int[WIDTH * HEIGHT];
		p1 = new int [WIDTH * HEIGHT];
		p2 = new int [WIDTH * HEIGHT];
		imageHeight = HEIGHT;
		imageWidth = WIDTH;
		System.out.println("Run");
		int i = 0;
		//BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < imageHeight; y++) {
			for (int x = 0; x < imageWidth; x++) {
				// image.setRGB(x, y, (randomGenerator.nextInt(16777215) + 1));
				int red = rand.nextInt(255) + 1;
				int green = rand.nextInt(255) + 1;
				int blue = rand.nextInt(255) + 1;
				working[i++] = (red << 16) | (green << 8) | blue;
			}
		}
		p1 = working;
		p2 = working;
		//image.setRGB(0, 0, imageWidth, imageHeight, working, 0, imageWidth);
	}

	static void readImage(String file_name) {
		int i = 0;
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(file_name + ".png"));
		} catch (IOException e) {
			System.out.println("---> CAN'T LOAD FILE <---");
			System.exit(0);
		}
		imageHeight = img.getHeight();
		imageWidth = img.getWidth();
		working = new int[imageHeight * imageWidth];
		p1 = new int [imageHeight * imageWidth];
		p2 = new int [imageHeight * imageWidth];
		p1 = working;
		p2 = working;
		//for (int y = 0; y < imageHeight; y++) {
		//	for (int x = 0; x < imageWidth; x++) {
		//		working[i++] = img.getRGB(x, y);
		//	}
		//}

	}

	static void saveImage(int[] imageData) {
		try {
			BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
			// retrieve image
			image.setRGB(0, 0, imageWidth, imageHeight, imageData, 0, imageWidth);
			ImageIO.write(image, "png", new File("art" + fitness(working) + ".png"));
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
		double r = (working.length - (200 * fitness(working))) / (working.length * (1 - minMutateRate));
		if (r < minMutateRate) {
			return minMutateRate;
		} else {
			return r;
			// return 0.5;
		}
	}

	static int[] mutate(int[] imageData, double rate) {
		int[] retVal = new int[imageData.length];
		for (int i = 0; i < imageData.length; i++) {
			retVal[i] = (rand.nextDouble() <= rate) ? (rand.nextInt(16777215) + 1) : imageData[i];
		}
		return retVal;
	}
}