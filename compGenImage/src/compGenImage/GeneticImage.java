package compGenImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

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
import org.uncommons.watchmaker.framework.selection.SigmaScaling;
import org.uncommons.watchmaker.framework.selection.TruncationSelection;
import org.uncommons.watchmaker.framework.termination.Stagnation;

@SuppressWarnings("serial")
class GeneticImage extends JComponent {
    static MersenneTwisterRNG rng = new MersenneTwisterRNG();
    final static int DIMENSIONS = 10;
    int imageMult = 2;
    static int stagnation = 10000;
    static int keepAfterTruncat = 5;
    static int C = 500;
    static double mutateProb = 0.02;
    static int displayIter = 500;
    static int lastFitness = 0;

    // Constructors

    /**
     * Default setting all round.
     */
    GeneticImage() {
    }

    /**
     * More options.
     * 
     * @param C
     *            The number of organisms per generation.
     * @param mutateProb
     *            The probability of mutation.
     * @param displayIter
     *            How often to display generation number and fitness.
     */
    GeneticImage(int C, double mutateProb, int displayIter) {
	GeneticImage.C = C;
	GeneticImage.mutateProb = mutateProb;
	GeneticImage.displayIter = displayIter;
    }

    /**
     * Even more options.
     * 
     * @param C
     *            The number of organisms per generation.
     * @param mutateProb
     *            The probability of mutation.
     * @param displayIter
     *            How often to display generation number and fitness.
     * @param keepAfterTruncat
     *            The number of organisms to keep after truncation.
     * @param stagnation
     *            The number of generations after nothing happens, the engine
     *            will end.
     */
    GeneticImage(int C, double mutateProb, int displayIter, int keepAfterTruncat, int stagnation) {
	GeneticImage.C = C;
	GeneticImage.mutateProb = mutateProb;
	GeneticImage.displayIter = displayIter;
	GeneticImage.keepAfterTruncat = keepAfterTruncat;
	GeneticImage.stagnation = stagnation;
    }

    // Main

    @SuppressWarnings("unused")
    int[] startGeneticA() {
	lastFitness = 0;
	int[] ints = new int[16777216];
	for (int i = 0; i < ints.length; i++) {
	    ints[i] = i;
	}
	CandidateFactory<int[]> factory = new IntArrayFactory(ints, (int) Math.pow(DIMENSIONS, 2));
	LinkedList<EvolutionaryOperator<int[]>> operators = new LinkedList<EvolutionaryOperator<int[]>>();
	operators.add(new IntArrayMutation(ints, new Probability(mutateProb)));

	EvolutionaryOperator<int[]> pipeline = new EvolutionPipeline<int[]>(operators);
	FitnessEvaluator<int[]> evaluator = new IntArrayEvaluator();
	FitnessEvaluator<int[]> colorEvaluator = new IntArrayColorEvaluator();
	SelectionStrategy<Object> truncationStrategy = new TruncationSelection((keepAfterTruncat / (double) C));
	SelectionStrategy<Object> sigmaStrategy = new SigmaScaling();

	EvolutionEngine<int[]> engine = new GenerationalEvolutionEngine<int[]>(factory, pipeline, evaluator,
		truncationStrategy, rng);

	engine.addEvolutionObserver(new EvolutionObserver<int[]>() {
	    public void populationUpdate(PopulationData<? extends int[]> data) {
		if (data.getGenerationNumber() % displayIter == 0) {
		    System.out.println("Generation " + data.getGenerationNumber() + " with fitness: "
			    + (int) data.getBestCandidateFitness() + " (+"
			    + ((int) data.getBestCandidateFitness() - lastFitness) + ")");
		    lastFitness = (int) data.getBestCandidateFitness();
		    saveImage(data.getBestCandidate(), "", imageMult);
		    saveImage(data.getBestCandidate(), "1");
		}
	    }
	});

	int[] imageData = engine.evolve(C, 1, new Stagnation(stagnation, true));
	return imageData;
    }

    // Support

    public void saveImage(int[] imageData, String name) {
	try {
	    BufferedImage image = new BufferedImage(DIMENSIONS, DIMENSIONS, BufferedImage.TYPE_INT_RGB);
	    image.setRGB(0, 0, DIMENSIONS, DIMENSIONS, imageData, 0, DIMENSIONS);
	    ImageIO.write(image, "png", new File("art " + name + ".png"));
	} catch (IOException e) {
	    System.out.println("---> CAN'T SAVE FILE <---");
	}
    }

    public void saveImage(int[] imageData, String name, int mult) {
	int[] imageDataMult = new int[(DIMENSIONS * mult) * (DIMENSIONS * mult)];
	int length = (int) Math.sqrt(imageData.length);
	int lengthMult = (int) Math.sqrt(imageDataMult.length);
	System.out.println(imageData.length + " " + imageDataMult.length + " " + length + " " + lengthMult);
	for (int i = 0; i < imageData.length; i++) {
	    for (int y = ((i / length) * mult); y < (((i / length) * mult) + mult); y++) {
		for (int x = wrap(i * mult, DIMENSIONS * mult); x < (wrap(i * mult, DIMENSIONS * mult) + mult); x++) {
		    System.out.println("X: " + x + " Y: " + y + " F(): " + (x + (y * lengthMult)) + " I: " + i);
		    imageDataMult[x + (y * lengthMult)] = imageData[i];
		}
	    }
	}
	try {
	    BufferedImage image = new BufferedImage(DIMENSIONS * mult, DIMENSIONS * mult, BufferedImage.TYPE_INT_RGB);
	    image.setRGB(0, 0, DIMENSIONS * mult, DIMENSIONS * mult, imageDataMult, 0, lengthMult);
	    ImageIO.write(image, "png", new File("art " + name + ".png"));
	} catch (IOException e) {
	    System.out.println("---> CAN'T SAVE FILE <---");
	}
    }
    
    private int wrap(int number, int max) {
	int temp = number;
	while(temp > max) {
	    temp = temp - max;
	}
	return temp;
    }
}