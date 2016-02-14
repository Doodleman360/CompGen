package compGenImage;

import org.uncommons.watchmaker.framework.EvolutionEngine;

@SuppressWarnings("unused")

public class Main {

	public static void main(String[] args) {
		
		EvolutionEngine<int[]> engine;
		
		GeneticImage genI = new GeneticImage(50, 20, 0.001, 0.01, 200, 2);
		genI.geneticLoop();

	}

}
