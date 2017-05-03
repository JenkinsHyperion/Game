package animation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

//Animation class holds array of individual frames as well as playback functionality
public class Animation {
	
	//protected int animationOffsetX = 0;
	//protected int animationOffsetY = 0;

	private int frameCount; // Counts ticks for change
	protected int frameDelay; // frame delay 1-12 (You will have to play around
								// with this)
	private int currentFrame; // animations current frame
	private int animationDirection; // animation direction (i.e counting forward
									// or backward)
	private int totalFrames; // total amount of frames for your animation

	private boolean stopped; // has animations stopped

	private List<Frame> frames = new ArrayList<Frame>(); // Arraylist of frames

	public Animation(BufferedImage[] frames, int frameDelay) {
		this.frameDelay = frameDelay;
		this.stopped = true;

		for (int i = 0; i < frames.length; i++) {
			addFrame(frames[i], frameDelay);
		}
		
		this.frameCount = 0;
		this.frameDelay = frameDelay;
		this.currentFrame = 0;
		this.animationDirection = 1;
		this.totalFrames = this.frames.size();

	}

	public static Animation animationFromGif( String path , int frameDelay ){
		return new Animation( AnimatedGifReader.convertGifToFrames(path) , frameDelay );
	}

	public void setReverse(){
		this.animationDirection = -1;
	}
	
	public void start() {
		if (!stopped) {
			return;
		}

		if (frames.size() == 0) {
			return;
		}

		stopped = false;
	}
	
	//possibly move to enhanced animation
	public void setFrame(int startFrame) {
		if (!stopped) {
			return;
		}

		if (frames.size() == 0) {
			return;
		}

		currentFrame = startFrame;
	}
	
	public int getFrameNumber(){
		return currentFrame;
	}

	public void stop() {
		if (frames.size() == 0) {
			return;
		}

		stopped = true;
	}

	public void restart() { // revert frame to 0
		if (frames.size() == 0) {
			return;
		}

		stopped = false;
		currentFrame = 0;
	}

	public void reset() { // stop and revert frame to 0
		this.stopped = true;
		this.frameCount = 0;
		this.currentFrame = 0;
	}

	private void addFrame(BufferedImage frame, int duration) {
		if (duration <= 0) {
			System.err.println("Invalid duration: " + duration);
			throw new RuntimeException("Invalid duration: " + duration);
		}

		frames.add(new Frame(frame, duration));
		currentFrame = 0;
	}

	public BufferedImage getAnimationFrame() {
		return frames.get(currentFrame).getFrame();
	}

	public void update() {
		
		if (!stopped) {
			frameCount++;

			if (frameCount > frameDelay) {
				frameCount = 0;
				currentFrame += animationDirection;

				if (currentFrame > totalFrames - 1) {
					currentFrame = 0;
				} else if (currentFrame < 0) {
					currentFrame = totalFrames - 1;
				}
			}
		}


	}
	
	public int getFrameCount(){return this.totalFrames; }
	public int getDelay(){return this.frameDelay; }
	public int getDirection(){return this.animationDirection; }
	

}