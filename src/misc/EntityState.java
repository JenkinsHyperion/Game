package misc;

import sprites.Sprite;
import sprites.SpriteAnimated;

public class EntityState {

	private Sprite stateSpriteRight;
	private Sprite stateSpriteLeft;
	private String stateName;

	public EntityState(String name, Sprite spriteRight , Sprite spriteLeft) {

		initState(name, spriteRight, spriteLeft);
	}

	private void initState(String name, Sprite right, Sprite left) {
		stateSpriteRight = right;
		stateName = name;
	}
	
	public Sprite getSpriteRight() {
		return stateSpriteRight;
	}
	
	public Sprite getSpriteLeft() {
		return stateSpriteLeft;
	}

	public String getName() {
		return stateName;
	}
}
