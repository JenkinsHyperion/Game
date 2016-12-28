package animation;

public class EntityState {

	private Animation stateAnimation;
	private String stateName;

	public EntityState(String name, Animation anim) {

		initState(name, anim);
	}

	private void initState(String name, Animation anim) {
		stateAnimation = anim;
		stateName = name;
	}

	public Animation getAnimation() {
		return stateAnimation;
	}

	public String getName() {
		return stateName;
	}
}
