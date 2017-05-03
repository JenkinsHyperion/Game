package animation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class LoadAnimation {

	private static BufferedImage spriteSheet;

	public static BufferedImage loadSpriteSheet(String path) { // load
																// spritesheet
																// to be passed 
																// to frame
																// getter

		BufferedImage sprite = null;

		try {

			sprite = ImageIO
					.read(new File(System.getProperty("user.dir") + "\\Assets\\" +path));

		} catch (IOException e) {
			e.printStackTrace();
		}

		return sprite;
	}

	// Cuts each animation frame out of the sprite sheet
	public static BufferedImage getFrame(int xGrid, int yGrid, String file, int tileWidth, int tileHeight) {

		// if (spriteSheet == null) {
		spriteSheet = loadSpriteSheet(file);
		// }

		return spriteSheet.getSubimage(xGrid * tileWidth, yGrid * tileHeight, tileWidth, tileHeight);
	}

	/**
	 * 
	 * @param length
	 * @param row
	 * @param tileSize
	 * @param file
	 * @return
	 */
	public static BufferedImage[] buildAnimation(int length, int row, int tileSize, String file) {

		if (length < 1){System.out.println("WARNING BUILD ANIMATION IN LOAD ANIMATION CLASS");}
		
		BufferedImage[] b = new BufferedImage[length];

		for (int i = 0; i < length; i++) {
			b[i] = getFrame(i, row, file, tileSize, tileSize);
		}
		return b;
	}

	public static BufferedImage[] buildAnimation(int length, int row, int tileWidth , int tileHeight, String file) {

		if (length < 1){System.out.println("WARNING BUILD ANIMATION IN LOAD ANIMATION CLASS");}
		
		BufferedImage[] b = new BufferedImage[length];

		for (int i = 0; i < length; i++) {
			b[i] = getFrame(i, row, file, tileWidth, tileHeight);
		}
		return b;
	}
	
	/**
	 * 
	 * @param length
	 * @param row
	 * @param tileWidth
	 * @param tileHeight
	 * @param file
	 * @return
	 */
	public static BufferedImage[] getAnimation(int length, int row, int tileWidth, int tileHeight , String file) {

		BufferedImage[] b = new BufferedImage[length];

		for (int i = 0; i < length; i++) {
			b[i] = getFrame(i, row, file, tileWidth, tileHeight);
		}

		return b;
	}

}