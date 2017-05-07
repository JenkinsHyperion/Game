package sprites;

import java.awt.Point;

import engine.Camera;
import engine.MovingCamera;
import entityComposites.GraphicComposite;

public interface Graphic {

	public void draw( Camera camera,  GraphicComposite composite);
}
