package misc;

import java.io.Console;

public class NullCollisionEvent extends CollisionEvent {

	@Override
	public void run() {
		System.out.println("Leaving Collision");
	}
	
}
