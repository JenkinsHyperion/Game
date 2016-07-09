package animation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class LoadAnimation {

	private static BufferedImage spriteSheet;

	public static BufferedImage loadSpriteSheet(String name) { // load
																// spritesheet
																// to be passed
																// to frame
																// getter

		BufferedImage sprite = null;

		try {

			sprite = ImageIO
					.read(new File(System.getProperty("user.dir").replace("\\", "//") + "//Assets//" + name + ".png"));

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

	// Compile individual frames into array of buffered images
	/**
	 * 
	 * @param length
	 *            - number of frames in row
	 * @param row
	 *            - vertical row of sprite sheet to use. Starts at 0 for top row
	 * @param tileSize
	 *            - width and length of square for each frame
	 * @param file
	 *            - name only of file in Assets folder, minus directory path and
	 *            extension.
	 * @return
	 */
	public static BufferedImage[] getAnimation(int length, int row, int tileSize, String file) {

		BufferedImage[] b = new BufferedImage[length];

		for (int i = 0; i < length; i++) {
			b[i] = getFrame(i, row, file, tileSize, tileSize);
		}

		return b;
	}
	
	public static BufferedImage[] getAnimation(int length, int row, int tileWidth, int tileHeight , String file) {

		BufferedImage[] b = new BufferedImage[length];

		for (int i = 0; i < length; i++) {
			b[i] = getFrame(i, row, file, tileWidth, tileHeight);
		}

		return b;
	}

}