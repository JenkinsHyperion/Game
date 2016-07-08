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
		} else { // for clarity assume linkedVariable = player running speed dx

			frameDelay = maxDelay - ((linkedVariable - minValue) / maxValue) * (maxDelay - minDelay);
			/*
			 * |dx, zeroed at minimum value| |animation speed range|
			 * 
			 * |dx as a percentage of the input value range| * by animation
			 * speed range
			 * 
			 * gives animation speed % within set range, based on input variable
			 * % within set input range.
			 * 
			 * The (maxDelay - ...) just inverts the range, since maximum fame
			 * delay is actually minimum animation playback speed
			 */

		}

	}

}
