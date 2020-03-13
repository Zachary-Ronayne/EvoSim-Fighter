package fighter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class UserFighter extends Fighter{
	
	private KeyInput keys;
	
	/**
	 * True if this fighter will be on the left, false if it is on the right
	 */
	private boolean leftSide;
	
	public UserFighter(double x, double y, int birthGeneration, int fighterID, int species, boolean leftSide){
		super(x, y, birthGeneration, fighterID, species);
		this.leftSide = leftSide;
		
		keys = new KeyInput();
	}
	
	@Override
	public void reset(boolean left){
		super.reset(left);
		leftSide = left;
	}
	
	public void setLeftSide(boolean leftSide){
		this.leftSide = leftSide;
	}
	
	public boolean getLeftSide(){
		return leftSide;
	}
	
	@Override
	public void renderDisplayInfo(Graphics g, int x, int y){
		int w = 60;
		if(leftSide){
			drawCharBox(g, x + w / 2, y + w * 3, "A", "Left", leftDown);
			drawCharBox(g, x + 5 * w / 2, y + w * 3, "D", "Right", rightDown);
			drawCharBox(g, x + 3 * w / 2, y + w * 2, "W", "Jump", jumpDown);
			drawCharBox(g, x, y + w / 2, "1", "Attack", attackDown);
			drawCharBox(g, x + w, y + w / 2, "2", "Block", blockDown);
		}
		else{
			drawCharBox(g, x + w / 2, y + w * 3, "<-", "Left", leftDown);
			drawCharBox(g, x + 5 * w / 2, y + w * 3, "->", "Right", rightDown);
			drawCharBox(g, x + 3 * w / 2, y + w * 2, "^", "Jump", jumpDown);
			drawCharBox(g, x, y + w / 2, ",", "Attack", attackDown);
			drawCharBox(g, x + w, y + w / 2, ".", "Block", blockDown);
		}
	}
	
	private void drawCharBox(Graphics g, int x, int y, String c, String description, boolean pressed){
		int w = 60;
		g.setColor(Color.BLACK);
		g.fillRect(x, y, w, w);
		if(pressed) g.setColor(Color.RED);
		else g.setColor(Color.WHITE);
		g.fillRect(x + 2, y + 2, w - 4, w - 4);
		g.setColor(Color.BLACK);
		g.setFont(new Font("Courier New", Font.BOLD, 30));
		g.drawString(c, x + 6, y + 35);
		g.setFont(new Font("Courier New", Font.BOLD, 15));
		g.drawString(description, x + 2, y - 2);
	}
	
	/**
	 * Allows this fighter to be controlled by the key inputs from c
	 * @param c
	 */
	public void setUpControls(Component c){
		c.addKeyListener(keys);
	}
	/**
	 * Stops this fighter from being controlled by the key inputs from c
	 * @param c
	 */
	public void disableControls(Component c){
		c.addKeyListener(keys);
	}
	
	public class KeyInput extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e){
			int key = e.getKeyCode();
			if(leftSide){
				if(key == KeyEvent.VK_W && !jumpDown) jumpDown = true;
				else if(key == KeyEvent.VK_A && !leftDown) leftDown = true;
				else if(key == KeyEvent.VK_D && !rightDown) rightDown = true;
				else if(key == KeyEvent.VK_1 && !attackDown) attackDown = true;
				else if(key == KeyEvent.VK_2 && !blockDown) blockDown = true;
			}
			else{
				if(key == KeyEvent.VK_UP && !jumpDown) jumpDown = true;
				else if(key == KeyEvent.VK_LEFT && !leftDown) leftDown = true;
				else if(key == KeyEvent.VK_RIGHT && !rightDown) rightDown = true;
				else if(key == KeyEvent.VK_COMMA && !attackDown) attackDown = true;
				else if(key == KeyEvent.VK_PERIOD && !blockDown) blockDown = true;
			}
		}
		@Override
		public void keyReleased(KeyEvent e){
			int key = e.getKeyCode();
			if(leftSide){
				if(key == KeyEvent.VK_W && jumpDown) jumpDown = false;
				else if(key == KeyEvent.VK_A && leftDown) leftDown = false;
				else if(key == KeyEvent.VK_D && rightDown) rightDown = false;
				else if(key == KeyEvent.VK_1 && attackDown) attackDown = false;
				else if(key == KeyEvent.VK_2 && blockDown) blockDown = false;
			}
			else{
				if(key == KeyEvent.VK_UP && jumpDown) jumpDown = false;
				else if(key == KeyEvent.VK_LEFT && leftDown) leftDown = false;
				else if(key == KeyEvent.VK_RIGHT && rightDown) rightDown = false;
				else if(key == KeyEvent.VK_COMMA && attackDown) attackDown = false;
				else if(key == KeyEvent.VK_PERIOD && blockDown) blockDown = false;
			}
		}
	}
}
