package menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.MouseEvent;

public class MenuScroller extends MenuButton{
	
	/**
	 * The distance between the scrolling buttons and the main scroller
	 */
	protected int startX;
	/**
	 * The width that the scrolling line is
	 */
	protected int endWidth;
	
	/**
	 * The highest value the scroller can range to
	 */
	protected int maxValue;
	
	/**
	 * The current value the scroller is at
	 */
	protected int currentValue;
	
	private boolean dragging;
	
	public MenuScroller(int x, int y, int width, int height, int startX, int endWidth){
		super(x, y, width, height);
		this.startX = startX;
		this.endWidth = endWidth;
		maxValue = 0;
		currentValue = 0;
		
		dragging = false;
	}
	
	/**
	 * Draw this object to the given graphics object
	 */
	@Override
	public void render(Graphics g){
		//draw side buttons
		//button 1
		g.setColor(Color.BLACK);
		double xx = getX();
		double yy = getY();
		g.fillRect((int)xx, (int)yy, getHeight(), getHeight());
		g.setColor(Color.WHITE);
		g.fillRect((int)xx + 2, (int)yy + 2, getHeight() - 4, getHeight() - 4);
		//arrow
		g.setColor(Color.BLACK);
		xx = getX() + getHeight() - 17;
		yy = getY() + getHeight() / 2 - 3;
		g.fillRect((int)xx, (int)yy, 12, 6);
		g.fillPolygon(new Polygon(new int[]{
			(int)(xx - 6), (int)(xx + 4), (int)(xx + 4)
		}, new int[]{
			(int)(yy + 3), (int)(yy - 4), (int)(yy + 10)
		}, 3));
		//button2
		g.setColor(Color.BLACK);
		xx = startX + endWidth;
		yy = getY();
		g.fillRect((int)xx, (int)yy, getHeight(), getHeight());
		g.setColor(Color.WHITE);
		g.fillRect((int)xx + 2, (int)yy + 2, getHeight() - 4, getHeight() - 4);
		//arrow
		g.setColor(Color.BLACK);
		xx = startX + endWidth + 5;
		yy = getY() + getHeight() / 2 - 3;
		g.fillRect((int)xx, (int)yy, 12, 6);
		xx += 12;
		g.fillPolygon(new Polygon(new int[]{
			(int)(xx + 6), (int)(xx - 4), (int)(xx - 4)
		}, new int[]{
			(int)(yy + 3), (int)(yy - 4), (int)(yy + 10)
		}, 3));
		
		//draw main body
		g.setColor(Color.BLACK);
		g.fillRect(startX, getY(), endWidth, getHeight());
		g.setColor(Color.GRAY);
		g.fillRect(startX + 1, getY() + 1, endWidth - 2, getHeight() - 2);
		g.setColor(Color.BLACK);
		double ww = getHeight() + 8;
		double hh = getHeight() + 8;
		xx = (int)getScrollX() - ww * .5;
		yy =  getY() - 4;
		g.fillRect((int)xx, (int)yy, (int)ww, (int)hh);
		g.setColor(Color.WHITE);
		g.fillRect((int)xx + 2, (int)yy + 2, (int)ww - 4, (int)hh - 4);
	}
	@Override
	public void click(MouseEvent e){
		super.click(e);
		if(e.getX() <= startX) setSelectedValue(currentValue - 1);
		else if(e.getX() >= startX + endWidth) setSelectedValue(currentValue + 1);
	}
	@Override
	public void update(){
		if(mousePressed() && containsMouse() && getMX() >= startX && getMX() <= startX + endWidth){
			dragging = true;
		}
		if(dragging){
			if(!mousePressed()) dragging = false;
			int newG = (int)(maxValue * ((double)getMX() - getX() - (8 + getHeight() - (double)(endWidth / maxValue)) * .5) / endWidth);
			if(newG != currentValue) setSelectedValue(newG);
		}
		super.update();
	}
	
	public double getScrollX(){
		if(maxValue == 0) return 1;
		else return startX + endWidth * ((double)currentValue / maxValue);
	}
	
	protected void setSelectedValue(int g){
		if(g == currentValue) return;
		else if(g < 0) g = 0;
		else if(g > maxValue) g = maxValue;
		currentValue = g;
	}
	
	public int getCurrentValue(){
		return currentValue;
	}
	public void setCurrentValue(int currentValue){
		this.currentValue = currentValue;
	}
	
	public void setMaxValue(int maxValue){
		this.maxValue = maxValue;
	}
}
