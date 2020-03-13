package sim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.util.Scanner;

public class NeuralNet{
	
	public static final int OUTPUT_BOX_WIDTH = 45;
	public static final int OUTPUT_BOX_SPACE = 8;
	
	public static final double CONSTANT_NODE = 1;
	
	/**
	 * Values from -1 to 1 that come directly from the input range, one more is always added on for the constant node
	 */
	private double[] inputs;
	/**
	 * The hidden layer in the middle, the weights going from the inputs to the hidden layer
	 * Should be of size [input nodes + 1 for the constant node][number of hidden nodes + 1 for the constant node]
	 */
	private double[][] inputWeights;
	/**
	 * Values from -1 to 1 that are the hidden values based on the weights from the inputs, one more is always added on for the constant node
	 */
	private double[] hidden;
	/**
	 * The hidden layer in the middle, the weights going from the hidden layer to the outputs
	 * Should be of size [number of hidden nodes + 1 for the constant node][number of output nodes]
	 */
	private double[][] outputWeights;
	
	/**
	 * Values from -1 to 1 that determine the output of the neural net
	 */
	private double[] outputs;
	
	/**
	 * The image that shows the lines that represent the connections between nodes
	 */
	private BufferedImage netLines;
	
	/**
	 * Both weight arrays must be between 0 and 1, if they are not, they will be put in the closest value to -1 to 1
	 * Both arrays must be rectangular arrays.
	 * inputWeights[0].length and outputWeights.length must be equal
	 * @param inputWeights
	 * @param outputWeights
	 */
	public NeuralNet(double[][] inputWeights, double[][] outputWeights, RandomSave rng){
		
		//hidden node layer initialization
		hidden = new double[inputWeights[0].length + 1];
		for(int i = 0; i < hidden.length; i++) hidden[i] = 0;
		//set the last node to the constant value
		hidden[hidden.length - 1] = CONSTANT_NODE;

		//input node layer initialization
		//one additional node for the constant node
		this.inputs = new double[inputWeights.length + 1];
		//one additional node for the input layer but not one for the hidden layer, because connections to the constant node do nothing
		this.inputWeights = new double[inputWeights.length + 1][inputWeights[0].length];
		//iterate through the entire input weight list
		for(int i = 0; i < this.inputWeights.length; i++){
			for(int j = 0; j < this.inputWeights[i].length; j++){
				//set the input weight to a new random value
				this.inputWeights[i][j] = getNewRand(rng);
			}
			//set the corresponding input node to 0 by default
			inputs[i] = 0;
		}
		//set the value of the last input node to the constant node value
		inputs[inputs.length - 1] = CONSTANT_NODE;
		
		//output node layer initialization
		//the outputs are the same length, as no additional nodes are added on
		this.outputs = new double[outputWeights.length];
		//one additional set of weights is added on for the constant node
		this.outputWeights = new double[outputWeights.length][outputWeights[0].length + 1];
		//iterate through the entire output weight list
		for(int i = 0; i < this.outputWeights.length; i++){
			for(int j = 0; j < this.outputWeights[0].length; j++){
				//set the output weight to a new random value
				this.outputWeights[i][j] = getNewRand(rng);
			}
			outputs[i] = 0;
		}
	}
	
	/**
	 * Use this to reset the size of the connection arrays. This is normally called right before calling both set input and output weights
	 */
	public void setBrainSize(int in, int hid, int out){
		inputWeights = new double[in + 1][hid + 1];
		outputWeights = new double[out][hid + 1];
		inputs = new double[in + 1];
		hidden = new double[hid + 1];
		outputs = new double[out];
	}
	
	public void setInputWeights(double[][] inputWeights){
		for(int i = 0; i < this.inputWeights.length; i++){
			for(int j = 0; j < this.inputWeights[i].length; j++){
				this.inputWeights[i][j] = inputWeights[i][j];
			}
		}
	}
	
	public void setOutputWeights(double[][] outputWeights){
		for(int i = 0; i < this.outputWeights.length; i++){
			for(int j = 0; j < this.outputWeights[i].length; j++){
				this.outputWeights[i][j] = outputWeights[i][j];
			}
		}
	}
	
	public double getInputWeight(int i, int j){
		return inputWeights[i][j];
	}
	public double[][] getInputWeights(){
		return inputWeights;
	}
	public void setInputWeight(int i, int j, double val){
		inputWeights[i][j] = val;
	}
	public double getOutputWeight(int i, int j){
		return outputWeights[i][j];
	}
	public double[][] getOutputWeights(){
		return outputWeights;
	}
	public void setOutputWeight(int i, int j, double val){
		outputWeights[i][j] = val;
	}
	
	public double getOutput(int i){
		return outputs[i];
	}
	
	public int getInputLength(){
		return inputs.length;
	}
	public int getHiddenLength(){
		return hidden.length;
	}
	public int getOutputLength(){
		return outputs.length;
	}
	
	/**
	 * Change the input values to a new set of values
	 * @param inputs
	 */
	public void updateInputs(double[] inputs){
		for(int i = 0; i < inputs.length; i++){
			this.inputs[i] = inputs[i];
		}
	}
	
	/**
	 * Determine new outputs based on the current inputs
	 */
	public void calculateOutputs(){
		for(int i = 0; i < inputWeights[0].length; i++){
			double total = 0;
			for(int j = 0; j < inputWeights.length; j++){
				total += inputWeights[j][i] * inputs[j];
			}
			hidden[i] = strangeTan(total);
		}
		for(int i = 0; i < outputWeights.length; i++){
			double total = 0;
			for(int j = 0; j < outputWeights[i].length; j++){
				total += outputWeights[i][j] * hidden[j];
			}
			outputs[i] = strangeTan(total);
		}
		
		inputs[inputs.length - 1] = CONSTANT_NODE;
		hidden[hidden.length - 1] = CONSTANT_NODE;
	}
	
	/**
	 * Draw a visual representing this neural net at the specified x and y on graphcis g
	 * @param g
	 * @param x
	 * @param y
	 */
	public void render(Graphics g, int x, int y){
		//draw the connections
		g.drawImage(netLines, x, y, null);
		
		//draw the values of the brain

		int inSpace = 900 / inputs.length - OUTPUT_BOX_WIDTH;
		int hidSpace = 900 / hidden.length - OUTPUT_BOX_WIDTH;
		int outSpace = 900 / outputs.length - OUTPUT_BOX_WIDTH;
		renderRow(g, x, y, inputs, inSpace);
		renderRow(g, x + 160, y, hidden, hidSpace);
		renderRow(g, x + 320, y, outputs, outSpace);
		String[] text = new String[]{"X", "Y", "DX1", "DY1", "DX2", "DY2", "A1", "B1", "A2", "B2", "M", "C"};
		g.setColor(Color.BLACK);
		g.setFont(new Font("Courier New", Font.BOLD, 30));
		for(int i = 0; i < text.length; i++) g.drawString(text[i], x - 60, y + 28 + (OUTPUT_BOX_WIDTH + inSpace) * i);
		text = new String[]{"L", "R", "J", "A", "B", "M"};
		for(int i = 0; i < text.length; i++) g.drawString(text[i], x + 380, y + 28 + (OUTPUT_BOX_WIDTH + outSpace) * i);
	}
	
	/**
	 * use a negative number for layer and or pos to show all
	 * use a number greater than 2 to clear the board to nothing
	 * 
	 * @param layer the layer to draw lines from
	 * @param pos the position to draw lines from
	 */
	public void updateNetLines(int layer, int pos){
		//reset the image for the lines
		netLines = new BufferedImage(400, 900, BufferedImage.TYPE_3BYTE_BGR);
		
		//get the graphics object from the image
		Graphics2D g = (Graphics2D)netLines.getGraphics();
		
		//fill the background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, netLines.getWidth(), netLines.getHeight());
		
		//if should draw no lines, return
		if(layer > 2) return;
		
		//draw lines of thickness 2
		g.setStroke(new BasicStroke(2f));
		
		//width of a node in the display
		for(int i = 0; i < inputWeights.length; i++){
			for(int j = 0; j < inputWeights[i].length; j++){
				//only draw the line if all lines should be drawn or if this is the matching line
				if(layer < 0 || pos < 0 || layer == 0 && pos == i || layer == 1 && pos == j){
					//the value of the weight
					double ww = inputWeights[i][j];
					//determine the opacity of the line, 0 = white, closer to -1 = red, closer to 1 = blue
					int opacity;
					if(ww > 0)  opacity = (int)(255 * inputWeights[i][j]);
					else opacity = (int)(255 * -inputWeights[i][j]);
					if(opacity < 0) opacity = 0;
					else if(opacity > 255) opacity = 255;
					if(inputWeights[i][j] > 0) g.setColor(new Color(0, 0, 255, opacity));
					else g.setColor(new Color(255, 0, 0, opacity));
					g.drawLine(getNodeLineX(0), getNodeLineY(0, i), getNodeLineX(1), getNodeLineY(1, j));
				}
			}
		}
		for(int i = 0; i < outputWeights.length; i++){
			for(int j = 0; j < outputWeights[i].length; j++){
				//only draw the line if all lines should be drawn or if this is the matching line
				if(layer < 0 || pos < 0 || layer == 1 && pos == j || layer == 2 && pos == i){
					//the value of the weight
					double ww = outputWeights[i][j];
					//determine the opacity of the line, 0 = white, closer to -1 = red, closer to 1 = blue
					int opacity;
					if(ww > 0)  opacity = (int)(255 * outputWeights[i][j]);
					else opacity = (int)(255 * -outputWeights[i][j]);
					if(opacity < 0) opacity = 0;
					else if(opacity > 255) opacity = 255;
					if(ww > 0) g.setColor(new Color(0, 0, 255, opacity));
					else g.setColor(new Color(255, 0, 0, opacity));
					g.drawLine(getNodeLineX(1), getNodeLineY(1, j), getNodeLineX(2), getNodeLineY(2, i));
				}
			}
		}
	}
	
	private void renderRow(Graphics g, int x, int y, double[] row, int space){
		for(int i = 0; i < row.length; i++){
			g.setColor(Color.BLACK);
			g.fillOval(x, y + i * (OUTPUT_BOX_WIDTH + space), OUTPUT_BOX_WIDTH, OUTPUT_BOX_WIDTH);
			int scale = 255 - (int)(255 * Math.abs(row[i]));
			if(scale < 0) scale = 0;
			else if(scale > 255) scale = 255;
			if(row[i] < 0) g.setColor(new Color(255, scale, scale));
			else g.setColor(new Color(scale, scale, 255));
			g.fillOval(x + 1, y + 1 + i * (OUTPUT_BOX_WIDTH + space), OUTPUT_BOX_WIDTH - 2, OUTPUT_BOX_WIDTH - 2);
			g.setColor(Color.BLACK);
			g.setFont(new Font("Courier New", Font.BOLD, 20));
			g.drawString("" + (int)(row[i] * 1000), x + 4, y + 26 + i * (OUTPUT_BOX_WIDTH + space));
		}
	}
	
	/**
	 * @param layer 0 = input, 1 = hidden, 2 = output
	 * @return The x coordinate relative to the origin of where the given node will be
	 */
	public int getNodeLineX(int layer){
		return 160 * layer + OUTPUT_BOX_WIDTH / 2;
	}
	/**
	 * @param pos the position in the row
	 * @return The x coordinate relative to the origin of where the given node will be
	 */
	public int getNodeLineY(int layer, int pos){
		if(layer == 0) return  OUTPUT_BOX_WIDTH / 2 + 900 / inputs.length * pos;
		else if(layer == 1) return  OUTPUT_BOX_WIDTH / 2 + 900 / hidden.length * pos;
		else return  OUTPUT_BOX_WIDTH / 2 + 900 / outputs.length * pos;
	}
	
	/**
	 * Mutates the brain randomly, the higher mutability is, the more significant the changes are
	 * @param mutability
	 * @param rng
	 */
	public void mutate(double mutability, RandomSave rng){
		for(int i = 0; i < inputWeights.length; i++){
			for(int j = 0; j < inputWeights[i].length; j++){
				inputWeights[i][j] += mutability * (rng.getRand() - .5);
				if(inputWeights[i][j] < -1) inputWeights[i][j] = -1;
				if(inputWeights[i][j] > 1) inputWeights[i][j] = 1;
			}
		}
		
		for(int i = 0; i < outputWeights.length; i++){
			for(int j = 0; j < outputWeights[i].length; j++){
				outputWeights[i][j] += mutability * (rng.getRand() - .5);
				if(outputWeights[i][j] < -1) outputWeights[i][j] = -1;
				if(outputWeights[i][j] > 1) outputWeights[i][j] = 1;
			}
		}
	}
	
	public static double sigmoid(double x){
		return 1.0 / (1.0 + Math.pow(Math.E, -x));
	}
	
	/**
	 * I am basically just using this as a replacement for the sigmoid function, so long that x is positive, it always returns a value between 0 and 1
	 * @param x
	 * @return
	 */
	public static double strangeTan(double x){
		return 2.0 / Math.PI * Math.atan(.7 * x);
	}
	

	/**
	 * Gets a new random number for a weight
	 * @return
	 */
	public static double getNewRand(RandomSave rng){
		return rng.getRand() * 2 - 1;
	}
	
	/**
	 * Saves this NeuralNet with the given PrintWriter, mist be ready to immediately save the data
	 * @param write
	 */
	public void save(PrintWriter write){
		write.println("NeuralNet");
		write.println("Input_Length: " + inputs.length);
		write.println("Hidden_Length: " + hidden.length);
		write.println("Ouptut_Length: " + outputs.length);
		write.println("Input_Weights: " + inputWeights.length + " " + inputWeights[0].length);
		for(int i = 0; i < inputWeights.length; i++){
			for(int j = 0; j < inputWeights[i].length; j++){
				write.print(inputWeights[i][j] + " ");
			}
			write.println();
		}
		write.println("Output_Weights: " + outputWeights.length + " " + outputWeights[0].length);
		for(int i = 0; i < outputWeights.length; i++){
			for(int j = 0; j < outputWeights[i].length; j++){
				write.print(outputWeights[i][j] + " ");
			}
			write.println();
		}
		write.println();
	}
	
	/**
	 * Reads in the data for this NeuralNet from the given Scanner, must be immediately read the data
	 * @param scan
	 */
	public void load(Scanner scan){
		scan.next();
		scan.next(); inputs = new double[scan.nextInt()];
		scan.next(); hidden = new double[scan.nextInt()];
		scan.next(); outputs = new double[scan.nextInt()];
		scan.next(); inputWeights = new double[scan.nextInt()][scan.nextInt()];
		for(int i = 0; i < inputWeights.length; i++){
			for(int j = 0; j < inputWeights[i].length; j++){
				inputWeights[i][j] = scan.nextDouble();
			}
		}
		scan.next(); outputWeights = new double[scan.nextInt()][scan.nextInt()];
		for(int i = 0; i < outputWeights.length; i++){
			for(int j = 0; j < outputWeights[i].length; j++){
				outputWeights[i][j] = scan.nextDouble();
			}
		}
	}
	
}
