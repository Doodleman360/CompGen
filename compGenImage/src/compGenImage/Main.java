package compGenImage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

@SuppressWarnings("serial")
class Main extends JComponent {
	static int C = 100; // number of spawn per generation
	static double minMutateRate = 0.001;
	static Random rand = new Random();
	static int userScore = 1;
	boolean drawn = false;
	final static int HEIGHT = 50;
	final static int WIDTH = 50;
	static int imageHeight;
	static int imageWidth;
	static int[] imageData;
	static int iter = 0;

	public void paint(Graphics g) {
		System.out.println("Run");
		int i = 0;
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT,
				BufferedImage.TYPE_INT_RGB);
		if (!drawn) {
			drawn = true;
			for (int y = 0; y < HEIGHT; y++) {
				for (int x = 0; x < WIDTH; x++) {
					// image.setRGB(x, y, (randomGenerator.nextInt(16777215) +
					// 1));
					int red = rand.nextInt(255) + 1;
					int green = rand.nextInt(255) + 1;
					int blue = rand.nextInt(255) + 1;
					imageData[i++] = (red << 16) | (green << 8) | blue;
				}
			}
		}
		image.setRGB(0, 0, WIDTH, HEIGHT, imageData, 0, WIDTH);
		g.drawImage(image, 0, 0, this);
		Color myColor = new Color(imageData[1]);
		System.out.println(myColor.getRed());
	}

	public static void main(String[] args) {
		// JFrame frame = new JFrame("main");
		// frame.getContentPane().add(new Main());
		// frame.setSize(HEIGHT, WIDTH);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setVisible(true);
		setColors();
		// readImage("Landscape");
		saveImage(imageData);
		startGenetic();

	}

	public static void setColors() {
		imageData = new int[WIDTH * HEIGHT];
		imageHeight = HEIGHT;
		imageWidth = WIDTH;
		System.out.println("Run");
		int i = 0;
		BufferedImage image = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < imageHeight; y++) {
			for (int x = 0; x < imageWidth; x++) {
				// image.setRGB(x, y, (randomGenerator.nextInt(16777215) + 1));
				int red = rand.nextInt(255) + 1;
				int green = rand.nextInt(255) + 1;
				int blue = rand.nextInt(255) + 1;
				imageData[i++] = (red << 16) | (green << 8) | blue;
			}
		}
		image.setRGB(0, 0, imageWidth, imageHeight, imageData, 0, imageWidth);
	}

	static void readImage(String file_name) {
		int i = 0;
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(file_name + ".jpg"));
		} catch (IOException e) {
			System.out.println("---> CAN'T LOAD FILE <---");
			System.exit(0);
		}
		imageHeight = img.getHeight();
		imageWidth = img.getWidth();
		imageData = new int[imageHeight * imageWidth];
		for (int y = 0; y < imageHeight; y++) {
			for (int x = 0; x < imageWidth; x++) {
				imageData[i++] = img.getRGB(x, y);
			}
		}

	}

	static void saveImage(int[] list) {
		try {
			BufferedImage image = new BufferedImage(imageWidth, imageHeight,
					BufferedImage.TYPE_INT_RGB);
			// retrieve image
			image.setRGB(0, 0, imageWidth, imageHeight, list, 0, imageWidth);
			ImageIO.write(image, "png", new File("art" + fitness(imageData) + ".png"));
		} catch (IOException e) {
			System.out.println("---> CAN'T SAVE FILE <---");
		}
	}

	static void startGenetic() {
		System.out.println("Initializing ARTificial Creativity");
		while (userScore < 100) {
			double rate = newMutateRate();
			iter++;
			if (iter % 100 == 0) {
				System.out.println(iter + ": " + "fitness: "
						+ fitness(imageData) + ", rate: " + rate);
				if (iter % 5000 == 0) {
					saveImage(imageData);
					System.out.println("saved image");
				}
			}
			int[] bestSpawn = new int[imageData.length];
			int bestFit = 0;
			for (int i = 0; i < C; i++) {
				int[] spawn = mutate(imageData, rate);
				int fitness = fitness(spawn);
				if (fitness >= bestFit) {
					bestSpawn = spawn;
					bestFit = fitness;
				}
			}
			// System.out.println(bestFit);
			imageData = bestFit > fitness(imageData) ? bestSpawn : imageData;

		}
		System.out.println(imageData + ", " + iter);
	}

	static int fitness(int[] list) {
		int retVal = 0;
		for (int i = 0; i < list.length - 1; i++) {
			if (i / imageWidth != 1) {
				Color color1 = new Color(list[i]);
				Color color2 = new Color(list[i + 1]);
				if ((Math.abs(color1.getRed() - color2.getRed()) < 10)
						&& (Math.abs(color1.getGreen() - color2.getGreen()) < 10)
						&& (Math.abs(color1.getBlue() - color2.getBlue()) < 10)
						&& (Math.abs(color1.getRed() - color2.getRed()) > -10)
						&& (Math.abs(color1.getGreen() - color2.getGreen()) > -10)
						&& (Math.abs(color1.getBlue() - color2.getBlue()) > -10)) {
					retVal++;
				}
			}
			if (i < list.length - imageWidth) {
				Color color1 = new Color(list[i]);
				Color color2 = new Color(list[i + imageWidth]);
				if ((Math.abs(color1.getRed() - color2.getRed()) < 10)
						&& (Math.abs(color1.getGreen() - color2.getGreen()) < 10)
						&& (Math.abs(color1.getBlue() - color2.getBlue()) < 10)
						&& (Math.abs(color1.getRed() - color2.getRed()) > -10)
						&& (Math.abs(color1.getGreen() - color2.getGreen()) > -10)
						&& (Math.abs(color1.getBlue() - color2.getBlue()) > -10)) {
					retVal++;
				}
			}
		}
		return retVal;
	}

	static double newMutateRate() {
		double r = (imageData.length - (350 * fitness(imageData)))/ (imageData.length * (1 - minMutateRate));
		if (r < minMutateRate) {
			return minMutateRate;
		} else {
			return r;
			// return 0.5;
		}
	}

	static int[] mutate(int[] imageData2, double rate) {
		int[] retVal = new int[imageData2.length];
		for (int i = 0; i < imageData2.length; i++) {
			retVal[i] = (rand.nextDouble() <= rate) ? (rand.nextInt(16777215) + 1)
					: imageData2[i];
		}
		return retVal;
	}
}