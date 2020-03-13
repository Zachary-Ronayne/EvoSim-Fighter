package sim;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import fighter.AIFighter;
import fighter.Fighter;
import fighter.FighterArena;
import fighter.NeuralNetFighter;
import fighter.UserFighter;
import menu.Config;
import menu.Main;
import menu.MenuButton;
import menu.MenuScroller;
import menu.graph.AreaGraph;
import menu.graph.GLine;
import menu.graph.LineGraph;

public class Simulation{
	
	/**
	 * Each gen, each fighter is tested with one random other fighter
	 */
	public final static int TEST_ONE = 0;
	/**
	 * Each gen, each fighter is tested against all fighters (VERY SLOW)
	 */
	public final static int TEST_ALL = 1;
	/**
	 * Each gen, each fighter is tested against the hard coded AI
	 */
	public final static int TEST_AI = 2;
	/**
	 * Each gen, each fighter is tested against the best fighter form the previous 10 gens, not including this gen. If there are not enough gens yet, the best from this gen are used, skipping themselves
	 */
	public final static int TEST_BEST = 3;
	/**
	 * Each gen, every fighter is tested against 4 random other fighters, and against the smart and expert AI, the AI fitness scores are added twice
	 */
	public final static int TEST_4_AND_BOTH_AI = 4;
	/**
	 * Each gen, every fighter is tested against all AIs, except for the random moving one
	 */
	public final static int TEST_ALL_AI = 5;
	/**
	 * Each gen, every fighter is tested against 5 random fighters and themselves, the score from themselves is added 5 times
	 */
	public final static int TEST_5_AND_SELF = 6;
	
	private MenuButton nextGen;
	private MenuButton loopGens;
	private MenuButton toggleView;
	private MenuButton enterFight;
	private MenuButton saveButton;
	
	/**
	 * The slider that allows you to select which generations informaiton is displayed
	 */
	private MenuScroller infoSlider;
	
	/**
	 * Buttons used to turn the 4 main graphs on or off
	 */
	private MenuButton[] toggleGraphButtons;
	/**
	 * keeps track of which graphs are turned on or off
	 */
	private boolean[] toggleGraphsOn;
	
	/**
	 * A slider that allows the user to go through the list of fighters from each generation
	 */
	private MenuScroller bestFighterSlider;
	
	/**
	 * A button used to click on in order to select one of the fighters from the bestFighters list
	 */
	private MenuButton bestFighterSelectButton;
	
	/**
	 * The random number generator which determines how random numbers will change in this simulation
	 */
	private RandomSave seededRNG;
	
	private boolean loopingGens;
	
	private boolean inInfoScreen;
	
	private int nextFighterID;
	private int currentGen;
	
	private Fighter[] fighters;
	private MenuButton[] fighterButtons;
	
	/**
	 * Tracks the oldest fighter in the list
	 */
	private int oldestFighter;
	
	private Fighter leftSelectedFighter;
	private Fighter rightSelectedFighter;
	
	private FighterArena mainArena;
	
	private MouseAdapter menuMouse;
	
	/**
	 * A list of all the indexes of the fighters who will die in the next generation
	 */
	private int[] fightersToDie;
	
	/**
	 * The fitness values from previous generations, the arraylist is infinite values. 
	 * array[0] is for the averages up to that generation, array[1] is for the best values of that generation. 
	 * array[][0] is the best, array[][1] is the median, array[][2] is the worst
	 */
	private ArrayList<Double[][]> fitnessHistory;
	/**
	 * The graph of the fitness history
	 */
	private LineGraph fitnessLineGraph;
	
	/**
	 * The amount of each species for each generation. 
	 * The arraylist is for each generaiton, the array is for each species
	 */
	private ArrayList<Integer[]> speciesHistory;
	
	/**
	 * The graph that tracks the graph of the species
	 */
	private AreaGraph speciesAreaGraph;
	
	/**
	 * The number of nodes in each fighter for each gen
	 */
	private ArrayList<Integer[]> nodeHistory;
	
	/**
	 * The graph for the number of nodes in each fighter for each gen
	 */
	private AreaGraph nodeAreaGraph;
	
	/**
	 * Shows the average number of nodes, the most nodes, and the least nodes in the current generation
	 */
	private LineGraph nodeCountLineGraph;
	
	/**
	 * The generation that should have its information displayed
	 */
	private int selectedGen;
	
	/**
	 * A list of the best fighters from each generation
	 */
	private ArrayList<NeuralNetFighter> bestFighters;
	
	public Simulation(Component c, Main instance, boolean makeNewGen){
		seededRNG = new RandomSave(Config.SEED);
		
		fitnessHistory = new ArrayList<Double[][]>();
		
		int numLines = 0;
		if(Config.DRAW_AVERAGE_LINES) numLines += 11;
		if(Config.DRAW_BEST_LINES) numLines += 11;
		GLine[] fitnessLines = new GLine[numLines];
		int cnt = 0;
		if(Config.DRAW_BEST_LINES){
			for(int i = 0; i < 8; i++){
				fitnessLines[cnt] = new GLine(new Color(0, 0, 255, 127), 1, "");
				cnt++;
			}
			fitnessLines[cnt] = new GLine(Color.BLUE, 2, "Best This Gen");
			fitnessLines[cnt + 1] = new GLine(new Color(0, 100, 0), 2, "Median This Gen");
			fitnessLines[cnt + 2] = new GLine(Color.BLUE, 2, "Worst This Gen");
			cnt = 11;
		}
		if(Config.DRAW_AVERAGE_LINES){
			for(int i = 0; i < 8; i++){
				fitnessLines[cnt] = new GLine(new Color(0, 0, 0, 127), 1, "");
				cnt++;
			}
			fitnessLines[cnt] = new GLine(Color.BLACK, 2, "Best Average");
			fitnessLines[cnt + 1] = new GLine(Color.RED, 2, "Median Average");
			fitnessLines[cnt + 2] = new GLine(Color.BLACK, 2, "Worst Average");
		}
		fitnessLineGraph = new LineGraph(fitnessLines, 1300, 400, 70, 300, 10, false);
		
		speciesHistory = new ArrayList<Integer[]>();
		speciesAreaGraph = new AreaGraph(speciesHistory, 1150, 275, "S");
		
		nodeHistory = new ArrayList<Integer[]>();
		nodeAreaGraph = new AreaGraph(nodeHistory, 1150, 275, "#Node");
		
		nodeCountLineGraph = new LineGraph(new GLine[]{
			new GLine(Color.BLUE, 2, "Most"),
			new GLine(new Color(0, 100, 0), 2, "Avg"),
			new GLine(Color.RED, 2, "Least")
		}, 600, 275, 20, 150, 5, false);
		
		toggleGraphsOn = new boolean[]{true, true, true, true, true, true};
		toggleGraphButtons = new MenuButton[6];
		for(int i = 0; i < toggleGraphButtons.length; i++){
			final int ii = i;
			final String[] text = new String[]{"Hide fitness line grpah", "Show fitness line grpah",
											   "Hide species area grpah", "Show species area grpah",
											   "Hide node num area grpah", "Show node num area grpah",
											   "Hide node num line grpah", "Show node num line grpah",
											   "Hide all graphs", "Hide all graphs",
											   "Show all graphs", "Show all graphs"};
			toggleGraphButtons[i] = new MenuButton(1200 + 260 * (i % 2), 450 + 60 * (i / 2), 250, 50){
				@Override
				public void click(MouseEvent e){
					super.click(e);
					if(ii < 4) toggleGraphsOn[ii] = !toggleGraphsOn[ii];
					else if(ii == 4){
						for(int i = 0; i < 4; i++) toggleGraphsOn[i] = false;
					}
					else if(ii == 5){
						for(int i = 0; i < 4; i++) toggleGraphsOn[i] = true;
					}
				}
				@Override
				public void render(Graphics g){
					super.render(g);
					g.setColor(Color.BLACK);
					g.setFont(new Font("Courier New", Font.BOLD, 15));
					String s;
					if(toggleGraphsOn[ii]) s = text[ii * 2];
					else s = text[ii * 2 + 1];
					g.drawString(s, getX() + 10, getY() + 30);
				}
			};
		}
		
		mainArena = new FighterArena(instance);
		
		leftSelectedFighter = null;
		rightSelectedFighter = null;
		
		loopingGens = false;
		inInfoScreen = true;
		
		fightersToDie = new int[50];
		
		fighterButtons = new MenuButton[101 + AIFighter.NUM_AI];
		for(int i = 0; i < fighterButtons.length; i++){
			final int ii = i;
			fighterButtons[i] = new MenuButton(5 + i % 10 * 80, 5 + i / 10 * 80, 80, 80){
				@Override
				public void click(MouseEvent e){
					super.click(e);
					if(containsMouse()){
						if(e.getButton() == MouseEvent.BUTTON1) leftSelectedFighter = fighters[ii];
						else if(e.getButton() == MouseEvent.BUTTON3) rightSelectedFighter = fighters[ii];
					}
				}
				@Override
				public void render(Graphics g){
					super.render(g);
					if(leftSelectedFighter != null && leftSelectedFighter.getFighterID() == fighters[ii].getFighterID()){
						g.setColor(new Color(100, 0, 0));
						g.fillRect(x + 2, y + 50, width - 4, 10);
					}
					if(rightSelectedFighter != null && rightSelectedFighter.getFighterID() == fighters[ii].getFighterID()){
						g.setColor(new Color(0, 0, 100));
						g.fillRect(x + 2, y + 60, width - 4, 10);
					}
				}
			};
		}
		
		for(int i = 0; i < 1 + AIFighter.NUM_AI; i++){
			fighterButtons[i + 100].setX(820 + 160 * (i / 5));
			fighterButtons[i + 100].setY(5 + 80 * (i % 5));
			fighterButtons[i + 100].setWidth(160);
		}
		
		menuMouse = new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if(loopingGens && e.getButton() == MouseEvent.BUTTON3){
					loopingGens = false;
					if(!Config.ALWAYS_UPDATE_GRAPHS){
						selectedGen = currentGen;
						updateGraphs();
					}
				}
			}
		};
		
		//Next Generation Button
		nextGen = new MenuButton(Main.SCREEN_WIDTH - 160, 55, 140, 30){
			@Override
			public void render(Graphics g){
				super.render(g);
				g.setColor(Color.BLACK);
				g.setFont(new Font("Courier New", Font.BOLD, 20));
				g.drawString("Next Gen", x + 4, y + 20);
			}
			@Override
			public void click(MouseEvent e){
				super.click(e);
				if(loopingGens){
					loopingGens = false;
					return;
				}
				nextGeneration();
			}
		};
		
		//Loop generations button
		loopGens = new MenuButton(Main.SCREEN_WIDTH - 160, 90, 140, 30){
			@Override
			public void render(Graphics g){
				super.render(g);
				if(loopingGens){
					g.setColor(new Color(200, 200, 200, 127));
					g.fillRect(getX(), getY(), getWidth(), getHeight());
					g.setColor(Color.BLACK);
					g.setFont(new Font("Courier New", Font.BOLD, 13));
					g.drawString("Hold right click", x + 4, y + 13);
					g.drawString("to stop looping", x + 4, y + 26);
				}
				else{
					g.setColor(Color.BLACK);
					g.setFont(new Font("Courier New", Font.BOLD, 20));
					g.drawString("Loop Gens", x + 4, y + 20);
				}
			}
			@Override
			public void click(MouseEvent e){
				super.click(e);
				loopingGens = true;
			}
		};
		
		//Switch from the graph screen or the fighters screen button
		toggleView = new MenuButton(Main.SCREEN_WIDTH - 160, 20, 140, 30){
			@Override
			public void render(Graphics g){
				super.render(g);
				g.setColor(Color.BLACK);
				g.setFont(new Font("Courier New", Font.BOLD, 20));
				g.drawString("Swap screen", x + 4, y + 20);
			}
			@Override
			public void click(MouseEvent e){
				super.click(e);
				//unlink the current screen buttons
				unlinkFromSim(c, inInfoScreen);
				//link the new screen buttons
				linkToSim(c, !inInfoScreen);
				inInfoScreen = !inInfoScreen;
				loopingGens = false;
			}
		};
		
		//Enter fight button
		enterFight = new MenuButton(Main.SCREEN_WIDTH - 160, 55, 140, 30){
			@Override
			public void render(Graphics g){
				super.render(g);
				g.setColor(Color.BLACK);
				g.setFont(new Font("Courier New", Font.BOLD, 20));
				g.drawString("Fight", x + 4, y + 20);
			}
			@Override
			public void click(MouseEvent e){
				super.click(e);
				loopingGens = false;
				if(leftSelectedFighter != null && rightSelectedFighter != null){
					boolean isUser = leftSelectedFighter instanceof UserFighter && rightSelectedFighter instanceof UserFighter;
					boolean isAI = leftSelectedFighter instanceof AIFighter && rightSelectedFighter instanceof AIFighter;
					boolean isNeuralNet = leftSelectedFighter instanceof NeuralNetFighter && rightSelectedFighter instanceof NeuralNetFighter;
					boolean equal = leftSelectedFighter == rightSelectedFighter;
					if(!equal || isUser || isAI || isNeuralNet){
						if(isUser){
							leftSelectedFighter = new UserFighter(0, 0, -1, -1, -1, false);
							rightSelectedFighter = new UserFighter(0, 0, -1, -1, -1, false);
						}
						if(isAI){
							leftSelectedFighter = new AIFighter(0, 0, -1, leftSelectedFighter.getFighterID(), leftSelectedFighter.getSpecies(), ((AIFighter)leftSelectedFighter).getAItype());
							rightSelectedFighter = new AIFighter(0, 0, -1, rightSelectedFighter.getFighterID(), rightSelectedFighter.getSpecies(), ((AIFighter)rightSelectedFighter).getAItype());
						}
						if(isNeuralNet && equal){
							rightSelectedFighter = ((NeuralNetFighter)leftSelectedFighter).getCopy();
						}
						leftSelectedFighter.reset(true);
						rightSelectedFighter.reset(false);
						if(leftSelectedFighter instanceof UserFighter) ((UserFighter)leftSelectedFighter).setUpControls(c);
						if(rightSelectedFighter instanceof UserFighter) ((UserFighter)rightSelectedFighter).setUpControls(c);
						mainArena.reset();
						mainArena.addFighters(leftSelectedFighter, rightSelectedFighter, true);
						instance.startFight();
					}
				}
			}
		};
		
		//Save button
		saveButton = new MenuButton(Main.SCREEN_WIDTH - 160, 125, 140, 30){
			@Override
			public void render(Graphics g){
				super.render(g);
				g.setColor(Color.BLACK);
				g.setFont(new Font("Courier New", Font.BOLD, 20));
				g.drawString("Save", x + 4, y + 20);
			}
			@Override
			public void click(MouseEvent e){
				super.click(e);
				instance.saveSimulation();
			}
		};
		
		//info slider for the line showing where on the graphs is selected
		selectedGen = 1;
		infoSlider = new MenuScroller(55, 418, 980, 25, 80, 930){
			@Override
			public void render(Graphics g){
				super.render(g);
				//line showing on the main 3 graphs what position is being looked at
				g.setColor(Color.BLACK);
				g.drawLine((int)(startX + endWidth * ((double)selectedGen / currentGen)), 5,
						   (int)(startX + endWidth * ((double)selectedGen / currentGen)), Main.SCREEN_HEIGHT);
				//other line for the node number graph
				if(toggleGraphsOn[3]) g.drawLine((int)(1200 + 430 * ((double)selectedGen / currentGen)), 735, (int)(1200 + 430 * ((double)selectedGen / currentGen)), 1010);
			}
			
			@Override
			protected void setSelectedValue(int g){
				boolean updateGraph = currentValue == selectedGen;
				super.setSelectedValue(g);
				selectedGen = currentValue;
				if(!loopingGens && updateGraph){
					updateGraphs();
				}
			}
		};
		
		//slider for selecting which fighter to use for fighting, allows the user to pick from the best fighters
		bestFighterSlider =	new MenuScroller(950, 500, 500, 25, 975, 450);
		
		//button for selecting a fighter from the bestFighters list
		bestFighterSelectButton = new MenuButton(bestFighterSlider.getX(), bestFighterSlider.getY() + 30, 500, 400){
			@Override
			public void click(MouseEvent e){
				super.click(e);
				if(bestFighterSlider.getCurrentValue() >= 1){
					if(e.getButton() == MouseEvent.BUTTON1){
						leftSelectedFighter = bestFighters.get(bestFighterSlider.getCurrentValue() - 1);
					}
					else if(e.getButton() == MouseEvent.BUTTON3){
						rightSelectedFighter = bestFighters.get(bestFighterSlider.getCurrentValue() - 1);
					}
				}
			}
			
			@Override
			public void render(Graphics g){
				super.render(g);
				if(bestFighterSlider.getCurrentValue() >= 1){
					NeuralNetFighter f = bestFighters.get(bestFighterSlider.getCurrentValue() - 1);
					f.renderInfo(g, getX() + 10, getY() + 30);
					if(leftSelectedFighter != null && leftSelectedFighter.getFighterID() == f.getFighterID()){
						g.setColor(new Color(100, 0, 0));
						g.fillRect(getX(), getY() + 300, getWidth(), 20);
					}
					if(rightSelectedFighter != null && rightSelectedFighter.getFighterID() == f.getFighterID()){
						g.setColor(new Color(0, 0, 100));
						g.fillRect(getX(), getY() + 320, getWidth(), 20);
					}
				}
			}
		};
		
		if(makeNewGen){
			updateGraphs();
			createNewSimulation();
		}
	}
	
	public void nextGeneration(){
		//first kill the weakest of the previous generation but only if this is not generation 0
		if(currentGen != 0){
			//kill the 50 fighters to die from last gen
			for(int i = 1; i < 100; i++){
				for(int j = 0; j < 50; j++){
					if(i == fightersToDie[j]) fighters[i] = null;
				}
			}
			
			//each of the 50 surviving fighters has a certain weight to make a child, and the best will always make a child
			Fighter[] living = new Fighter[50];
			int cnt = 0;
			//get a list of all 50 surviving fighters in living
			for(int i = 0; i < 100; i++){
				if(fighters[i] != null){
					living[cnt] = fighters[i];
					cnt++;
				}
			}
			Fighter[] offspring;
			//if the parent should die
			if(Config.KILL_PARENT){
				//The new list of 100 offspring
				offspring = new Fighter[100];
				//kill the rest of the fighters in the main fighter list
				for(int i = 0; i < 100; i++) fighters[i] = null;
				//the best fighter always reproduces
				for(int i = 0; i < 2; i++) offspring[i] = ((NeuralNetFighter)living[0]).getMutatedChild(currentGen + 1, nextFighterID++, seededRNG);
				cnt = 2;
				for(int i = 1; i < 50; i++){
					//pick an index weighted towards 0
					int index = (int)(50 * Math.pow(seededRNG.getRand(), 1.5));
					if(index > 49) index = 49;
					//the next two offspring is from the index
					offspring[cnt] = ((NeuralNetFighter)living[index]).getMutatedChild(currentGen + 1, nextFighterID++, seededRNG);
					cnt++;
					offspring[cnt] = ((NeuralNetFighter)living[index]).getMutatedChild(currentGen + 1, nextFighterID++, seededRNG);
					cnt++;
				}
			}
			//if the parent should live
			else{
				//only 50 offspring needed for when the parent lives
				offspring = new Fighter[50];
				//the first offspring is always from the best fighter
				offspring[0] = ((NeuralNetFighter)fighters[0]).getMutatedChild(currentGen + 1, nextFighterID++, seededRNG);
				//go through the rest of the offspring fighters
				for(int i = 1; i < 50; i++){
					//pick an index weighted towards 0
					int index = (int)(50 * (Math.pow(seededRNG.getRand(), 1.5)));
					if(index > 49) index = 49;
					//the next offspring is from the index
					offspring[i] = ((NeuralNetFighter)living[index]).getMutatedChild(currentGen + 1, nextFighterID++, seededRNG);
				}
			}
			
			//replace the dead fighters with the new offspring
			cnt = 0;
			for(int i = 0; i < fighters.length; i++){
				if(fighters[i] == null){
					fighters[i] = offspring[cnt];
					cnt++;
				}
			}
		}
		
		//pick which 50 fighters will die next generation
		selectFightersToDie();
		
		//now test the new generation
		
		//code to make the fighters fight one random other fighter
		if(Config.TEST_TYPE == TEST_ONE){
			//make an ArrayList with 0-99 numbers
			ArrayList<Integer> nums = new ArrayList<Integer>();
			for(int i = 0; i < 100; i++) nums.add(i);
			//while there are still numbers in the list, keep looping
			while(nums.size() > 0){
				//pick a random fighter for the left fighter
				int f1 = (int)((nums.size()) * seededRNG.getRand());
				leftSelectedFighter = fighters[nums.get(f1)];
				for(int i = 0; i < nums.size(); i++) if(i == f1) nums.remove(i);
				
				//pick a random fighter for the right fighter
				int f2 = (int)((nums.size()) * seededRNG.getRand());
				rightSelectedFighter = fighters[nums.get(f2)];
				for(int i = 0; i < nums.size(); i++) if(i == f2) nums.remove(i);

				testFighters();
			}
		}
		//code to make each fighter fight every other fighter on both sides
		else if(Config.TEST_TYPE == TEST_ALL){
			for(int h = 0; h < 100; h++){
				fighters[h].resetFitness();
			}
			
			for(int h = 0; h < 100; h++){
				for(int j = h + 1; j < 100; j++){
					if(seededRNG.getRand() > .5){
						leftSelectedFighter = fighters[h];
						rightSelectedFighter = fighters[j];
					}
					else{
						leftSelectedFighter = fighters[j];
						rightSelectedFighter = fighters[h];
					}

					testFighters();
				}
			}
		}
		//code to make each fighter fight the hard code ai
		else if(Config.TEST_TYPE == TEST_AI){
			for(int h = 0; h < 100; h++){
				int fights = 1;
				boolean random = Config.AI_TYPE == AIFighter.MOVE_RANDOM;
				if(random){
					fights = 5;
					fighters[h].resetFitness();
				}
				if(fighters[h].getFitnessSize() == 0 || random){
					for(int i = 0; i < fights; i++){
						
						leftSelectedFighter = fighters[h];
						rightSelectedFighter = new AIFighter(0, 0, -1, -100, -1, Config.AI_TYPE);

						testFighters();
					}
				}
			}
		}
		//test the fighters with the best from previous gens
		else if(Config.TEST_TYPE == TEST_BEST){
			//reset the fitness scores of all the fighters
			for(int h = 0; h < 100; h++){
				fighters[h].resetFitness();
			}
			
			ArrayList<Fighter> testers = new ArrayList<Fighter>();
			//get all the best fighters from the previous gens
			for(int pos = bestFighters.size() - 1; testers.size() < 10 && pos >= 0; pos--){
				boolean found = false;
				//make sure the selected best fighter is not in the current gen
				for(int i = 0; i < 100 && !found; i++){
					if(fighters[i].getFighterID() == bestFighters.get(pos).getFighterID()) found = true;
				}
				//if the selected best fighter is not in the current gen, then continue
				if(!found){
					//make sure the selected best fighter is not already in the testers list
					for(int j = 0; j < testers.size() && !found; j++){
						if(bestFighters.get(pos).getFighterID() == testers.get(j).getFighterID()) found = true;
					}
					//if the selected best fighter is not in the testers list already, then add it
					if(!found) testers.add(bestFighters.get(pos));
				}
			}
			//fill in the rest of the list with fighters from this gen
			if(testers.size() < 10) for(int i = 0; testers.size() < 11; i++) testers.add(fighters[i]);
			
			//test all the fighters against each of the testers
			for(int i = 0; i < 100; i++){
				int cnt = 0;
				for(int j = 0; j < 10; j++){
					leftSelectedFighter = fighters[i];
					//make sure you are not testing a fighter against itself
					do{
						rightSelectedFighter = testers.get(cnt);
						cnt++;
						if(cnt > testers.size() - 1) cnt = 0;
					}while(leftSelectedFighter.getFighterID() == rightSelectedFighter.getFighterID());
					
					testFighters();
				}
			}
		}
		else if(Config.TEST_TYPE == TEST_4_AND_BOTH_AI){
			//reset the fitness scores of all the fighters
			for(int h = 0; h < 100; h++){
				fighters[h].resetFitness();
			}
			
			//test every fighter
			for(int h = 0; h < 100; h++){
				int fights = 4;
				//all the indexes the given fighter can choose to fight
				ArrayList<Integer> indexes = new ArrayList<Integer>();
				for(int i = 0; i < 100; i++) if(i != h) indexes.add(i);
				for(int i = 0; i < fights; i++){
					
					leftSelectedFighter = fighters[h];
					//pick an index for the other fighter
					int index = (int)(indexes.size() * seededRNG.getRand());
					rightSelectedFighter = fighters[indexes.get(index)];
					indexes.remove(index);
					
					testFighters();
				}
				
				//test the fighter against both the smart and expert AIs, then adds the score from each fight a second time
				leftSelectedFighter = fighters[h];
				rightSelectedFighter = fighters[101];
				testFighters();
				leftSelectedFighter.addFitnessScore(leftSelectedFighter.getLastRecordedFitness());
				
				leftSelectedFighter = fighters[h];
				rightSelectedFighter = fighters[106];
				testFighters();
				leftSelectedFighter.addFitnessScore(leftSelectedFighter.getLastRecordedFitness());
			}
		}
		else if(Config.TEST_TYPE == TEST_ALL_AI){
			//test every fighter
			for(int h = 0; h < 100; h++){
				//only test the fighter if they have not been tested before
				if(fighters[h].getFitnessSize() == 0){
					//test the fighter against the smart AI
					leftSelectedFighter = fighters[h];
					rightSelectedFighter = fighters[101];
					testFighters();
					//test the fighter against the none AI
					leftSelectedFighter = fighters[h];
					rightSelectedFighter = fighters[102];
					testFighters();
					//test the fighter against the go left AI
					leftSelectedFighter = fighters[h];
					rightSelectedFighter = fighters[103];
					testFighters();
					//test the fighter against the move right AI
					leftSelectedFighter = fighters[h];
					rightSelectedFighter = fighters[105];
					testFighters();
					//test the fighter against the expert AI
					leftSelectedFighter = fighters[h];
					rightSelectedFighter = fighters[106];
					testFighters();
					//test the fighter against the defense AI
					leftSelectedFighter = fighters[h];
					rightSelectedFighter = fighters[107];
					testFighters();
				}
			}
		}
		else if(Config.TEST_TYPE == TEST_5_AND_SELF){
			//test every fighter
			for(int h = 0; h < 100; h++){
				fighters[h].resetFitness();
			}
			for(int h = 0; h < 100; h++){
				for(int i = 0; i < 5; i++){
					if(seededRNG.getRand() < .5){
						leftSelectedFighter = fighters[h];
						rightSelectedFighter = fighters[(int)(100 * seededRNG.getRand())];
					}
					else{
						leftSelectedFighter = fighters[(int)(100 * seededRNG.getRand())];
						rightSelectedFighter = fighters[h];
					}
					testFighters();
				}
				if(seededRNG.getRand() < .5){
					leftSelectedFighter = fighters[h];
					rightSelectedFighter = ((NeuralNetFighter)fighters[h]).getCopy();
				}
				else{
					leftSelectedFighter = ((NeuralNetFighter)fighters[h]).getCopy();
					rightSelectedFighter = fighters[h];
				}
				testFighters();
				for(int i = 0; i < 5; i++) fighters[h].addFitnessScore(fighters[h].getLastRecordedFitness());
			}
		}
		
		//after all fighters have been tested, go on to the next generation
		currentGen++;
		
		//sort the fighters
		sortFighters();
		
		//add the best fighter of this gen to the list
		bestFighters.add((NeuralNetFighter)fighters[0]);
		
		double avgB = 0, avgM = 0, avgW = 0, best = 0, median = 0, worst = 0;
		avgB = avgW = fighters[0].getAverageFitness();
		//find averages for best and worst
		for(int i = 1; i < 100; i++){
			if(avgB < fighters[i].getAverageFitness()) avgB = fighters[i].getAverageFitness();
			if(avgW > fighters[i].getAverageFitness()) avgW = fighters[i].getAverageFitness();
		}
		//find the median of the average values
		double[] values = new double[100];
		for(int i = 0; i < 100; i++) values[i] = fighters[i].getAverageFitness();
		for(int i = 0; i < 100; i++){
			int least = i;
			for(int j = i; j < 100; j++){
				if(values[j] < values[least]) least = j;
			}
			double temp = values[i];
			values[i] = values[least];
			values[least] = temp;
		}
		//get the 10-40 percentile lines and the 60-90 percentile lines for average values
		double[] avgPercentile = new double[8];
		for(int i = 0; i < 2; i++){
			for(int j = 0; j < 4; j++){
				avgPercentile[j + i * 4] = fighters[10 * (j + 1 + i * 5) - 1].getAverageFitness();
			}
		}
		avgM = values[49];
		//find best values from this generation for best and worst
		best = worst = fighters[0].getLastRecordedFitness();
		for(int i = 1; i < 100; i++){
			if(best < fighters[i].getLastRecordedFitness()) best = fighters[i].getLastRecordedFitness();
			if(worst > fighters[i].getLastRecordedFitness()) worst = fighters[i].getLastRecordedFitness();
		}
		//find the median of the values this generation
		values = new double[100];
		for(int i = 0; i < 100; i++) values[i] = fighters[i].getLastRecordedFitness();
		for(int i = 0; i < 100; i++){
			int least = i;
			for(int j = i; j < 100; j++){
				if(values[j] < values[least]) least = j;
			}
			double temp = values[i];
			values[i] = values[least];
			values[least] = temp;
		}
		median = values[49];

		//get the 10-40 percentile lines and the 60-90 percentile lines for current values
		double[] currentPercentile = new double[8];
		for(int i = 0; i < 2; i++){
			for(int j = 0; j < 4; j++){
				currentPercentile[j + i * 4] = fighters[10 * (j + 1 + i * 5) - 1].getLastRecordedFitness();
			}
		}
		//update the selected generation
		selectedGen = currentGen;
		
		//add the new data to the history
		fitnessHistory.add(new Double[][]{
				{avgPercentile[0], avgPercentile[1], avgPercentile[2], avgPercentile[3],
				 avgPercentile[4], avgPercentile[5], avgPercentile[6], avgPercentile[7],
				 avgB, avgM, avgW},
				{currentPercentile[0], currentPercentile[1], currentPercentile[2], currentPercentile[3],
				 currentPercentile[4], currentPercentile[5], currentPercentile[6], currentPercentile[7],
				 best, median, worst}
				});

		if(Config.DRAW_AVERAGE_LINES && !Config.DRAW_BEST_LINES) fitnessLineGraph.addValue(new Double[]{
				avgPercentile[0], avgPercentile[1], avgPercentile[2], avgPercentile[3],
				avgPercentile[4], avgPercentile[5], avgPercentile[6], avgPercentile[7],
				avgB, avgM, avgW
			});
		else if(!Config.DRAW_AVERAGE_LINES && Config.DRAW_BEST_LINES) fitnessLineGraph.addValue(new Double[]{
				currentPercentile[0], currentPercentile[1], currentPercentile[2], currentPercentile[3],
				currentPercentile[4], currentPercentile[5], currentPercentile[6], currentPercentile[7],
				best, median, worst
			});
		else if(Config.DRAW_AVERAGE_LINES && Config.DRAW_BEST_LINES) fitnessLineGraph.addValue(new Double[]{
				currentPercentile[0], currentPercentile[1], currentPercentile[2], currentPercentile[3],
				currentPercentile[4], currentPercentile[5], currentPercentile[6], currentPercentile[7],
				best, median, worst,
				avgPercentile[0], avgPercentile[1], avgPercentile[2], avgPercentile[3],
				avgPercentile[4], avgPercentile[5], avgPercentile[6], avgPercentile[7],
				avgB, avgM, avgW
			});
		
		//store the species information
		speciesHistory.add(new Integer[100]);
		for(int i = 0; i < 100; i++) speciesHistory.get(speciesHistory.size() - 1)[i] = 0;
		for(int i = 0; i < 100; i++) speciesHistory.get(speciesHistory.size() - 1)[fighters[i].getSpecies()]++;
		
		//find the number of nodes in each fighter's brain this gen
		nodeHistory.add(new Integer[Config.MAX_NODES + 1]);
		for(int i = 0; i < Config.MAX_NODES + 1; i++) nodeHistory.get(nodeHistory.size() - 1)[i] = 0;
		for(int i = 0; i < 100; i++) nodeHistory.get(nodeHistory.size() - 1)[((NeuralNetFighter)fighters[i]).getIntNodes()]++;
		
		//find the average number of nodes, the most amount of nodes, and the least amount of nodes
		double averageNodes = 0;
		int leastNodes = -1;
		int mostNodes = -1;
		int total = 0;
		for(int i = 0; i < 100; i++){
			NeuralNetFighter f = ((NeuralNetFighter)fighters[i]);
			total += f.getIntNodes();
			if(leastNodes == -1 || f.getIntNodes() < leastNodes) leastNodes = f.getIntNodes();
			if(mostNodes == -1 || f.getIntNodes() > mostNodes) mostNodes = f.getIntNodes();
		}
		averageNodes = total / 100.0;
		//update the node line graph
		nodeCountLineGraph.addValue(new Double[]{(double)mostNodes, averageNodes, (double)leastNodes});
		
		//update the graphs
		if(loopingGens && Config.ALWAYS_UPDATE_GRAPHS || !loopingGens){
			updateGraphs();
		}
		
		//update the oldest fighter
		findOldestFighter();
		
		//update the menu scroller
		infoSlider.setMaxValue(currentGen);
		infoSlider.setCurrentValue(currentGen);
		bestFighterSlider.setMaxValue(currentGen);
		bestFighterSlider.setCurrentValue(currentGen);
	}
	
	/**
	 * Call this to test both fighters in the arena and add their scores to thir fitness
	 */
	private void testFighters(){
		//fight the fighters
		mainArena.reset();
		
		//set up both fighters on the corresponding sides
		leftSelectedFighter.setLastFoughtID(rightSelectedFighter.getFighterID());
		leftSelectedFighter.setLastFoughtOnLeft(true);
		rightSelectedFighter.setLastFoughtID(leftSelectedFighter.getFighterID());
		rightSelectedFighter.setLastFoughtOnLeft(false);
		
		//reset both fighters
		leftSelectedFighter.reset(true);
		rightSelectedFighter.reset(false);
		
		//add the fighters to the arena
		mainArena.addFighters(leftSelectedFighter, rightSelectedFighter, false);
		
		//simulate the entire fight
		while(!mainArena.fightOver()){
			mainArena.tick();
		}
		
		//add the new fitness score to both fighters
		leftSelectedFighter.addFitnessScore(mainArena.getFighter1EndFitness());
		rightSelectedFighter.addFitnessScore(mainArena.getFighter2EndFitness());
	}
	
	private void selectFightersToDie(){
		//worst fighter always dies next time
		fightersToDie[0] = 99;
		//the list of fighter indexes that can still die
		ArrayList<Integer> canDie = new ArrayList<Integer>();
		//populate the canDie list with all valid indexes
		for(int i = 1; i < 99; i++) canDie.add(i);
		//pick 49 indexes from canDie and put them in the fighters to die list
		for(int i = 0; i < 49; i++){
			//pick an index to kill, weighted towards the end of the list
			int index = (int)((1 - Math.pow(seededRNG.getRand(), 10)) * canDie.size());
			if(index >= canDie.size()) index = canDie.size() - 1;
			fightersToDie[i + 1] = canDie.get(index);
			canDie.remove(index);
		}
	}
	
	private void findOldestFighter(){
		oldestFighter = 0;
		for(int i = 1; i < 100; i++) if(fighters[oldestFighter].getFighterID() > fighters[i].getFighterID()) oldestFighter = i;
	}
	
	private void sortFighters(){
		Fighter[] sortedFighters = new Fighter[100];
		for(int j = 0; j < 100; j++){
			int highest = -1;
			for(int i = 0; i < 100 && highest == -1; i++){
				if(fighters[i] != null) highest = i;
			}
			for(int i = highest; i < 100; i++){
				if(Config.TEST_ON_AVERAGE){
					if(fighters[i] != null && fighters[highest] != null && fighters[i].getAverageFitness() >= fighters[highest].getAverageFitness()){
						highest = i;
					}
				}
				else {
					if(fighters[i] != null && fighters[highest] != null && fighters[i].getLastRecordedFitness() >= fighters[highest].getLastRecordedFitness()){
						highest = i;
					}
				}
			}

			sortedFighters[j] = fighters[highest];
			fighters[highest] = null;
		}
		for(int i = 0; i < 100; i++) fighters[i] = sortedFighters[i];
	}
	
	/**
	 * Reset all current data and make a brand new generation 1
	 */
	public void createNewSimulation(){
		nextFighterID = 0;
		currentGen = 0;
		
		fighters = new Fighter[101 + AIFighter.NUM_AI];
		for(int i = 0; i < 100; i++) fighters[i] = new NeuralNetFighter(0, 0, 0, nextFighterID++, i, seededRNG);
		setMiscFighters();
		
		bestFighters = new ArrayList<NeuralNetFighter>();
		
		nextGeneration();
	}
	
	private void setMiscFighters(){
		fighters[100] = new UserFighter(0, 0, -1, -1, -1, false);
		fighters[101] = new AIFighter(0, 0, -1, -2, -1, AIFighter.SMART);
		fighters[102] = new AIFighter(0, 0, -1, -3, -1, AIFighter.NONE);
		fighters[103] = new AIFighter(0, 0, -1, -4, -1, AIFighter.GO_LEFT);
		fighters[104] = new AIFighter(0, 0, -1, -5, -1, AIFighter.MOVE_RANDOM);
		fighters[105] = new AIFighter(0, 0, -1, -6, -1, AIFighter.JUMP_RIGHT);
		fighters[106] = new AIFighter(0, 0, -1, -7, -1, AIFighter.EXPERT);
		fighters[107] = new AIFighter(0, 0, -1, -8, -1, AIFighter.DEFENSE);
		fighters[108] = new AIFighter(0, 0, -1, -9, -1, AIFighter.RANDOM_ATTACK);
	}
	
	/**
	 * Link a component to this sim for mouse input
	 * @param c
	 * @param menu true if this should link the menu buttons, false if it should link the fighter screen buttons
	 */
	public void linkToSim(Component c, boolean menu){
		if(menu){
			nextGen.linkToComponent(c);
			loopGens.linkToComponent(c);
			infoSlider.linkToComponent(c);
			saveButton.linkToComponent(c);
			for(int i = 0; i < toggleGraphButtons.length; i++) toggleGraphButtons[i].linkToComponent(c);
			c.addMouseListener(menuMouse);
		}
		else{
			enterFight.linkToComponent(c);
			bestFighterSlider.linkToComponent(c);
			bestFighterSelectButton.linkToComponent(c);
			for(int i = 0; i < fighterButtons.length; i++) fighterButtons[i].linkToComponent(c);
		}
		toggleView.linkToComponent(c);
	}
	/**
	 * Unlink a component to this sim for mouse input
	 * @param c
	 * @param menu true if this should unlink the menu buttons, false if it should unlink the fighter screen buttons
	 */
	public void unlinkFromSim(Component c, boolean menu){
		if(menu){
			nextGen.unlinkFromComponent(c);
			loopGens.unlinkFromComponent(c);
			infoSlider.unlinkFromComponent(c);
			saveButton.unlinkFromComponent(c);
			for(int i = 0; i < toggleGraphButtons.length; i++) toggleGraphButtons[i].unlinkFromComponent(c);
			c.removeMouseListener(menuMouse);
		}
		else{
			enterFight.unlinkFromComponent(c);
			bestFighterSlider.unlinkFromComponent(c);
			bestFighterSelectButton.unlinkFromComponent(c);
			for(int i = 0; i < fighterButtons.length; i++) fighterButtons[i].unlinkFromComponent(c);
		}
		toggleView.unlinkFromComponent(c);
	}
	
	/**
	 * Resets the state of the menu for this simulation. This DOES NOT reset the data in the simulation
	 * @param c
	 */
	public void reset(Component c){
		leftSelectedFighter = null;
		rightSelectedFighter = null;
		unlinkFromSim(c, false);
		linkToSim(c, true);
		inInfoScreen = true;
	}
	
	public FighterArena getFighterArena(){
		return mainArena;
	}
	
	/**
	 * Get the object for this simulations seeded random number generator
	 * @return
	 */
	public RandomSave getSeededRNG(){
		return seededRNG;
	}
	
	public boolean isLoopingGens(){
		return loopingGens;
	}
	
	public void tick(){
		if(inInfoScreen){
			if(loopingGens) nextGeneration();
			infoSlider.update();
		}
		else{
			bestFighterSlider.update();
		}
	}
	
	public void render(Graphics g){
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);

		if(inInfoScreen){
			nextGen.render(g);
			loopGens.render(g);
			saveButton.render(g);
			g.setColor(Color.BLACK);
			g.setFont(new Font("Courier New", Font.BOLD, 20));
			g.drawString("Gen: " + currentGen, Main.SCREEN_WIDTH - 160, 175);
			
			int x = 1320, y = 40;
			
			//draw stats of best fighter of the selected gen
			if(selectedGen > 0){
				g.setColor(Color.BLACK);
				g.setFont(new Font("Courier New", Font.BOLD, 20));
				g.drawString("Best in gen: " + selectedGen, x, y - 10);
				BufferedImage statsImage = new BufferedImage(300, 200, BufferedImage.TYPE_4BYTE_ABGR);
				bestFighters.get(selectedGen - 1).renderInfo(statsImage.getGraphics(), 5, 10);
				g.setColor(Color.BLACK);
				g.fillRect(x - 2, y - 2, statsImage.getWidth() + 4, statsImage.getHeight() + 4);
				g.setColor(Color.WHITE);
				g.fillRect(x - 1, y - 1, statsImage.getWidth() + 2, statsImage.getHeight() + 2);
				g.drawImage(statsImage, x, y, null);
			}
			
			//buttons for toggling graphs on and off
			for(int i = 0; i < toggleGraphButtons.length; i++) toggleGraphButtons[i].render(g);
			
			if(toggleGraphsOn[0]){
				//draw fitness graph
				if(fitnessLineGraph != null){
					x = 10;
					y = 10;
					g.setColor(Color.BLACK);
					g.fillRect(x - 2, y - 2, fitnessLineGraph.getWidth() + 4, fitnessLineGraph.getHeight() + 4);
					g.setColor(Color.WHITE);
					g.fillRect(x - 1, y - 1, fitnessLineGraph.getWidth() + 2, fitnessLineGraph.getHeight() + 2);
					fitnessLineGraph.render(g, x, y);
				}
			}
			
			if(toggleGraphsOn[1]){
				//draw species graph
				if(speciesAreaGraph != null){
					x = 10;
					y = 450;
					g.setColor(Color.BLACK);
					g.fillRect(x - 2, y - 2, speciesAreaGraph.getWidth() + 4, speciesAreaGraph.getHeight() + 4);
					g.setColor(Color.WHITE);
					g.fillRect(x - 1, y - 1, speciesAreaGraph.getWidth() + 2, speciesAreaGraph.getHeight() + 2);
					speciesAreaGraph.render(g, x, y);
				}
			}
			
			if(toggleGraphsOn[2]){
				//draw node area graph
				if(nodeAreaGraph != null){
					x = 10;
					y = speciesAreaGraph.getHeight() + 460;
					g.setColor(Color.BLACK);
					g.fillRect(x - 2, y - 2, nodeAreaGraph.getWidth() + 4, nodeAreaGraph.getHeight() + 4);
					g.setColor(Color.WHITE);
					g.fillRect(x - 1, y - 1, nodeAreaGraph.getWidth() + 2, nodeAreaGraph.getHeight() + 2);
					nodeAreaGraph.render(g, x, y);
				}
			}

			if(toggleGraphsOn[3]){
				//draw node line graph
				if(nodeCountLineGraph != null){
					x = 1180;
					y = 735;
					g.setColor(Color.BLACK);
					g.fillRect(x - 2, y - 2, nodeCountLineGraph.getWidth() + 4, nodeCountLineGraph.getHeight() + 4);
					g.setColor(Color.WHITE);
					g.fillRect(x - 1, y - 1, nodeCountLineGraph.getWidth() + 2, nodeCountLineGraph.getHeight() + 2);
					nodeCountLineGraph.render(g, x, y);
				}
			}
			
			infoSlider.render(g);
		}
		else{
			g.setColor(Color.BLACK);
			g.setFont(new Font("Courier New", Font.BOLD, 20));
			g.drawString("Gen: " + currentGen, Main.SCREEN_WIDTH - 160, 120);
			
			enterFight.render(g);
			
			//draw each of the buttons for selecting fighters
			for(int i = 0; i < fighterButtons.length; i++) fighterButtons[i].render(g);
			for(int i = 0; i < fighterButtons.length; i++){
				fighterButtons[i].render(g);
				g.setColor(Color.BLACK);
				
				String text = "" + fighters[i].getFighterID();
				if(i >= 100) g.setFont(new Font("Courier New", Font.BOLD, 15));
				else g.setFont(new Font("Courier New", Font.BOLD, 10));
				if(i == 100) text = "User Conrol";
				else if(i == 101) text = "Smart AI";
				else if(i == 102) text = "None AI";
				else if(i == 103) text = "Go Left AI";
				else if(i == 104) text = "Random AI";
				else if(i == 105) text = "Jump Right AI";
				else if(i == 106) text = "Expert AI";
				else if(i == 107) text = "Defense AI";
				else if(i == 108) text = "Pseudo Random AI";
				g.drawString(text, fighterButtons[i].getX() + 2, fighterButtons[i].getY() + 40);
			}
			
			//draw the slider for selecting from the best fighters which to fight
			bestFighterSlider.render(g);
			g.setColor(Color.BLACK);
			g.setFont(new Font("Courier New", Font.BOLD, 20));
			g.drawString("Select best fighter of gen: " + bestFighterSlider.getCurrentValue(), bestFighterSlider.getX() + 30, bestFighterSlider.getY() - 8);
			bestFighterSelectButton.render(g);
			
			//draw the info for the fighters
			if(leftSelectedFighter != null){
				g.setColor(new Color(100, 0, 0));
				g.fillRect(2, 804, 6, 300);
				leftSelectedFighter.renderInfo(g, 10, 820);
			}
			if(rightSelectedFighter != null){
				g.setColor(new Color(0, 0, 100));
				g.fillRect(492, 804, 6, 300);
				rightSelectedFighter.renderInfo(g, 500, 820);
			}
			
			//highlight the fighter that the selected fighters last fought
			if(leftSelectedFighter != null || rightSelectedFighter != null){
				boolean done1 = false, done2 = false;
				for(int i = 0; i < 100 && (!done1 || !done2); i++){
					if(fighters[i].getLastFoughtID() >= 0){
						if(leftSelectedFighter != null){
							if(fighters[i].getLastFoughtID() == leftSelectedFighter.getFighterID()){
								g.setColor(new Color(100, 0, 0, 70));
								g.fillRect(fighterButtons[i].getX(), fighterButtons[i].getY(), fighterButtons[i].getWidth(), fighterButtons[i].getHeight());
							}
						}
						if(rightSelectedFighter != null){
							if(fighters[i].getLastFoughtID() == rightSelectedFighter.getFighterID()){
								g.setColor(new Color(0, 0, 100, 70));
								g.fillRect(fighterButtons[i].getX(), fighterButtons[i].getY(), fighterButtons[i].getWidth(), fighterButtons[i].getHeight());
							}
						}
					}
				}
			}
			
			//highlight the fighters that will die next generation in gray and the ones who were just born in green, and the oldest fighter
			for(int i = 0; i < 100; i++){
				int x = fighterButtons[i].getX();
				int y = fighterButtons[i].getY();
				int w = fighterButtons[i].getWidth();
				int h = fighterButtons[i].getHeight();
				for(int j = 0; j < 50; j++){
					if(i == fightersToDie[j]){
						g.setColor(new Color(0, 0, 0, 50));
						g.fillRect(x, y, w, h);
					}
				}
				if(fighters[i].getBirthGeneration() == currentGen){
					g.setColor(new Color(0, 255, 0, 30));
					g.fillRect(x, y, w, h);
				}

				//highlight the oldest fighter
				if(i == oldestFighter){
					g.setColor(new Color(0, 0, 255, 127));
					g.fillRect(x, y, w, 6);
					g.fillRect(x, y + 6, 6, h - 6);
					g.fillRect(x + 6, y + h - 6, w - 6, 6);
					g.fillRect(x + w - 6, y + 6, 6, h - 12);
				}
			}
			
		}
		toggleView.render(g);
	}
	
	private void updateGraphs(){
		if(toggleGraphsOn[0]) fitnessLineGraph.updateLineGraph(selectedGen);

		speciesAreaGraph.updateValues(speciesHistory);
		if(toggleGraphsOn[1]) speciesAreaGraph.update(Config.DRAW_SEPERATION_LINES, Config.POLY_GRAPH, selectedGen);

		nodeAreaGraph.updateValues(nodeHistory);
		if(toggleGraphsOn[2]) nodeAreaGraph.update(Config.DRAW_SEPERATION_LINES, Config.POLY_GRAPH, selectedGen);
		
		if(toggleGraphsOn[3]) nodeCountLineGraph.updateLineGraph(selectedGen);
	}
	
	/**
	 * Writes the data of this simulation to the given PrintWriter, it must be ready to have the data directly printed
	 * @param write
	 */
	public void save(PrintWriter write){
		//save simple data
		write.println("Simulation_data:");
		write.println("Saved_Seed: " + seededRNG.getSavedSeed());
		write.println("Saved_Seed_Count: " + seededRNG.getSeedCount());
		write.println("Next_Fighter_Id: " + nextFighterID);
		write.println("Current_Generation: " + currentGen);
		
		//save fighters
		write.println("\nFighters:\n");
		for(int i = 0; i < 100; i++) ((NeuralNetFighter)fighters[i]).save(write);
		
		//save best fighters
		write.println("Best_Fighters: " + bestFighters.size() + "\n");
		for(int i = 0; i < bestFighters.size(); i++) bestFighters.get(i).save(write);
		
		//save fitness history
		write.println("\nFitness_History: " + fitnessHistory.size() + " " + fitnessHistory.get(0)[0].length + "\n");
		for(int i = 0; i < fitnessHistory.size(); i++){
			for(int j = 0; j < fitnessHistory.get(i)[0].length; j++){
				write.print(fitnessHistory.get(i)[0][j] + " ");
			}
			write.println();
			for(int j = 0; j < fitnessHistory.get(i)[1].length; j++){
				write.print(fitnessHistory.get(i)[1][j] + " ");
			}
			write.println();
		}

		//save species history
		write.println("\nSpecies_History: " + speciesHistory.get(0).length + "\n");
		for(int i = 0; i < speciesHistory.size(); i++){
			for(int j = 0; j < speciesHistory.get(i).length; j++){
				write.print(speciesHistory.get(i)[j] + " ");
			}
			write.println();
		}
		
		//save node amount history
		write.println("\nNode_Area_History: " + nodeHistory.get(0).length + "\n");
		for(int i = 0; i < nodeHistory.size(); i++){
			for(int j = 0; j < nodeHistory.get(i).length; j++){
				write.print(nodeHistory.get(i)[j] + " ");
			}
			write.println();
		}

		//save node count history
		write.println("\nNode_Count_History: " + nodeCountLineGraph.getLines().length + "\n");
		for(int i = 0; i < nodeCountLineGraph.getLineLength(); i++){
			for(int j = 0; j < nodeCountLineGraph.getLines().length; j++){
				write.print(nodeCountLineGraph.getLines()[j].getValue(i) + " ");
			}
			write.println();
		}
	}
	
	/**
	 * Loads the data into this simulator from the given Scanner, must be ready to read in the data
	 * @param scan
	 */
	public void load(Scanner scan){
		//load in simple data
		scan.next();
		scan.next(); seededRNG = new RandomSave(scan.nextLong());
		scan.next(); long count = scan.nextLong();
		for(long i = 0; i < count; i++) seededRNG.getRand();
		scan.next(); nextFighterID = scan.nextInt();
		scan.next(); currentGen = scan.nextInt();
		
		//load in fighter data
		scan.next();
		fighters = new Fighter[101 + AIFighter.NUM_AI];
		for(int i = 0; i < 100; i++){
			fighters[i] = new NeuralNetFighter(-1, -1, -1, -1, -1, seededRNG);
			((NeuralNetFighter)fighters[i]).load(scan);
		}
		
		//load in best fighter data
		bestFighters = new ArrayList<NeuralNetFighter>();
		scan.next();
		int size = scan.nextInt();
		for(int i = 0; i < size; i++){
			bestFighters.add(new NeuralNetFighter(-1, -1, -1, -1, -1, seededRNG));
			bestFighters.get(i).load(scan);
		}
		
		//load in fitness history data
		scan.next();
		int historySize = scan.nextInt();
		int valueLength = scan.nextInt();
		fitnessHistory = new ArrayList<Double[][]>();
		
		for(int i = 0; i < historySize; i++){
			fitnessHistory.add(new Double[2][valueLength]);
			for(int j = 0; j < valueLength; j++){
				fitnessHistory.get(i)[0][j] = scan.nextDouble();
			}
			for(int j = 0; j < valueLength; j++){
				fitnessHistory.get(i)[1][j] = scan.nextDouble();
			}
		}
		
		//update data in fitness history graph
		for(int i = 0; i < fitnessHistory.size(); i++){
			if(Config.DRAW_AVERAGE_LINES && !Config.DRAW_BEST_LINES) fitnessLineGraph.addValue(new Double[]{
				fitnessHistory.get(i)[0][0], fitnessHistory.get(i)[0][1], fitnessHistory.get(i)[0][2], fitnessHistory.get(i)[0][3],
				fitnessHistory.get(i)[0][4], fitnessHistory.get(i)[0][5], fitnessHistory.get(i)[0][6], fitnessHistory.get(i)[0][7],
				fitnessHistory.get(i)[0][8], fitnessHistory.get(i)[0][9], fitnessHistory.get(i)[0][10]
			});
			else if(!Config.DRAW_AVERAGE_LINES && Config.DRAW_BEST_LINES) fitnessLineGraph.addValue(new Double[]{
					fitnessHistory.get(i)[1][0], fitnessHistory.get(i)[1][1], fitnessHistory.get(i)[1][2], fitnessHistory.get(i)[1][3],
					fitnessHistory.get(i)[1][4], fitnessHistory.get(i)[1][5], fitnessHistory.get(i)[1][6], fitnessHistory.get(i)[1][7],
					fitnessHistory.get(i)[1][8], fitnessHistory.get(i)[1][9], fitnessHistory.get(i)[1][10]
			});
			else if(Config.DRAW_AVERAGE_LINES && Config.DRAW_BEST_LINES) fitnessLineGraph.addValue(new Double[]{
				fitnessHistory.get(i)[0][0], fitnessHistory.get(i)[0][1], fitnessHistory.get(i)[0][2], fitnessHistory.get(i)[0][3],
				fitnessHistory.get(i)[0][4], fitnessHistory.get(i)[0][5], fitnessHistory.get(i)[0][6], fitnessHistory.get(i)[0][7],
				fitnessHistory.get(i)[0][8], fitnessHistory.get(i)[0][9], fitnessHistory.get(i)[0][10],
				fitnessHistory.get(i)[1][0], fitnessHistory.get(i)[1][1], fitnessHistory.get(i)[1][2], fitnessHistory.get(i)[1][3],
				fitnessHistory.get(i)[1][4], fitnessHistory.get(i)[1][5], fitnessHistory.get(i)[1][6], fitnessHistory.get(i)[1][7],
				fitnessHistory.get(i)[1][8], fitnessHistory.get(i)[1][9], fitnessHistory.get(i)[1][10]
			});
		}
		
		//load in species history data
		scan.next();
		valueLength = scan.nextInt();
		speciesHistory = new ArrayList<Integer[]>();
		
		for(int i = 0; i < historySize; i++){
			speciesHistory.add(new Integer[valueLength]);
			for(int j = 0; j < valueLength; j++){
				speciesHistory.get(i)[j] = scan.nextInt();
			}
		}
		
		//load in node amount history
		scan.next();
		valueLength = scan.nextInt();
		nodeHistory = new ArrayList<Integer[]>();
		
		for(int i = 0; i < historySize; i++){
			nodeHistory.add(new Integer[valueLength]);
			for(int j = 0; j < valueLength; j++){
				nodeHistory.get(i)[j] = scan.nextInt();
			}
		}
		
		//load in node count history
		scan.next();
		valueLength = scan.nextInt();
		Double[][] tempData = new Double[historySize][valueLength];
		
		for(int i = 0; i < historySize; i++){
			for(int j = 0; j < valueLength; j++){
				tempData[i][j] = scan.nextDouble();
			}
		}
		
		for(int i = 0; i < valueLength; i++) nodeCountLineGraph.getLines()[i].clearData();
		
		for(int i = 0; i < historySize; i++){
			nodeCountLineGraph.addValue(tempData[i]);
		}
		
		//recalculate graphs and other info
		selectedGen = currentGen;
		for(int i = 0; i < 4; i++) toggleGraphsOn[i] = true;
		setMiscFighters();
		findOldestFighter();
		selectFightersToDie();
		updateGraphs();
		
		loopingGens = false;
		inInfoScreen = true;
		infoSlider.setCurrentValue(currentGen);
		infoSlider.setMaxValue(currentGen);
		bestFighterSlider.setMaxValue(currentGen);
		bestFighterSlider.setCurrentValue(currentGen);
	}
	
	/**
	 * Get a string that represents num with n decimal places
	 * @param num
	 * @return
	 */
	public static int round(double num){
		return (int)Math.round(num);
	}
}