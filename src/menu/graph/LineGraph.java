package menu.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class LineGraph{
	
	private BufferedImage graph;
	private int width;
	private int height;

	/**
	 * The space before the lines start
	 */
	private int startX = 70;
	/**
	 * The space between the end of the graph and the end of the image
	 */
	private int endX = 300;
	
	/**
	 * The space between the top and bottom of the graph and the line
	 */
	private int startY;
	
	/**
	 * True if the y scale lines should be rounded to integers
	 */
	private boolean roundToInt;
	
	private GLine[] lines;
	
	public LineGraph(GLine[] lines, int width, int height, int startX, int endX, int startY, boolean roundToInt){
		this.lines = lines;
		this.width = width;
		this.height = height;
		this.startX = startX;
		this.endX = endX;
		this.startY = startY;
		this.roundToInt = roundToInt;
	}
	
	/**
	 * Get the actual line object that this object uses to track data
	 * @return
	 */
	public GLine[] getLines(){
		return lines;
	}
	
	/**
	 * Get the value of the specified line at the specified index
	 * @param line
	 * @param index
	 * @return
	 */
	public double getValue(int line, int index){
		return lines[line].getValue(index);
	}
	
	/**
	 * Add the a new set of values to the lines, values.length should be the same as lines.length
	 * @param values
	 */
	public void addValue(Double[] values){
		for(int i = 0; i < values.length; i++) lines[i].addValue(values[i]);
	}
	
	public int getLineLength(){
		return lines[0].getValues().size();
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	/**
	 * @param selected which data point to show information for
	 */
	public void updateLineGraph(int selected){
		
		graph = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = (Graphics2D)graph.getGraphics();
		
		//find y scale of graph
		double big = -1, small = -1;
		for(int i = 0; i < lines.length; i++){
			ArrayList<Double> values = lines[i].getValues();
			for(int j = 0; j < values.size(); j++){
				if(big == -1 || big < values.get(j)) big = values.get(j);
				if(small == -1 || small > values.get(j)) small = values.get(j);
			}
		}
		
		if(small == big){
			small = -1;
			big = 1;
		}
		
		//background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, graph.getWidth(), graph.getHeight());
		
		//draw scale numbers, y axis lines
		//find the distance between scale lines
		double numLines = 13;
		double lineDistance = height / numLines;
		if(roundToInt){
			lineDistance = (int)(lineDistance + 1);
			big = (int)(big * numLines + 1) / numLines;
			small = (int)(small * numLines - 1) / numLines;
		}
		
		//determine the scale of the graph on the y axis
		double yScale = (graph.getHeight() - startY * 2) / Math.abs(big - small);
		//determine where the origin is on the y axis on the screen
		double yZero = (graph.getHeight() - startY * 2) * (big / Math.abs(big - small)) + startY;
		
		g.setFont(new Font("Courier New", Font.BOLD, 15));
		
		for(int i = 0; i < numLines + 2; i++){
			//the y coordinate on the screen
			double y = (i - round(yZero / lineDistance)) * lineDistance + yZero;
			//the label for this line
			double outputValue = (yZero - y) / yScale;
			String s;
			if(roundToInt) s = "" + round(outputValue);
			else{
				s = (outputValue + "0000");
				s = s.substring(0, s.indexOf(".") + 4);
			}
			g.setColor(new Color(127, 127, 127));
			if(outputValue == 0) g.setStroke(new BasicStroke(2f));
			else g.setStroke(new BasicStroke(.5f));
			g.drawLine(g.getFontMetrics().stringWidth(s) + 4, (int)y, graph.getWidth() - endX, (int)y);
			g.setColor(Color.BLACK);
			g.drawString(s, 1, (int)y + 5);
		}
		
		//draw the lines for the values of each line
		g.setFont(new Font("Courier New", Font.BOLD, 15));
		int cnt = 0;
		for(int i = 0; i < lines.length; i++){
			GLine l = lines[i];
			l.setGraphicsSettings(g);
			Point2D.Double previous = new Point2D.Double(startX, yZero);
			for(int j = 0; j < l.getValues().size(); j++){
				double x = previous.x + (double)(graph.getWidth() - startX - endX) / l.getValues().size();
				double y = yZero - l.getValues().get(j) * yScale;
				g.drawLine((int)previous.x, (int)previous.y, (int)x, (int)y);
				previous = new Point2D.Double(x, y);
				if(selected - 1 == j){
					if(!l.getName().equals("")){
						g.drawString(l.getName() + ": " + l.getValues().get(j), graph.getWidth() - endX + 10, 24 + 20 * cnt);
						cnt++;
					}
				}
			}
		}
		
		g.setFont(new Font("Courier New", Font.BOLD, 20));
		g.setColor(Color.BLACK);
		g.drawString("Pos: ", graph.getWidth() - endX + 10, height - 30);
		g.drawString("" + selected, graph.getWidth() - endX + 10, height - 5);
		
	}
	
	/**
	 * Draw the graph at the specified coordinates
	 * @param g
	 * @param x
	 * @param y
	 */
	public void render(Graphics g, int x, int y){
		g.drawImage(graph, x, y, null);
	}
	
	/**
	 * Rounds num to the closest int
	 * @param num
	 * @return
	 */
	public static int round(double num){
		return (int)Math.round(num);
	}
}
