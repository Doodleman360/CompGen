package compGenImage;

import java.util.Random;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

public class IntArrayFactory extends AbstractCandidateFactory<int[]> {

    private final int[] alphabet;
    private final int length;

    public IntArrayFactory(int[] alphabet, int length) {
	this.alphabet = alphabet.clone();
	this.length = length;

    }

    public int[] generateRandomCandidate(Random rng) {
	int[] chars = new int[length];
	for (int i = 0; i < length; i++) {
	    chars[i] = alphabet[rng.nextInt(alphabet.length)];
	}
	return chars;
    }

}
