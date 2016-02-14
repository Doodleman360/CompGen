package compGenImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.IntArrayCrossover;
import org.uncommons.watchmaker.framework.selection.SigmaScaling;
import org.uncommons.watchmaker.framework.termination.Stagnation;

@SuppressWarnings("serial")
class GeneticImage extends JComponent {
    // twekable
    static int C = 50; // number of spawn per generation
    static int crossNumber = 2;
    // nonTwekable
    static Random rand = new Random();
    MersenneTwisterRNG rng = new MersenneTwisterRNG();
    final static int HEIGHT = 50;
    final static int WIDTH = 50;

    GeneticImage() {

	int[] ints = new int[16777216];
	for (int i = 0; i < ints.length; i++) {
	    ints[i] = i;
	}
	CandidateFactory<int[]> factory = new IntArrayFactory(ints, 2500);
	LinkedList<EvolutionaryOperator<int[]>> operators = new LinkedList<EvolutionaryOperator<int[]>>();
	//operators.add(new IntArrayCrossover());
	operators.add(new IntArrayMutation(ints, new Probability(0.02)));

	EvolutionaryOperator<int[]> pipeline = new EvolutionPipeline<int[]>(operators);
	FitnessEvaluator<int[]> evaluator = new IntArrayEvaluator();
	SelectionStrategy<Object> strategy = new SigmaScaling();

	EvolutionEngine<int[]> engine = new GenerationalEvolutionEngine<int[]>(factory, pipeline, evaluator, strategy,
		rng);

	engine.addEvolutionObserver(new EvolutionObserver<int[]>() {
	    public void populationUpdate(PopulationData<? extends int[]> data) {
		if(data.getGenerationNumber() % 50 == 0) {
		    System.out.println("Generation " + data.getGenerationNumber() + " with fitness: " + data.getBestCandidateFitness());
		}
		
	    }
	});

	saveImage(engine.evolve(C, 0, new Stagnation(1000, true)), 0);
	System.out.println("Finished");
    }

    // Support functions

    static void saveImage(int[] imageData, int name) {
	try {
	    BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	    // retrieve image
	    image.setRGB(0, 0, WIDTH, HEIGHT, imageData, 0, WIDTH);
	    ImageIO.write(image, "png", new File("art" + name + ".png"));
	} catch (IOException e) {
	    System.out.println("---> CAN'T SAVE FILE <---");
	}
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