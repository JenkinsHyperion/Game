package entityComposites;

public class Rigidbody implements EntityComposite{
	
	private static final Null nullSingleton = new Null(null);
	
	private EntityStatic ownerEntity;
	
	private double mass = 1;
	private double friction = 1;
	
	public Rigidbody( EntityStatic ownerEntity ){
		this.ownerEntity = ownerEntity;
	}

	protected static Rigidbody nullSingleton(){
		return nullSingleton;
	}
	
	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public void disableComposite() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EntityStatic getOwnerEntity() {
		return ownerEntity;
	}

	
	public static class Null extends Rigidbody{
		
		public Null(EntityStatic ownerEntity) {
			super(ownerEntity);
		}

		@Override
		public boolean exists() {
			return false;
		}
		
	}

}
