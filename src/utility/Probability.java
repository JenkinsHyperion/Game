package utility;

import java.util.Random;

public class Probability {

	public static int randomInt( int min, int max ){

		Random temporaryRandom = new Random();
		int returnInt = temporaryRandom.nextInt( max - min );
		temporaryRandom = null;
		return min + returnInt;
	}
}
