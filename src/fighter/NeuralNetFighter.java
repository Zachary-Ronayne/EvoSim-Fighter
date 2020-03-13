package fighter;

import java.awt.Graphics;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import menu.Config;
import sim.NeuralNet;
import sim.RandomSave;

public class NeuralNetFighter extends Fighter{
	
	public static final int IN_NODES = 11;
	public static final int OUT_NODES = 6;
	
	private NeuralNet brain;
	
	private double numNodes;
	private int intNodes;
	private double nodeMutability;
	private double mutability;
	private int parentID;
	
	public NeuralNetFighter(double x, double y, int birthGeneration, int fighterID, int species, RandomSave rng){
		super(x, y, birthGeneration, fighterID, species);
		
		parentID = -1;
		
		mutability = Config.MAX_MUTABILITY * rng.getRand();
		nodeMutability = Config.MAX_NODE_MUTABILITY * rng.getRand();
		if(mutability < Config.MIN_MUTABILITY) mutability = Config.MIN_MUTABILITY;
		if(nodeMutability < Config.MIN_NODE_MUTABILITY) nodeMutability = Config.MIN_NODE_MUTABILITY;
		numNodes = rng.getRand() * (Config.MAX_START_NODES - Config.MIN_START_NODES) + Config.MIN_START_NODES;
		
		determineIntNodes(rng);
		
		double[][] in = new double[IN_NODES][getIntNodes()];
		double[][] out = new double[OUT_NODES][getIntNodes()];
		
		brain = new NeuralNet(in, out, rng);
	}
	
	/**
	 * Creates a mutated version of this fighter with the given id and generation
	 * @param id
	 * @param gen
	 * @return
	 */
	public NeuralNetFighter getMutatedChild(int gen, int id, RandomSave rng){
		//make a new object
		NeuralNetFighter f = new NeuralNetFighter(0, 0, gen, id, getSpecies(), rng);
		
		//set the parent id
		f.parentID = getFighterID();
		
		//set the mutability to a mutated value from the parent
		f.mutability = mutability + (rng.getRand() - .5) * Config.MUTABILITY_CHANGE;
		//ensure the mutability is within its allowed range
		if(f.mutability < Config.MIN_MUTABILITY) f.mutability = Config.MIN_MUTABILITY;
		else if(f.mutability > Config.MAX_MUTABILITY) f.mutability = Config.MAX_MUTABILITY;

		//set the node mutability to a mutated value from the parent
		f.nodeMutability = nodeMutability + (rng.getRand() - .5) * Config.NODE_MUTABILITY_CHANGE;
		//ensure the node mutability is within its allowed range
		if(f.nodeMutability < Config.MIN_NODE_MUTABILITY) f.nodeMutability = Config.MIN_NODE_MUTABILITY;
		else if(f.nodeMutability > Config.MAX_NODE_MUTABILITY) f.nodeMutability = Config.MAX_NODE_MUTABILITY;
		
		//mutate the number of nodes based on mutability of the parent
		f.numNodes = numNodes + (rng.getRand() - .5) * f.nodeMutability;
		if(f.numNodes < Config.MIN_NODES) f.numNodes = Config.MIN_NODES;
		else if(f.numNodes > Config.MAX_NODES) f.numNodes = Config.MAX_NODES;
		
		//determine the nodes of the new fighter
		f.determineIntNodes(rng);
		
		//make the new brain
		NeuralNet newBrain = new NeuralNet(new double[IN_NODES][f.getIntNodes()],  new double[OUT_NODES][f.getIntNodes()], rng);
		
		//make lists to keep track of which node indexes from the parents will be put into the new brain
		int[] inNodes = new int[newBrain.getHiddenLength() - 1];
		int[] outNodes = new int[newBrain.getHiddenLength()];
		
		//make lists of all the parents node indexes
		ArrayList<Integer> pInNodes = new ArrayList<Integer>();
		ArrayList<Integer> pOutNodes = new ArrayList<Integer>();
		for(int i = 0; i < brain.getHiddenLength() - 1; i++) pInNodes.add(i);
		for(int i = 0; i < brain.getHiddenLength(); i++) pOutNodes.add(i);
		
		//if the parent has more nodes than the child, or the same amount, then randomly pick nodes from the parents to use
		if(getIntNodes() > f.getIntNodes()){
			//set the new indexes
			for(int i = 0; i < inNodes.length; i++){
				int index = (int)(rng.getRand() * pInNodes.size());
				inNodes[i] = index;
				//remove the index that was just set so that the same index is never used twice
				pInNodes.remove(index);
			}
			
			//repeat the same for output nodes
			for(int i = 0; i < outNodes.length; i++){
				int index = (int)(rng.getRand() * pOutNodes.size());
				outNodes[i] = index;
				//remove the index that was just set so that the same index is never used twice
				pOutNodes.remove(index);
			}
		}
		//if the parent has less nodes than the child
		else if(getIntNodes() < f.getIntNodes()){
			//copy all the nodes from the parent that it can
			int i;
			for(i = 0; i < pInNodes.size(); i++) inNodes[i] = pInNodes.get(i);
			//randomly pick from the parents nodes for the ones to copy
			for(int j = i; j < inNodes.length; j++){
				inNodes[j] = (int)(rng.getRand() * pInNodes.size());
			}
			
			//repeat the same for out nodes
			//copy all the nodes from the parent that it can
			for(i = 0; i < pOutNodes.size(); i++) outNodes[i] = pOutNodes.get(i);
			//randomly pick from the parents nodes for the ones to copy
			for(int j = i; j < outNodes.length; j++){
				outNodes[j] = (int)(rng.getRand() * pOutNodes.size());
			}
		}
		//the child and parent have the same number of nodes, just directly copy all the node indexes
		else{
			for(int i = 0; i < inNodes.length; i++) inNodes[i] = pInNodes.get(i);
			for(int i = 0; i < outNodes.length; i++) outNodes[i] = pOutNodes.get(i);
		}
		
		//copy the input node values to the new brain weights
		double[][] oldB = brain.getInputWeights();
		double[][] newB = newBrain.getInputWeights();
		for(int i = 0; i < newB.length; i++){
			for(int j = 0; j < inNodes.length && inNodes[j] < oldB[i].length; j++){
				newBrain.setInputWeight(i, j, oldB[i][inNodes[j]]);
			}
		}
		
		//copy the output node values to the new brain weights
		oldB = brain.getOutputWeights();
		newB = newBrain.getOutputWeights();
		for(int i = 0; i < newB.length; i++){
			for(int j = 0; j < outNodes.length; j++){
				newBrain.setOutputWeight(i, j, oldB[i][outNodes[j]]);
			}
		}
		
		//add the new brain to the new fighter
		f.brain = newBrain;
		//mutate the new brain
		f.brain.mutate(mutability, rng);
		
		//return the fighter
		return f;
	}
	
	/**
	 * A duplicate of this fighter, but as a different object
	 * @return
	 */
	public NeuralNetFighter getCopy(){
		NeuralNetFighter f = new NeuralNetFighter(0, 0, getBirthGeneration(), getFighterID(), getSpecies(), new RandomSave(0));
		f.brain = new NeuralNet(new double[brain.getInputLength() - 1][brain.getHiddenLength() - 1], new double[brain.getOutputLength()][brain.getHiddenLength() - 1], new RandomSave(0));
		
		double[][] in = brain.getInputWeights();
		double[][] out = brain.getOutputWeights();
		
		for(int i = 0; i < in.length; i++){
			for(int j = 0; j < in[i].length; j++){
				f.brain.setInputWeight(i, j, in[i][j]);
			}
		}
		
		for(int i = 0; i < out.length; i++){
			for(int j = 0; j < out[i].length; j++){
				f.brain.setOutputWeight(i, j, out[i][j]);
			}
		}
		
		return f;
	}
	
	@Override
	public void tick(){
		brain.updateInputs(new double[]{
				//difference in x
				(getCenterX() - partner.getCenterX()) / (Config.FIGHT_ZONE_WIDTH * 1.5),
				//difference in y
				(getY() - partner.getY()) / (Config.FIGHT_ZONE_HEIGHT),
				//this speed, this dy
				getSpeed() / (Config.FIGHTER_MOVE_SPEED * 1.5), getDy() / (Config.FIGHTER_JUMP_SPEED * 2),
				//partner speed, partner dy
				partner.getSpeed() / (Config.FIGHTER_MOVE_SPEED * 1.5), partner.getDy() / (Config.FIGHTER_JUMP_SPEED * 2),
				//this attack time, this block time
				(getAttackTime() - 127) / 255.0, (getBlockTime() - 127) / 255.0,
				//partner attack time, partner block time
				(partner.getAttackTime() - 127) / 255.0, (partner.getBlockTime() - 127) / 255.0,
				//memory node
				brain.getOutput(5)
		});
		brain.calculateOutputs();
		leftDown = brain.getOutput(0) > 0;
		rightDown = brain.getOutput(1) > 0;
		jumpDown = brain.getOutput(2) > 0;
		attackDown = brain.getOutput(3) > 0;
		blockDown = brain.getOutput(4) > 0;
		
		super.tick();
	}
	
	public NeuralNet getBrain(){
		return brain;
	}
	/**
	 * Get the real number of notes this fighter has
	 * @return
	 */
	public int getIntNodes(){
		return intNodes;
	}
	
	/**
	 * Decides how many nodes this fighter will have based on the number of nodes it has
	 * @param rng
	 */
	private void determineIntNodes(RandomSave rng){
		double chance = numNodes - (int)numNodes;
		if(chance > rng.getRand()) intNodes = (int)(numNodes + 1);
		else intNodes = (int)numNodes;
	}
	
	@Override
	public void renderInfo(Graphics g, int x, int y){
		super.renderInfo(g, x, y);
		g.drawString("Nodes Num: " + getIntNodes() + " (" + numNodes + ")", x, y + 136);
		g.drawString("Node Mut: " + nodeMutability, x, y + 153);
		g.drawString("Mutability: " + mutability, x, y + 170);
		if(parentID < 0) g.drawString("Parent: WILD", x, y + 187);
		else g.drawString("Parent: " + parentID, x, y + 187);
	}
	
	/**
	 * Save this fighter to the given PrintWriter, the PRintWriter must be ready to immediately have the data printed
	 * @param write
	 */
	public void save(PrintWriter write){
		write.println("NeuralNetFighter");
		write.println("Num_nodes: " + numNodes);
		write.println("Int_nodes: " + intNodes);
		write.println("Node_mutability: " + nodeMutability);
		write.println("Mutability: " + mutability);
		write.println("Birth_Gen: " + getBirthGeneration());
		write.println("Fighter_ID: " + getFighterID());
		write.println("Parent_ID: " + parentID);
		write.println("Species: " + getSpecies());
		write.println("last_fought_ID: " + getLastFoughtID());
		write.println("Current_fitness: " + getCurrentFitness());
		write.println("Last_fought_on_left: " + Config.boolToNum(getLastFoughtOnLeft()));
		write.println("Fitness_history: " + getFitnessSize());
		for(int i = 0; i < getFitnessSize(); i++) write.print(getFitness(i) + " ");
		write.println();
		
		brain.save(write);
	}
	
	/**
	 * Load this fighter's data in, must be ready to immediately be read by the given Scanner
	 * @param scan
	 */
	public void load(Scanner scan){
		scan.next();
		scan.next(); numNodes = scan.nextDouble();
		scan.next(); intNodes = scan.nextInt();
		scan.next(); nodeMutability = scan.nextDouble();
		scan.next(); mutability = scan.nextDouble();
		scan.next(); setBirthGeneration(scan.nextInt());
		scan.next(); setFighterID(scan.nextInt());
		scan.next(); parentID = scan.nextInt();
		scan.next(); setSpecies(scan.nextInt());
		scan.next(); setLastFoughtID(scan.nextInt());
		scan.next(); setCurrentFitness(scan.nextDouble());
		scan.next(); setLastFoughtOnLeft(scan.nextInt() == 1);
		scan.next();
		int size = scan.nextInt();
		resetFitness();
		for(int i = 0; i < size; i++) addFitnessScore(scan.nextDouble());
		
		brain.load(scan);
	}
	
	@Override
	public void renderDisplayInfo(Graphics g, int x, int y){
		brain.render(g, x + 50, y + 10);
	}
	
}
