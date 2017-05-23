package sprites;

import java.awt.Point;

import engine.ReferenceFrame;
import engine.MovingCamera;
import entityComposites.GraphicComposite;

public interface Graphic {

	public void draw( ReferenceFrame camera,  GraphicComposite composite);
}
