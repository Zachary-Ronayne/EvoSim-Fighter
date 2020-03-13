package fighter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import menu.Config;
import menu.Main;
import menu.MenuButton;
import menu.graph.GLine;
import menu.graph.LineGraph;
import sim.NeuralNet;

public class FighterArena{
	
	private Fighter fighter1;
	private Fighter fighter2;
	
	private int timer;
	
	private KeyAdapter keyInput;
	
	private boolean scoresRecorded;
	private double fighter1EndFitness;
	private double fighter2EndFitness;
	
	private LineGraph fitnessGraph;
	
	private boolean paused;
	private boolean showGraph;
	private boolean showNetLines;
	
	private MenuButton pauseButton;
	private MenuButton showGraphButton;
	private MenuButton showNetLinesButton;
	
	private static final Point FIGHTER1_VISUAL_POS = new Point(570, 40);
	private static final Point FIGHTER2_VISUAL_POS = new Point(1070, 40);
	
	/**
	 * Used to track the mouse for detecting which net lines should show
	 */
	private MenuButton netLinesDetection;
	
	public FighterArena(Main instance){
		reset();
		
		keyInput = new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e){
				super.keyPressed(e);
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_SPACE) instance.setSimState(Main.State.INFO);
			}
		};
		
		paused = false;
		pauseButton = new MenuButton(20, 500, 100, 50){
			@Override
			public void click(MouseEvent e){
				super.click(e);
				paused = !paused;
			}
			@Override
			public void render(Graphics g){
				super.render(g);
				g.setColor(Color.BLACK);
				g.setFont(new Font("Courier New", Font.BOLD, 15));
				g.drawString("Click to:", x + 4, y + 17);
				if(paused) g.drawString("Unpause", x + 4, y + 32);
				else g.drawString("Pause", x + 4, y + 32);
			}
		};

		showGraph = true;
		showGraphButton = new MenuButton(130, 500, 100, 50){
			@Override
			public void click(MouseEvent e){
				super.click(e);
				showGraph = !showGraph;
			}
			@Override
			public void render(Graphics g){
				super.render(g);
				g.setColor(Color.BLACK);
				g.setFont(new Font("Courier New", Font.BOLD, 15));
				g.drawString("Click to:", x + 4, y + 17);
				if(showGraph) g.drawString("Hide graph", x + 4, y + 32);
				else g.drawString("Show graph", x + 4, y + 32);
			}
		};
		
		showNetLines = true;
		showNetLinesButton = new MenuButton(240, 500, 200, 50){
			@Override
			public void click(MouseEvent e){
				super.click(e);
				showNetLines = !showNetLines;
				if(showNetLines){
					if(fighter1 instanceof NeuralNetFighter) ((NeuralNetFighter)fighter1).getBrain().updateNetLines(-1, -1);
					if(fighter2 instanceof NeuralNetFighter) ((NeuralNetFighter)fighter2).getBrain().updateNetLines(-1, -1);
				}
				else{
					if(fighter1 instanceof NeuralNetFighter) ((NeuralNetFighter)fighter1).getBrain().updateNetLines(3, -1);
					if(fighter2 instanceof NeuralNetFighter) ((NeuralNetFighter)fighter2).getBrain().updateNetLines(3, -1);
				}
			}
			@Override
			public void render(Graphics g){
				super.render(g);
				g.setColor(Color.BLACK);
				g.setFont(new Font("Courier New", Font.BOLD, 15));
				g.drawString("Click to:", x + 4, y + 17);
				if(showNetLines) g.drawString("Hover for net lines", x + 4, y + 32);
				else g.drawString("Always show net lines", x + 4, y + 32);
			}
		};
		
		netLinesDetection = new MenuButton(FIGHTER1_VISUAL_POS.x, FIGHTER1_VISUAL_POS.y, 1000, 1200){
			private int lastLayer = -1;
			private int lastPos = -1;
			private int lastBrain = -1;
			
			@Override
			public void update(){
				super.update();
				int newLayer = lastLayer;
				int newPos = lastPos;
				int newBrain = lastBrain;
				boolean inFighter = false;
				//see if the mouse is on a fighter 1 node
				if(fighter1 instanceof NeuralNetFighter){
					NeuralNet b = ((NeuralNetFighter)fighter1).getBrain();
					int x = getMX() - FIGHTER1_VISUAL_POS.x - 48;
					int y = getMY() - FIGHTER1_VISUAL_POS.y - 34;
					int brain = 0;
					int[] lengths = new int[]{b.getInputLength(), b.getHiddenLength(), b.getOutputLength()};
					for(int j = 0; j < 3 && !inFighter; j++){
						for(int i = 0; i < lengths[j] && !inFighter; i++){
							if(new Point(x, y).distance(b.getNodeLineX(j), b.getNodeLineY(j, i)) <= NeuralNet.OUTPUT_BOX_WIDTH / 2){
								if(!inFighter){
									newLayer = j;
									newPos = i;
									newBrain = brain;
									inFighter = true;
								}
							}
						}
					}
					if(!inFighter){
						newLayer = -1;
						newPos = -1;
						newBrain = -1;
					}
				}
				//see if the mouse is on a fighter 2 node
				if(!inFighter && fighter2 instanceof NeuralNetFighter){
					NeuralNet b = ((NeuralNetFighter)fighter2).getBrain();
					int x = getMX() - FIGHTER2_VISUAL_POS.x - 48;
					int y = getMY() - FIGHTER2_VISUAL_POS.y - 34;
					int brain = 1;
					int[] lengths = new int[]{b.getInputLength(), b.getHiddenLength(), b.getOutputLength()};
					for(int j = 0; j < 3 && !inFighter; j++){
						for(int i = 0; i < lengths[j] && !inFighter; i++){
							if(new Point(x, y).distance(b.getNodeLineX(j), b.getNodeLineY(j, i)) <= NeuralNet.OUTPUT_BOX_WIDTH / 2){
								if(!inFighter){
									newLayer = j;
									newPos = i;
									newBrain = brain;
									inFighter = true;
								}
							}
						}
					}
					if(!inFighter){
						newLayer = -1;
						newPos = -1;
						newBrain = -1;
					}
				}
				
				//update the corresponding fighter
				if(!(lastLayer == newLayer && lastPos == newPos && lastBrain == newBrain)){
					lastLayer = newLayer;
					lastPos = newPos;
					lastBrain = newBrain;
					//clear both brains
					if(newBrain == -1){
						if(fighter1 instanceof NeuralNetFighter) ((NeuralNetFighter)fighter1).getBrain().updateNetLines(3, -1);
						if(fighter2 instanceof NeuralNetFighter) ((NeuralNetFighter)fighter2).getBrain().updateNetLines(3, -1);
					}
					//clear fighter 2 and draw the nodes in fighter 1
					else if(newBrain == 0){
						if(fighter1 instanceof NeuralNetFighter) ((NeuralNetFighter)fighter1).getBrain().updateNetLines(newLayer, newPos);
						if(fighter2 instanceof NeuralNetFighter) ((NeuralNetFighter)fighter2).getBrain().updateNetLines(3, -1);
					}
					//clear fighter 1 and draw the nodes in fighter 2
					else if(newBrain == 1){
						if(fighter1 instanceof NeuralNetFighter) ((NeuralNetFighter)fighter1).getBrain().updateNetLines(3, -1);
						if(fighter2 instanceof NeuralNetFighter) ((NeuralNetFighter)fighter2).getBrain().updateNetLines(newLayer, newPos);
					}
				}
			}
		};
	}
	
	public void reset(){
		fitnessGraph = new LineGraph(new GLine[]{
			new GLine(Color.RED, 2, "F1"),
			new GLine(Color.BLUE, 2, "F2")
		}, 450, 300, 30, 100, 5, false);
		fitnessGraph.addValue(new Double[]{0.0, 0.0});
		
		fighter1EndFitness = 0;
		fighter2EndFitness = 0;
		
		timer = 0;
		
		scoresRecorded = false;
	}
	
	/**
	 * Send two fighters to the arena to fight, they will be linked to each other when the arena is created. 
	 * @param f1
	 * @param f2
	 * @param updateLines true if the neural net lines of the fighters should be updated, this should be true if you plan on calling render
	 */
	public void addFighters(Fighter f1, Fighter f2, boolean updateLines){
		fighter1 = f1;
		fighter2 = f2;
		f1.setCurrentFitness(0);
		f2.setCurrentFitness(0);
		f1.setParnter(f2);
		f2.setParnter(f1);
		f1.setX(10);
		f1.setY(Config.FIGHT_ZONE_HEIGHT - Config.FIGHTER_HEIGHT);
		f2.setX(Config.FIGHT_ZONE_WIDTH - Config.FIGHTER_WIDTH - 10);
		f2.setY(Config.FIGHT_ZONE_HEIGHT - Config.FIGHTER_HEIGHT);
		if(updateLines){
			if(f1 instanceof NeuralNetFighter) ((NeuralNetFighter)f1).getBrain().updateNetLines(-1, -1);
			if(f2 instanceof NeuralNetFighter) ((NeuralNetFighter)f2).getBrain().updateNetLines(-1, -1);
		}
	}
	
	public boolean fightOver(){
		return timer >= Config.MAX_FIGHT_TIME;
	}

	public double getFighter1EndFitness(){
		return fighter1EndFitness;
	}
	public double getFighter2EndFitness(){
		return fighter2EndFitness;
	}
	
	/**
	 * Update the arena
	 */
	public void tick(){
		if(!paused){
			timer++;
			
			fighter1.tick();
			fighter2.tick();
			
			if(fightOver() && !scoresRecorded){
				scoresRecorded = true;
				fighter1EndFitness = fighter1.getCurrentFitness();
				fighter2EndFitness = fighter2.getCurrentFitness();
			}
			
			fitnessGraph.addValue(new Double[]{(double)fighter1.getCurrentFitness(), (double)fighter2.getCurrentFitness()});
		}
		if(!showNetLines) netLinesDetection.update();
	}
	
	public void render(Graphics g){
		int y = Config.FIGHT_ZONE_HEIGHT;
		
		//line separating fight zone from info
		g.setColor(Color.BLACK);
		g.fillRect(0, y, Config.FIGHT_ZONE_WIDTH, 2);
		g.setColor(Color.BLACK);
		g.fillRect(Config.FIGHT_ZONE_WIDTH, 0, 2, 1000);
		g.fillRect(0, 450, Config.FIGHT_ZONE_WIDTH, 2);
		
		//instructions
		g.setFont(new Font("Courier New", Font.BOLD, 20));
		g.drawString("Press Spacebar to exit", 20, 480);
		
		//titles for each fighter
		g.setFont(new Font("Courier New", Font.BOLD, 15));
		g.setColor(fighter1.getColor().darker());
		g.drawString("Fighter 1: " + fighter1.getFighterID() + " S" + fighter1.getSpecies(), 10, y + 15);
		g.setColor(fighter2.getColor().darker());
		g.drawString("Fighter 2: " + fighter2.getFighterID() + " S" + fighter2.getSpecies(), 250, y + 15);
		
		//attack time of each fighter
		if(fighter1.getAttackTime() > Config.NEUTRAL_ATTACK_TIME) g.setColor(new Color(0, 0, 200));
		else if(fighter1.getAttackTime() < Config.NEUTRAL_ATTACK_TIME) g.setColor(new Color(200, 0, 0));
		else g.setColor(Color.BLACK);
		g.drawString("Attack " + fighter1.getAttackTime(), 10, y + 35);
		if(fighter2.getAttackTime() > Config.NEUTRAL_ATTACK_TIME) g.setColor(new Color(0, 0, 200));
		else if(fighter2.getAttackTime() < Config.NEUTRAL_ATTACK_TIME) g.setColor(new Color(200, 0, 0));
		else g.setColor(Color.BLACK);
		g.drawString("Attack " + fighter2.getAttackTime(), 250, y + 35);
		
		//block time of each fighter
		if(fighter1.getBlockTime() > Config.NEUTRAL_BLOCK_TIME) g.setColor(new Color(0, 0, 200));
		else if(fighter1.getBlockTime() < Config.NEUTRAL_BLOCK_TIME) g.setColor(new Color(200, 0, 0));
		else g.setColor(Color.BLACK);
		g.drawString("Block " + fighter1.getBlockTime(), 10, y + 55);
		if(fighter2.getBlockTime() > Config.NEUTRAL_BLOCK_TIME) g.setColor(new Color(0, 0, 200));
		else if(fighter2.getBlockTime() < Config.NEUTRAL_BLOCK_TIME) g.setColor(new Color(200, 0, 0));
		else g.setColor(Color.BLACK);
		g.drawString("Block " + fighter2.getBlockTime(), 250, y + 55);
		
		//fitness of eeach fighter
		g.setColor(Color.BLACK);
		g.drawString(removeDecimalPlaces("Fitness " + fighter1.getCurrentFitness()), 10, y + 75);
		g.drawString(removeDecimalPlaces("Fitness " + fighter2.getCurrentFitness()), 250, y + 75);
		
		//final score of each fighter
		if(fightOver()){
			g.drawString(removeDecimalPlaces("End fitness: " + fighter1EndFitness), 10, y + 95);
			g.drawString(removeDecimalPlaces("End fitness: " + fighter2EndFitness), 250, y + 95);
		}
		
		//show which buttons are pressed by each fighter
		boolean[][] on = new boolean[][]{
			{fighter1.leftDown, fighter1.rightDown, fighter1.jumpDown, fighter1.attackDown, fighter1.blockDown}, 
			{fighter2.leftDown, fighter2.rightDown, fighter2.jumpDown, fighter2.attackDown, fighter2.blockDown}
		};
		String[] print = new String[]{"L", "R", "J", "A", "B"};
		
		for(int i = 0; i < 2; i++){
			for(int j = 0; j < 5; j++){
				int jx = i * 240 + 26 * j;
				int jy = y + 115;
				g.setColor(Color.BLACK);
				g.fillRect(10 + jx, jy, 25, 25);
				if(on[i][j]) g.setColor(Color.RED);
				else g.setColor(Color.WHITE);
				g.fillRect(12 + jx, jy + 2, 21, 21);
				g.setColor(Color.BLACK);
				g.setFont(new Font("Courier New", Font.BOLD, 19));
				g.drawString(print[j], jx + 14, jy + 18);
			}
		}
		
		//render each fighter
		BufferedImage fightZone = new BufferedImage(Config.FIGHT_ZONE_WIDTH, Config.FIGHT_ZONE_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		Graphics g2 = fightZone.getGraphics();
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, fightZone.getWidth(), fightZone.getHeight());
		
		//draw the buttons for pausing, showing the graph, and toggling net lines
		pauseButton.render(g);
		showGraphButton.render(g);
		showNetLinesButton.render(g);
		
		//draw the fight zone
		double drawX = 0;
		int squareSize = 60;
		for(int i = 0; i < Config.FIGHT_ZONE_WIDTH; i += squareSize){
			for(int j = 0; j < Config.FIGHT_ZONE_HEIGHT; j += squareSize){
				if(i / squareSize % 2 == 0 && j / squareSize % 2 == 0 || i / squareSize % 2 == 1 && j / squareSize % 2 == 1) g2.setColor(new Color(250, 250, 250));
				else g2.setColor(new Color(200, 200, 200));
				g2.fillRect(i, j, squareSize, squareSize);
			}
		}
		fighter1.render(g2, (int)drawX, 0);
		fighter2.render(g2, (int)drawX, 0);
		g.drawImage(fightZone, 0, 0, null);

		//draw the visual info of each fighter (like the neural net is the brain)
		g.setColor(fighter1.getColor());
		g.setFont(new Font("Courier New", Font.BOLD, 30));
		g.drawString("Fighter 1", 570, 40);
		fighter1.renderDisplayInfo(g, FIGHTER1_VISUAL_POS.x, FIGHTER1_VISUAL_POS.y);
		g.setColor(fighter2.getColor());
		g.setFont(new Font("Courier New", Font.BOLD, 30));
		g.drawString("Fighter 2", 1070, 40);
		fighter2.renderDisplayInfo(g, FIGHTER2_VISUAL_POS.x, FIGHTER2_VISUAL_POS.y);
		
		//draw line seperating the info
		g.setColor(Color.BLACK);
		g.fillRect(1040, 0, 2, 1200);
		
		//draw the fitness graph of the fighters
		if(showGraph){
			int xx = 5;
			int yy = 600;
			g.setColor(Color.BLACK);
			g.fillRect(xx - 2, yy - 2, fitnessGraph.getWidth() + 4, fitnessGraph.getHeight() + 4);
			g.setColor(Color.WHITE);
			g.fillRect(xx - 1, yy - 1, fitnessGraph.getWidth() + 2, fitnessGraph.getHeight() + 2);
			if(!paused) fitnessGraph.updateLineGraph(fitnessGraph.getLineLength() - 1);
			fitnessGraph.render(g, xx, yy);
		}
		
		//TODO fix the random seed thing not loading it correctly, but it works when you start from gen zero
		//add animations to the fighters in the arena so it is easier to tell what the fighters are doing
	}
	
	/**
	 * @param s a string ending with a number with a decimal point
	 * @return a string that has a number at the end with exactly 4 decimal places
	 */
	private String removeDecimalPlaces(String s){
		s += "0000";
		return s.substring(0, s.indexOf(".") + 4);
	}
	
	/**
	 * Link this arena to component c so that key and mouse input can be used
	 * @param c
	 */
	public void linkToComponent(Component c){
		c.addKeyListener(keyInput);
		pauseButton.linkToComponent(c);
		showGraphButton.linkToComponent(c);
		showNetLinesButton.linkToComponent(c);
		netLinesDetection.linkToComponent(c);
	}
	/**
	 * Unlink this arena to component c so that key and mouse input cannot be used
	 * @param c
	 */
	public void unlinkFromComponent(Component c){
		c.removeKeyListener(keyInput);
		pauseButton.unlinkFromComponent(c);
		showGraphButton.unlinkFromComponent(c);
		showNetLinesButton.unlinkFromComponent(c);
		netLinesDetection.linkToComponent(c);
	}
}
