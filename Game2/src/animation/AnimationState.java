package animation;

public class AnimationState {

	private Animation stateAnimation;
	private String stateName;

	public AnimationState(String name, Animation anim) {

		initState(name, anim);
	}

	private void initState(String name, Animation anim) {
		stateAnimation = anim;
		stateName = name;
	}

	public Animation getAnimaion() {
		return stateAnimation;
	}

	public String getName() {
		return stateName;
	}
}
