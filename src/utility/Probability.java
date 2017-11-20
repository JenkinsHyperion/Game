package utility;

import java.util.Random;

public class Probability {

	public static int randomInt( int min, int max ){

		if ( min >= max ){ //FIXME Make sure errors are not hidden
			return 0;
		}
		else{
			Random temporaryRandom = new Random();
			int returnInt = temporaryRandom.nextInt( max - min );
			temporaryRandom = null;
			return min + returnInt;
		}
	}
	
	public static boolean percentChance( int chance ){
		
		int rand = randomInt(1,100);
		
		return ( rand < chance );
	}
	
}
