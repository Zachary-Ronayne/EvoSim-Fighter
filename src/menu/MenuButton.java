package menu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuButton{
	
	//bounds of button
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	
	//mouse input
	private MouseInput mouseInput;
	
	private boolean mouseInBounds;
	private boolean mousePressed;
	private int mx;
	private int my;
	
	public MenuButton(int x, int y, int width, int height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		mouseInput = new MouseInput();
		
		mouseInBounds = false;
		mousePressed = false;
		mx = 0;
		my = 0;
	}
	
	/**
	 * Links this button with the given component so that mouse input activates
	 * @param c
	 */
	public void linkToComponent(Component c){
		c.addMouseListener(mouseInput);
		c.addMouseMotionListener(mouseInput);
		c.addMouseWheelListener(mouseInput);
	}

	/**
	 * Unlinks this button with the given component so that mouse input does not activate
	 * @param c
	 */
	public void unlinkFromComponent(Component c){
		c.removeMouseListener(mouseInput);
		c.removeMouseMotionListener(mouseInput);
		c.removeMouseWheelListener(mouseInput);
	}

	public int getX(){
		return x;
	}
	public void setX(int x){
		this.x = x;
	}
	public int getY(){
		return y;
	}
	public void setY(int y){
		this.y = y;
	}
	public int getWidth(){
		return width;
	}
	public void setWidth(int width){
		this.width = width;
	}
	public int getHeight(){
		return height;
	}
	public void setHeight(int height){
		this.height = height;
	}
	public Rectangle getBounds(){
		return new Rectangle(x, y, width, height);
	}
	
	public boolean containsMouse(){
		return mouseInBounds;
	}
	public boolean mousePressed(){
		return mousePressed;
	}
	/**
	 * Get the x position of the mouse
	 * @return
	 */
	public int getMX(){
		return mx;
	}
	/**
	 * Get the y position of the mouse
	 * @return
	 */
	public int getMY(){
		return my;
	}
	
	/**
	 * Draw the button
	 * @param g
	 */
	public void render(Graphics g){
		g.setColor(Color.BLACK);
		g.fillRect(x, y, width, height);
		if(mouseInBounds) g.setColor(new Color(200, 200, 200));
		else g.setColor(Color.WHITE);
		g.fillRect(x + 2, y + 2, width - 4, height - 4);
	}
	
	/**
	 * This method is called when this button is clicked on, ovveride this method to make it do something
	 */
	public void click(MouseEvent e){}
	
	/**
	 * Override and call this method if this button needs to have actions performed on a time interval
	 */
	public void update(){}
	
	public class MouseInput extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e){
			if(mouseInBounds){
				click(e);
				mousePressed = true;
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e){
			super.mouseReleased(e);
			mousePressed = false;
		}
		
		@Override
		public void mouseDragged(MouseEvent e){
			super.mouseDragged(e);
			mx = e.getX();
			my = e.getY();
		}
		
		@Override
		public void mouseMoved(MouseEvent e){
			super.mouseMoved(e);
			mouseInBounds = getBounds().contains(e.getX(), e.getY() - 23);
			mx = e.getX();
			my = e.getY();
		}
	}
	
}
