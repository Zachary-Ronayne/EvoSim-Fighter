package sim;

import java.util.Random;

/**
 * A class used to get a random number, but saves which seed it will use for the next number. This saved number 
 * allows many numbers to be generated, that saved number can be loaded and came back to later
 * @author Owner
 */
public class RandomSave{
	
	private long seedCount;
	private long initialSeed;
	private Random rand;
	
	public RandomSave(long initialSeed){
		this.initialSeed = initialSeed;
		rand = new Random(initialSeed);
		seedCount = 0;
	}
	
	/**
	 * @return a values between 0.0 and 1.0, not including 1.0, but including 0.0
	 */
	public double getRand(){
		seedCount++;
		return rand.nextDouble();
	}
	
	public long getSavedSeed(){
		return initialSeed;
	}
	
	public long getSeedCount(){
		return seedCount;
	}
	
	public void setSeedCount(long seedCount){
		this.seedCount = seedCount;
	}
	
}
