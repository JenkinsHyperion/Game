package entities;

public class EntityAssembler {

	private Platform01 builder;
	
	
	public EntityAssembler( Platform01 build ){
		
		this.builder = build;	
	}
	
	public void assembleEntity(){
		
		this.builder.buildCollision();
		this.builder.buildSprite();
		
	}
	
	public EntityStatic getEntity(){
		EntityStatic returnEntity = new EntityStatic(0, 0);
		
		return returnEntity;
	}
	
}
