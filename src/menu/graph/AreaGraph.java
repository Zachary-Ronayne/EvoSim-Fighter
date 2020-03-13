package menu.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class AreaGraph{
	
	private ArrayList<Integer[]> values;
	
	private int width;
	private int height;
	
	private String label;
	
	/**
	 * The image that tracks the graph of the species
	 */
	private BufferedImage graph;
	
	public AreaGraph(ArrayList<Integer[]> values, int width, int height, String label){
		updateValues(values);
		this.width = width;
		this.height = height;
		this.label = label;
	}
	
	public void updateValues(ArrayList<Integer[]> values){
		this.values = values;
	}

	public int getWidth(){
		return width;
	}

	public int getHeight(){
		return height;
	}
	
	/**
	 * 
	 * Redraws this graph with the new data
	 * @param drawSeperaitonLines true to draw black lines between each area
	 * @param usePolygons true to use polygon shapes, false to use rectangles
	 * @param selection the data point that should be used to draw information boxes
	 * @param label the additional text to label the data
	 */
	public void update(boolean drawSeperaitonLines, boolean usePolygons, int selection){
		if(values.size() <= 0) return;
		
		graph = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

		int endX = 150;
		
		BufferedImage labels = new BufferedImage(endX, graph.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		
		Graphics g = graph.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, graph.getWidth() - endX, 0, graph.getHeight());
		g.setColor(Color.WHITE);
		g.fillRect(graph.getWidth() - endX, 0, endX, graph.getHeight());
		
		
		double startX = 70;
		double space = (double)(graph.getWidth() - endX - startX) / values.size();
		double lastX = startX;
		for(int i = 0; i < values.size(); i++){
			
			double y1 = 0;
			double y2 = 0;
			double y3 = 0;
			double y4 = 0;
			
			int total = 0;
			for(int h = 0; h < values.get(i).length; h++){
				total += values.get(i)[h];
			}
			
			for(int j = 0; j < values.get(i).length; j++){
					
				//find the next value that needs to be drawn, and set that to j, if there are no more then this loop is done
				boolean draw = false;
				for(int h = j; h < values.get(i).length && !draw; h++){
					if(values.get(i)[h] != 0 || i != 0 && values.get(i - 1)[h] != 0){
						draw = true;
						j = h;
					}
				}
				
				//if something needs to be drawn, find the y coordinates and draw it
				if(draw){
					//the new top 2 y coordinates become the coordinates from the bottom of the last one
					y1 = y4;
					y2 = y3;
					
					if(i != 0) y4 += graph.getHeight() * (values.get(i - 1)[j] / (double)total);
					y3 += graph.getHeight() * (values.get(i)[j] / (double)total);
					
					Color fillColor = getColor(j * 15 + 4);
					g.setColor(fillColor);

					double x1 = Math.round(lastX);
					double x2 = (int)Math.round(lastX + space);
					
					//draw with polygons
					if(usePolygons){
						if(i == 0){
							g.fillPolygon(new Polygon(new int[]{
									(int)x1, (int)x2, (int)x2, (int)x1
								}, new int[]{
									graph.getHeight() / 2, (int)y2, (int)(y3), graph.getHeight() / 2
								}, 4));
							
							if(drawSeperaitonLines){
								g.setColor(Color.BLACK);
								g.drawLine((int)x1, graph.getHeight() / 2, (int)x2, graph.getHeight() / 2);
							}
						}
						else{
							g.fillPolygon(new Polygon(new int[]{
									(int)x1, (int)x2, (int)x2, (int)x1
								}, new int[]{
									(int)y1, (int)y2, (int)(y3), (int)(y4)
								}, 4));
							
							if(drawSeperaitonLines){
								g.setColor(Color.BLACK);
								g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
							}
						}
					}
					//draw with rectangles (weird display)
					else{
						g.fillRect((int)Math.round(lastX), (int)Math.round(y1), (int)Math.round(space), (int)Math.round(y4 - y1));
						if(drawSeperaitonLines){
							g.setColor(Color.BLACK);
							g.drawLine((int)Math.round(lastX), (int)Math.round(y1), (int)Math.round(space + lastX), (int)Math.round(y1));
						}
					}
					
					//draw labels for number in each species
					if(i == selection - 1 && values.get(i)[j] >= values.get(i).length * .04){
						Graphics g2 = labels.getGraphics();
						g2.setColor(fillColor);
						g2.setFont(new Font("Courier New", Font.BOLD, 14));
						String s = "(" + label + j + "): " + values.get(i)[j];
						int w = 8 + g2.getFontMetrics().stringWidth(s), h = 20, x = 1, y = (int)((y2 + y3 - h) / 2.0);
						g2.fillRect(x, y, w, h);
						g2.setColor(new Color(255, 255, 255, 150));
						g2.fillRect(x + 2, y + 2, w - 4, h - 4);
						g2.setColor(Color.BLACK);
						g2.drawString(s, x + 5, y + 16);
						g2.fillRect(0, 0, 1, labels.getHeight());
					}
				}
				
			}
			lastX += space;
		}
		g.drawImage(labels, (int)((selection) * space + startX), 0, null);
	}
	
	/**
	 * This is a strange color thing that goes through the rainbow
	 * @param num
	 * @return
	 */
	private static Color getColor(int num){
		int change = 15;
		num %= 100;
		boolean cUp = true;
		int cChange = 1;
		int[] color = new int[]{255, 0, 0};
		for(int j = 0; j < num; j++){
			if(cUp){
				color[cChange] += change;
				if(color[cChange] >= 255){
					int diff = 255 - color[cChange];
					color[cChange] = 255;
					cChange--;
					if(cChange <= -1) cChange = 2;
					cUp = false;
					color[cChange] -= diff;
				}
			}
			else{
				color[cChange] -= change;
				if(color[cChange] <= 0){
					int diff = -color[cChange];
					color[cChange] = 0;
					cChange--;
					if(cChange <= -1) cChange = 2;
					cUp = true;
					color[cChange] += diff;
				}
			}
		}
		return new Color(color[0], color[1], color[2]);
	}
	
	/**
	 * Draw the graph to the specified x and y coordinates
	 * @param g
	 * @param x
	 * @param y
	 */
	public void render(Graphics g, int x, int y){
		g.drawImage(graph, x, y, null);
	}
	
}
