package compGenImage;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
    static int DIMENSIONS = 20;
    int imageMult = 10;
    static int stagnation = 1000;
    static int keepAfterTruncat = 5;
    static int C = 500;
    static double mutateProb = 0.01;
    static int displayIter = 1000;
    static int lastFitness = 0;

    // Constructors

    /**
     * Default setting all round.
     */
    GeneticImage() {
    }

    /**
     * just dimension and mutateProb.
     * 
     * @param DIMENSIONS
     * @param mutateProb
     *            The probability of mutation.
     */
    GeneticImage(int DIMENSIONS, double mutateProb) {
	GeneticImage.mutateProb = mutateProb;
	GeneticImage.DIMENSIONS = DIMENSIONS;
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
    GeneticImage(int C, double mutateProb, int displayIter, int keepAfterTruncat, int stagnation, int DIMENSIONS) {
	GeneticImage.C = C;
	GeneticImage.mutateProb = mutateProb;
	GeneticImage.displayIter = displayIter;
	GeneticImage.keepAfterTruncat = keepAfterTruncat;
	GeneticImage.stagnation = stagnation;
	GeneticImage.DIMENSIONS = DIMENSIONS;
    }

    // Main

    @SuppressWarnings("unused")
    int[] startGeneticA() {
	lastFitness = 0;
	int[] intsColor = new int[16777216];
	for (int i = 0; i < intsColor.length; i++) {
	    intsColor[i] = i;
	}
	int[] intsGray = new int[255];
	for (int i = 0; i < intsGray.length; i++) {
	    intsGray[i] = (i << 16) | (i << 8) | i;
	}
	CandidateFactory<int[]> factory = new IntArrayFactory(intsGray, (int) Math.pow(DIMENSIONS, 2));
	LinkedList<EvolutionaryOperator<int[]>> operators = new LinkedList<EvolutionaryOperator<int[]>>();
	operators.add(new IntArrayMutation(intsGray, new Probability(mutateProb)));

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
		    saveImage(data.getBestCandidate(), "small");
		    saveImage(data.getBestCandidate(), "mult", imageMult);
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
	int newDimensions = DIMENSIONS * mult;
	try {
	    BufferedImage image = new BufferedImage(DIMENSIONS, DIMENSIONS, BufferedImage.TYPE_INT_RGB);
	    image.setRGB(0, 0, DIMENSIONS, DIMENSIONS, imageData, 0, DIMENSIONS);
	    ImageIO.write(getScaledImage(image, newDimensions, newDimensions), "png", new File("art " + name + ".png"));
	} catch (IOException e) {
	    System.out.println("---> CAN'T SAVE FILE <---");
	}
    }

    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * 
     * @param srcImg
     *            - source image to scale
     * @param w
     *            - desired width
     * @param h
     *            - desired height
     * @return - the new resized image
     */
    private BufferedImage getScaledImage(BufferedImage src, int w, int h) {
	int finalw = w;
	int finalh = h;
	double factor = 1.0d;
	if (src.getWidth() > src.getHeight()) {
	    factor = ((double) src.getHeight() / (double) src.getWidth());
	    finalh = (int) (finalw * factor);
	} else {
	    factor = ((double) src.getWidth() / (double) src.getHeight());
	    finalw = (int) (finalh * factor);
	}

	BufferedImage resizedImg = new BufferedImage(finalw, finalh, BufferedImage.TRANSLUCENT);
	Graphics2D g2 = resizedImg.createGraphics();
	g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
	g2.drawImage(src, 0, 0, finalw, finalh, null);
	g2.dispose();
	return resizedImg;
    }

}