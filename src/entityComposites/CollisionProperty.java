package entityComposites;

import java.awt.Graphics2D;

import engine.Camera;
import entities.EntityStatic;
import physics.Boundary;
import physics.CollisionCheck;
import physics.CollisionEngine;

public abstract class CollisionProperty {

	protected EntityStatic owner;
	
	public void setOwner(EntityStatic entity){ //composite only, set to protected and move to package
		owner = entity;
	}
	// check and pass are both points of termination if either entity in non colliding but is checked for collision
	// this shouldn't happen since collidables are in their own array, so this might be unoptimal
	public void checkForInteractionWith(CollisionProperty entity , CollisionCheck checkType ,CollisionEngine engine) {} 

	public void passInteraction(Collidable entity , CollisionCheck checkType, CollisionEngine engine) {}
	
	public void debugDrawBoundary(Camera camera, Graphics2D g){}
	
	public Boundary getBoundaryLocal(){return null;}

}
