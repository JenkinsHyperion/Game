package misc;

public class EntityState {

	private String stateName;

	public EntityState(String name) {

		initState(name);
	}

	private void initState(String name) {
		stateName = name;
	}

	public String getName() {
		return stateName;
	}
}
