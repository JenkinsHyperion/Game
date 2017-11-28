package utility;

import java.util.Random;

public class Benchmarker {

	private static final int[] checks = new int[100000];
	
	private static long startTime;
	
	public static void main(String[] args) {
		
		populateArray();
		
		perform1();
		perform2();
	}
	
	private static void populateArray(){
		for (int i = 0 ; i < checks.length ; ++i){
			Random rand = new Random();
			checks[i] = rand.nextInt();
		}
		System.out.println("List populated");
	}
	
	private static void perform1(){
		startTime = System.currentTimeMillis();
		for (int i = 1 ; i < checks.length ; ++i){
			
			if ( checks[i] > checks[i-1] ){
				//System.out.println(i+" true");
			}
			else{
				//System.out.println(i+" false");
			}
			
		}
		long finishTime = System.currentTimeMillis();
		System.out.println( "op1 "+(finishTime - startTime) );
	}
	
	private static void perform2(){
		startTime = System.currentTimeMillis();
		for (int i = 1 ; i < checks.length ; ++i){
			
			if ( checks[i] > checks[i-1] ){
				System.out.println(i+" true");
			}
			else{
				System.out.println(i+" false");
			}
			
		}
		long finishTime = System.currentTimeMillis();
		System.out.println( "op2 "+(finishTime - startTime) );
	}
	
}
