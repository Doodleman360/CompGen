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
import org.uncommons.watchmaker.framework.selection.TruncationSelection;
import org.uncommons.watchmaker.framework.termination.Stagnation;

@SuppressWarnings("serial")
class GeneticImage extends JComponent {
    static Random rand = new Random();
    MersenneTwisterRNG rng = new MersenneTwisterRNG();
    final static int DIMENTIONS = 50;

    /**
     *
     * @param C The number of organisms per generation.
     * @param mutateProb The probability of mutation.
     * @param displayIter How often to display generation number and fitness.
     */
    GeneticImage(int C, double mutateProb, final int displayIter) {

	int[] ints = new int[16777216];
	for (int i = 0; i < ints.length; i++) {
	    ints[i] = i;
	}
	CandidateFactory<int[]> factory = new IntArrayFactory(ints, (int) Math.pow(DIMENTIONS, 2));
	LinkedList<EvolutionaryOperator<int[]>> operators = new LinkedList<EvolutionaryOperator<int[]>>();
	// operators.add(new IntArrayCrossover());
	operators.add(new IntArrayMutation(ints, new Probability(mutateProb)));

	EvolutionaryOperator<int[]> pipeline = new EvolutionPipeline<int[]>(operators);
	FitnessEvaluator<int[]> evaluator = new IntArrayEvaluator();
	SelectionStrategy<Object> strategy = new TruncationSelection(0.1);

	EvolutionEngine<int[]> engine = new GenerationalEvolutionEngine<int[]>(factory, pipeline, evaluator, strategy,
		rng);

	engine.addEvolutionObserver(new EvolutionObserver<int[]>() {
	    public void populationUpdate(PopulationData<? extends int[]> data) {
		if (data.getGenerationNumber() % displayIter == 0) {
		    System.out.println("Generation " + data.getGenerationNumber() + " with fitness: "
			    + data.getBestCandidateFitness());
		}
	    }
	});

	saveImage(engine.evolve(C, 0, new Stagnation(1000, true)));
	System.out.println("Finished");
    }

    static void saveImage(int[] imageData) {
	try {
	    BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	    // retrieve image
	    image.setRGB(0, 0, WIDTH, HEIGHT, imageData, 0, WIDTH);
	    ImageIO.write(image, "png", new File("art.png"));
	} catch (IOException e) {
	    System.out.println("---> CAN'T SAVE FILE <---");
	}
    }
}