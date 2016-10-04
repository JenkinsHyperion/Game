package entities;

public final class EntityNull extends EntityStatic {
	
	private static EntityStatic nullEntity = new EntityNull();

	private EntityNull() {
		super(0,0);
		
	}
	
	public static EntityStatic nullEntity(){
		return nullEntity;
	}

}
