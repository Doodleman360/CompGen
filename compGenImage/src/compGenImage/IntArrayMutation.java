package compGenImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

public class IntArrayMutation implements EvolutionaryOperator<int[]> {

    private final int[] alphabet;
    private final NumberGenerator<Probability> mutationProbability;

    IntArrayMutation(int[] alphabet, Probability mutationProbability) {
	this(alphabet, new ConstantGenerator<Probability>(mutationProbability));
    }

    public IntArrayMutation(int[] alphabet, NumberGenerator<Probability> mutationProbability) {
	this.alphabet = alphabet.clone();
	this.mutationProbability = mutationProbability;
    }

    public List<int[]> apply(List<int[]> selectedCandidates, Random rng) {

	List<int[]> mutatedPopulation = new ArrayList<int[]>(selectedCandidates.size());
	for (int[] s : selectedCandidates) {
	    mutatedPopulation.add(mutateIntArray(s, rng));
	}
	return mutatedPopulation;
    }

    private int[] mutateIntArray(int[] array, Random rng) {
	int[] mutatedArray = array.clone();
	for (int i = 0; i < array.length; i++) {
	    if (mutationProbability.nextValue().nextEvent(rng)) {
		mutatedArray[i] = alphabet[rng.nextInt(alphabet.length)];
	    }
	}
	return mutatedArray;
    }

}
