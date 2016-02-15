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
import org.uncommons.watchmaker.framework.operators.IntArrayCrossover;
import org.uncommons.watchmaker.framework.selection.SigmaScaling;
import org.uncommons.watchmaker.framework.selection.TruncationSelection;
import org.uncommons.watchmaker.framework.termination.Stagnation;

@SuppressWarnings("serial")
class GeneticImage extends JComponent {
    static MersenneTwisterRNG rng = new MersenneTwisterRNG();
    final static int DIMENSIONS = 50;
    static int stagnation = 1000;
    static int keepAfterTruncat = 2;
    static int C = 100;
    static double mutateProb = 0.001;
    static int displayIter = 500;

    /**
     * default setting all round.
     */
    GeneticImage() {
	//startGeneticA();
    }

    /**
     * more options.
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
	//startGeneticA();
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
	//startGeneticA();
    }

    int[] startGeneticA() {
	int[] ints = new int[16777216];
	for (int i = 0; i < ints.length; i++) {
	    ints[i] = i;
	}
	CandidateFactory<int[]> factory = new IntArrayFactory(ints, (int) Math.pow(DIMENSIONS, 2));
	LinkedList<EvolutionaryOperator<int[]>> operators = new LinkedList<EvolutionaryOperator<int[]>>();
	// operators.add(new IntArrayCrossover());
	operators.add(new IntArrayMutation(ints, new Probability(mutateProb)));

	EvolutionaryOperator<int[]> pipeline = new EvolutionPipeline<int[]>(operators);
	FitnessEvaluator<int[]> evaluator = new IntArrayEvaluator();
	SelectionStrategy<Object> strategy = new TruncationSelection((keepAfterTruncat / (double) C));

	EvolutionEngine<int[]> engine = new GenerationalEvolutionEngine<int[]>(factory, pipeline, evaluator, strategy,
		rng);

	engine.addEvolutionObserver(new EvolutionObserver<int[]>() {
	    public void populationUpdate(PopulationData<? extends int[]> data) {
		if (data.getGenerationNumber() % displayIter == 0) {
		    System.out.println("Generation " + data.getGenerationNumber() + " with fitness: "
			    + (int) data.getBestCandidateFitness());
		}
	    }
	});

	int[] imageData = engine.evolve(C, 1, new Stagnation(stagnation, true));
	//saveImage(imageData);
	System.out.println("Finished");
	return imageData;
    }

    public void saveImage(int[] imageData, double name) {
	try {
	    BufferedImage image = new BufferedImage(DIMENSIONS, DIMENSIONS, BufferedImage.TYPE_INT_RGB);
	    image.setRGB(0, 0, DIMENSIONS, DIMENSIONS, imageData, 0, DIMENSIONS);
	    ImageIO.write(image, "png", new File("art "+ (int) name +".png"));
	} catch (IOException e) {
	    System.out.println("---> CAN'T SAVE FILE <---");
	}
    }
}