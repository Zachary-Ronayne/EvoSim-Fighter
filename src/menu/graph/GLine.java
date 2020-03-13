package menu.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * An object used by the line graph class, which stores information on a line on the graph
 */
public class GLine{
	
	private ArrayList<Double> values;
	private Color lineColor;
	private float lineThickness;
	private String name;
	
	/**
	 * @param lineColor the color to draw to the graph
	 * @param linethickness the thickness of the drawn line
	 * @param name the label to use on the graph, leave blank to draw no label for this line
	 */
	public GLine(Color lineColor, float linethickness, String name){
		values = new ArrayList<Double>();
		
		this.lineColor = lineColor;
		this.lineThickness = linethickness;
		this.name = name;
	}
	
	/**
	 * Adds the specified value to the end of the list
	 * @param val
	 */
	public void addValue(double val){
		values.add(val);
	}
	
	/**
	 * Get the value at the specificed index
	 * @param index
	 * @return
	 */
	public double getValue(int index){
		return values.get(index);
	}
	
	public ArrayList<Double> getValues(){
		return values;
	}
	
	/**
	 * Removes all data for this line
	 */
	public void clearData(){
		values.clear();
	}
	
	public String getName(){
		return name;
	}
	
	/**
	 * Set the given graphics settings to the appropriate settings for this line
	 * @param g
	 */
	public void setGraphicsSettings(Graphics2D g){
		g.setColor(lineColor);
		g.setStroke(new BasicStroke(lineThickness));
	}
	
}
