package menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * A screen that has an image representing what is currently on it
 */
public class Screen{
	protected BufferedImage image;
	
	public Screen(int width, int height){
		image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
	}

	public void setScreenWidth(int width){
		image = new BufferedImage(width, image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
	}
	public void setScreenHeight(int height){
		image = new BufferedImage(image.getWidth(), height, BufferedImage.TYPE_3BYTE_BGR);
	}
	public void setScreenSize(int width, int height){
		image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
	}
	
	/**
	 * Update the current image of the screen based on new parameters
	 */
	public void render(Graphics g){
		renderOverride(image.getGraphics());
		g.drawImage(image, 0, 23, null);
	}
	
	/**
	 * Override this method to draw additional grpahics to the screen
	 * @param g
	 */
	public void renderOverride(Graphics g){
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
	}
}
