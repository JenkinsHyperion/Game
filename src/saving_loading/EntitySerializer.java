package saving_loading;

import entityComposites.*;
import physics.*;
import sprites.*;

public class EntitySerializer {
	
	//BULK OF ENTITYDATA CLASS CONSTRUCTION
	protected static EntityData Serialize( EntityStatic entity ){
		
		boolean isDynamic = false;
		//COLLIDER DATA
		ColliderData colliderData = null; //better way of 
		GraphicData graphicData = null;
		
		if ( entity.getColliderComposite().exists() ){	
			
			Boundary boundary = entity.getColliderComposite().getBoundary();
			if ( boundary instanceof BoundaryPolygonal ){
				colliderData = new ColliderData( entity.getColliderComposite().getBoundary().getCornersVertex() );
				System.out.println("     Saved "+boundary+ " of "+entity);
			}//else if ( boundary instanceof BoundaryCircular ){
				
			//}
			else{
				System.err.println("     Couldn't save "+boundary+ " of "+entity);
			}
			
		}
		
		//GRAPHICS DATA
		
		if ( entity.getGraphicComposite() instanceof GraphicComposite ){
			Sprite sprite = entity.getGraphicComposite().getSprite();
			if ( sprite instanceof Sprite.Stillframe ){
				Sprite.Stillframe stillframe = (Sprite.Stillframe)sprite;
				graphicData= GraphicData.createStillFrameData( stillframe.getPathName() , stillframe.getOffsetX() , stillframe.getOffsetY() );
				System.out.println("     Saved "+stillframe+ " of "+entity+ " with path "+stillframe.getPathName());
			}
			else if ( sprite instanceof SpriteAnimated){
				SpriteAnimated anim = (SpriteAnimated) sprite;
				graphicData= GraphicData.createAnimationData( anim.getPathName() , anim.getOffsetX() , anim.getOffsetY(), 
						anim.getFrameCount(), anim.getRow(), anim.getFrameWidth(), anim.getFrameHeight(), anim.getDelay() );
				System.out.println("     Saved "+anim+ " of "+entity+ " with path "+anim.getPathName());
			}else{
				System.err.println("     Failed to save "+sprite+ " of "+entity);
			}
		}
		
		if ( entity.getTranslationComposite().exists() ){
			isDynamic = true;
		}
		
		//-----------------
		EntityData finalData = new EntityData(
				isDynamic,
				entity.getPosition(),
				colliderData
				);
		finalData.setGraphicData(graphicData);
		
		return finalData;
		
	}
	
}
