package utility;

import engine.BoardAbstract;

public class PerlinNoiseGenerator {
	
	public static void main(String[] args) {
		
		int[] noiseTest = perlinNoise(100,5,100);

		for( int i = 0 ; i < noiseTest.length ; ++i ){
		    for( int y = 0 ; y < noiseTest[i] ; ++y ){
		    	System.out.print("|");
		    }
		    System.out.println(">");
		}
	}
	
	public static int[] perlinNoise(int domain, int wavelength, int range ){
		
		int[] returnArray = new int[domain];
		int x = 0;
		double page1 = BoardAbstract.randomInt(0, range);
		double page2 = BoardAbstract.randomInt(0, range);
		
		while(x < domain){
			
		    if(x % wavelength == 0){ //keyPoints
		        page1 = page2;
		        page2 = BoardAbstract.randomInt(0, range);
		        returnArray[x] = (int)page1;

		        
		    }else{ //interpolated Points
		    	returnArray[x] = cosineInterpolate(page1,page2,( (x % wavelength) / (double)wavelength ));
		    }

		    x += 1;
		}
		
		return returnArray;
	}
	
	
	private static int cosineInterpolate( double minBound, double maxBound, double xBetween ){
		
		return (int) ( 
				
				minBound + // shifts 
				
				( 1 - Math.cos(xBetween*Math.PI) ) / 2.0 *	// [1-cosine(x)] / 2 is modified cosine function with domain and range both 0 to 1
				
				( maxBound - minBound )
				
		);

	}
}
