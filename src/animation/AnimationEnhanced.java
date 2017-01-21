package animation;

import java.awt.image.BufferedImage;

public class AnimationEnhanced extends Animation {

	private int dependent = 0;

	public AnimationEnhanced(BufferedImage[] frames, int frameDelay) {

		super(frames, frameDelay);

	}


	/**
	 * 
	 * @param linkedVariable
	 * @param minDelay
	 * @param maxDelay
	 * @param minValue
	 * @param maxValue
	 */
	public void updateSpeed(int linkedVariable, int minValue, int maxValue, int minDelay, int maxDelay) {
		// figure out way to do this better, without forcing enhanced update on
		// Animation
		// also move variable and speed ranges to constructor, not update

		dependent = linkedVariable;

		if (linkedVariable > maxValue) {
			dependent = maxValue;
		} else if (linkedVariable < minValue) {
			dependent = minValue;
		} else { 

			frameDelay = maxDelay - ((linkedVariable - minValue) / maxValue) * (maxDelay - minDelay);
			/*
			 * (linkedVariable - minValue) = linked variable value, zeroed at minimum value
			 * 
			 * (linkedVariable - minValue) / maxValue = percentage of Value range that linked Variable has covered
			 * 
			 * percentage x (maxDelay - minDelay) = percentage of Delay range
			 * 
			 * (maxDelay - ...) inverts the range, since maximum fame delay -> minimum animation playback speed
			 */

		}
		

	}
	
	

}
