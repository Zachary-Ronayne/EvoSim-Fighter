package menu;

import java.io.PrintWriter;
import java.util.Scanner;

import fighter.AIFighter;
import sim.Simulation;

public final class Config{
	
	//Fields used by NeuralNetFighter
	/**
	 * The minimum number of nodes in a fighter's brain
	 */
	public static int MIN_NODES;
	/**
	 * The maximum number of nodes in a fighter's brain
	 */
	public static int MAX_NODES;
	/**
	 * The minimum number of nodes in a fighter's brain when it is created
	 */
	public static int MIN_START_NODES;
	/**
	 * The maximum number of nodes in a fighter's brain when it is created
	 */
	public static int MAX_START_NODES;
	/**
	 * The rate at which mutability mutates
	 */
	public static double MUTABILITY_CHANGE;
	/**
	 * The rate at which node mutability mutates
	 */
	public static double NODE_MUTABILITY_CHANGE;
	/**
	 * The minimum value that regular mutability can take
	 */
	public static double MIN_MUTABILITY;
	/**
	 * The maximum value that regular mutability can take
	 */
	public static double MAX_MUTABILITY;
	/**
	 * The minimum value that node mutability can take
	 */
	public static double MIN_NODE_MUTABILITY;
	/**
	 * The maximum value that node mutability can take
	 */
	public static double MAX_NODE_MUTABILITY;
	//defaults
	public static final int DEFAULT_MIN_NODES = 1;
	public static final int DEFAULT_MAX_NODES = 100;
	public static final int DEFAULT_MIN_START_NODES = 1;
	public static final int DEFAULT_MAX_START_NODES = 30;
	public static final double DEFAULT_MUTABILITY_CHANGE = .5;
	public static final double DEFAULT_NODE_MUTABILITY_CHANGE = 5;
	public static final double DEFAULT_MIN_MUTABILITY = .08;
	public static final double DEFAULT_MAX_MUTABILITY = 2;
	public static final double DEFAULT_MIN_NODE_MUTABILITY = .05;
	public static final double DEFAULT_MAX_NODE_MUTABILITY = 4;
	
	//Fields used by Fighter
	/**
	 * The width of the fight arena
	 */
	public static int FIGHT_ZONE_WIDTH;
	/**
	 * The height of the fight arena
	 */
	public static int FIGHT_ZONE_HEIGHT;
	/**
	 * The width of a fighter
	 */
	public static int FIGHTER_WIDTH;
	/**
	 * The height of a fighter
	 */
	public static int FIGHTER_HEIGHT;
	/**
	 * The base speed of a fighter, should only be positive
	 */
	public static double FIGHTER_MOVE_SPEED;
	/**
	 * The initial velocity when a fighter jumps upwards, should always be negative
	 */
	public static double FIGHTER_JUMP_SPEED;
	/**
	 * The amount of gravity added to each figher each tick
	 */
	public static double FIGHTER_GRAVITY;
	/**
	 * The fitness added when: You hit your opponent while it was not blocking
	 */
	public static double LANDED_HIT;
	/**
	 * The fitness added when: You were hit while you were blocking
	 */
	public static double BLOCKED;
	/**
	 * The fitness added when: Your attack was blocked
	 */
	public static double ATTACK_BLOCKED;
	/**
	 * The fitness added when: You got hit by an attack when you were not blocking
	 */
	public static double ATTACKED;
	/**
	 * The fitness added when: Your block ended and you didn't block anything
	 */
	public static double BLOCK_ENDED;
	/**
	 * The fitness added when: Your attack ended and hit nothing
	 */
	public static double ATTACK_ENDED;
	/**
	 * The red color when this fighter has just attacked, and the attack is now on cool down
	 */
	public static int NORMAL_ATTACK_TIME;
	/**
	 * The red color when this fighter has just had its attack blocked, and is now on a longer than normal cool down
	 */
	public static int LONG_ATTACK_TIME;
	/**
	 * The red color when this fighter has just begun to attack
	 */
	public static int BEGIN_ATTACK_TIME;
	/**
	 * The red color when this fighter is able to attack but has not yet began to 
	 */
	public static int NEUTRAL_ATTACK_TIME;
	/**
	 * The blue color when this fighter has just stopped blocking, and blocking is now on cool down
	 */
	public static int NORMAL_BLOCK_TIME;
	/**
	 * The blue color when this fighter had an attack blocked, and blocking is now on a longer than normal cool down
	 */
	public static int LONG_BLOCK_TIME;
	/**
	 * The blue color when this fighter is blocking, and the time until the fighter is no longer able to block
	 */
	public static int BLOCKING_TIME;
	/**
	 * The blue color when this fighter is able to block, but has not yet blocked
	 */
	public static int NEUTRAL_BLOCK_TIME;
	//defaults
	public static final int DEFAULT_FIGHT_ZONE_WIDTH = 500;
	public static final int DEFAULT_FIGHT_ZONE_HEIGHT = 300;
	public static final int DEFAULT_FIGHTER_WIDTH = 50;
	public static final int DEFAULT_FIGHTER_HEIGHT = 70;
	public static final double DEFAULT_FIGHTER_MOVE_SPEED = 3;
	public static final double DEFAULT_FIGHTER_JUMP_SPEED = -6;
	public static final double DEFAULT_FIGHTER_GRAVITY = .1;
	public static final double DEFAULT_LANDED_HIT = 9;
	public static final double DEFAULT_BLOCKED = 6;
	public static final double DEFAULT_ATTACK_BLOCKED = -2;
	public static final double DEFAULT_ATTACKED = -5;
	public static final double DEFAULT_BLOCK_ENDED = -1;
	public static final double DEFAULT_ATTACK_ENDED = -1;
	public static final int DEFAULT_NORMAL_ATTACK_TIME = 100;
	public static final int DEFAULT_LONG_ATTACK_TIME = 200;
	public static final int DEFAULT_BEGIN_ATTACK_TIME = 50;
	public static final int DEFAULT_NEUTRAL_ATTACK_TIME = DEFAULT_BEGIN_ATTACK_TIME + 1;
	public static final int DEFAULT_NORMAL_BLOCK_TIME = 200;
	public static final int DEFAULT_LONG_BLOCK_TIME = 255;
	public static final int DEFAULT_BLOCKING_TIME = 175;
	public static final int DEFAULT_NEUTRAL_BLOCK_TIME = DEFAULT_BLOCKING_TIME + 1;
	
	//Fields used by FighterArena
	/**
	 * The amount of ticks a fight will go on before it is over, number of seconds = MAX_FIGHT_TIME / 100
	 */
	public static int MAX_FIGHT_TIME;
	/**
	 * True if the fighters should go off of the screen and appear on the other side, false if they should be bounded by the sides
	 */
	public static boolean USE_TORUS;
	//defaults
	public static final int DEFAULT_MAX_FIGHT_TIME = 2000;
	public static final boolean DEFAULT_USE_TORUS = true;
	
	//Fields used by Simulation
	/**
	 * The way to test the fighters in each gen
	 */
	public static int TEST_TYPE;
	/**
	 * The AI that you want to use for the hard coded AI
	 */
	public static int AI_TYPE;
	/**
	 * True if it should show the stats on the line graph of each generations betst, worst, and median fitness
	 */
	public static boolean DRAW_BEST_LINES;
	/**
	 * True if it should show the stats on the line graph of each generations average best, worst, and median fitness
	 */
	public static boolean DRAW_AVERAGE_LINES;
	/**
	 * True if the species area graph should use polygons, false if it should use rectangles
	 */
	public static boolean POLY_GRAPH;
	/**
	 * True if black lines should be drawn on the species area graph separating each segment
	 */
	public static boolean DRAW_SEPERATION_LINES;
	/**
	 * True if, while looping, the graphs should update, false if they should not until looping stops
	 */
	public static boolean ALWAYS_UPDATE_GRAPHS;
	/**
	 * True if the fighters should use their average fitness to sort and see if they should go onto the next generation. 
	 * False if the fitness from that generation test should be used
	 */
	public static boolean TEST_ON_AVERAGE;
	/**
	 * True if a surviving fighter should give birth to 2 fighters and then die, false if they should live and give birth to one offspring
	 */
	public static boolean KILL_PARENT;
	//defaults
	public static final int DEFAULT_TEST_TYPE = Simulation.TEST_ONE;
	public static final int DEFAULT_AI_TYPE = AIFighter.SMART;
	public static final boolean DEFAULT_DRAW_BEST_LINES = false;
	public static final boolean DEFAULT_DRAW_AVERAGE_LINES = true;
	public static final boolean DEFAULT_POLY_GRAPH = true;
	public static final boolean DEFAULT_DRAW_SEPERATION_LINES = false;
	public static final boolean DEFAULT_ALWAYS_UPDATE_GRAPHS = false;
	public static final boolean DEFAULT_TEST_ON_AVERAGE = true;
	public static final boolean DEFAULT_KILL_PARENT = false;
	
	/**
	 * the seed for the current simulation
	 */
	public static int SEED;
	
	/**
	 * Load in the values from the given scanner, the next values input from the scanner must be the appropriate values for this function
	 * @param scan
	 */
	public static void load(Scanner scan){
		scan.nextLine();
		scan.next(); MIN_NODES = scan.nextInt();
		scan.next(); MAX_NODES = scan.nextInt();
		scan.next(); MIN_START_NODES = scan.nextInt();
		scan.next(); MAX_START_NODES = scan.nextInt();
		scan.next(); MUTABILITY_CHANGE = scan.nextDouble();
		scan.next(); NODE_MUTABILITY_CHANGE = scan.nextDouble();
		scan.next(); MIN_MUTABILITY = scan.nextDouble();
		scan.next(); MAX_MUTABILITY = scan.nextDouble();
		scan.next(); MIN_NODE_MUTABILITY = scan.nextDouble();
		scan.next(); MAX_NODE_MUTABILITY = scan.nextDouble();
		scan.nextLine();
		scan.next(); FIGHT_ZONE_WIDTH = scan.nextInt();
		scan.next(); FIGHT_ZONE_HEIGHT = scan.nextInt();
		scan.next(); FIGHTER_WIDTH = scan.nextInt();
		scan.next(); FIGHTER_HEIGHT = scan.nextInt();
		scan.next(); FIGHTER_MOVE_SPEED = scan.nextDouble();
		scan.next(); FIGHTER_JUMP_SPEED = scan.nextDouble();
		scan.next(); FIGHTER_GRAVITY = scan.nextDouble();
		scan.nextLine();
		scan.next(); MAX_FIGHT_TIME = scan.nextInt();
		scan.next(); USE_TORUS = scan.nextInt() == 1;
		scan.nextLine();
		scan.next(); TEST_TYPE = scan.nextInt();
		scan.next(); AI_TYPE = scan.nextInt();
		scan.next(); DRAW_BEST_LINES = scan.nextInt() == 1;
		scan.next(); DRAW_AVERAGE_LINES = scan.nextInt() == 1;
		scan.next(); POLY_GRAPH = scan.nextInt() == 1;
		scan.next(); DRAW_SEPERATION_LINES = scan.nextInt() == 1;
		scan.next(); ALWAYS_UPDATE_GRAPHS = scan.nextInt() == 1;
		scan.next(); TEST_ON_AVERAGE = scan.nextInt() == 1;
		scan.next(); KILL_PARENT = scan.nextInt() == 1;
		scan.nextLine();
		scan.next(); LANDED_HIT = scan.nextDouble();
		scan.next(); BLOCKED = scan.nextDouble();
		scan.next(); ATTACK_BLOCKED = scan.nextDouble();
		scan.next(); ATTACKED = scan.nextDouble();
		scan.next(); BLOCK_ENDED = scan.nextDouble();
		scan.next(); ATTACK_ENDED = scan.nextDouble();
		scan.next(); NORMAL_ATTACK_TIME = scan.nextInt();
		scan.next(); LONG_ATTACK_TIME = scan.nextInt();
		scan.next(); BEGIN_ATTACK_TIME = scan.nextInt();
		scan.next(); NEUTRAL_ATTACK_TIME = scan.nextInt();
		scan.next(); NORMAL_BLOCK_TIME = scan.nextInt();
		scan.next(); LONG_BLOCK_TIME = scan.nextInt();
		scan.next(); BLOCKING_TIME = scan.nextInt();
		scan.next(); NEUTRAL_BLOCK_TIME = scan.nextInt();
		scan.nextLine();
		scan.next();
		String s = scan.next();
		try{
			SEED = Integer.parseInt(s);
		}catch(Exception e){
			SEED = 0;
			for(int i = 0; i < s.length(); i++) SEED += s.charAt(i);
		}
	}
	
	/**
	 * Save the current values to the given PrintWriter, the print writer must be ready to have all the values for this class printed
	 * @param write
	 */
	public static void save(PrintWriter write){
		write.println("Simulation Settings\n");
		write.println("Min_Nodes: " + MIN_NODES);
		write.println("Max_Nodes: " + MAX_NODES);
		write.println("Min_Start_Nodes: " + MIN_START_NODES);
		write.println("Max_Start_Nodes: " + MAX_START_NODES);
		write.println("Mutability_Change: " + MUTABILITY_CHANGE);
		write.println("Node_Mutability_Change: " + NODE_MUTABILITY_CHANGE);
		write.println("Min_Mutability: " + MIN_MUTABILITY);
		write.println("Max_Mutability: " + MAX_MUTABILITY);
		write.println("Min_Node_Mutability: " + MIN_NODE_MUTABILITY);
		write.println("Max_Node_Mutability: " + MAX_NODE_MUTABILITY);
		write.println("");
		write.println("Fight_Arena_Width: " + FIGHT_ZONE_WIDTH);
		write.println("Fight_Arena_Height: " + FIGHT_ZONE_HEIGHT);
		write.println("Fighter_Width: " + FIGHTER_WIDTH );
		write.println("Fighter_Height: " + FIGHTER_HEIGHT);
		write.println("Fighter_Move_Speed: " + FIGHTER_MOVE_SPEED);
		write.println("Fighter_Jump_Speed: " + FIGHTER_JUMP_SPEED);
		write.println("Fighter_Gravity: " + FIGHTER_GRAVITY);
		write.println("");
		write.println("Max_Fight_Time: " + MAX_FIGHT_TIME);
		write.println("Use_Torus: " + boolToNum(USE_TORUS));
		write.println("");
		write.println("Test_Type: " + TEST_TYPE);
		write.println("AI_Type: " + AI_TYPE);
		write.println("Draw_Best_Lines: " + boolToNum(DRAW_BEST_LINES));
		write.println("Draw_Average_Lines: " + boolToNum(DRAW_AVERAGE_LINES));
		write.println("Poly_Graph: " + boolToNum(POLY_GRAPH));
		write.println("Draw_Seperation_Lines: " + boolToNum(DRAW_SEPERATION_LINES));
		write.println("Always_Update_Graphs: " + boolToNum(ALWAYS_UPDATE_GRAPHS));
		write.println("Test_On_Average: " + boolToNum(TEST_ON_AVERAGE));
		write.println("Kill_Parent: " + boolToNum(KILL_PARENT));
		write.println("");
		write.println("Hit_Landed_Fitness: " + LANDED_HIT);
		write.println("Hit_Blocked_Fitness: " + BLOCKED);
		write.println("Attack_Blocked_Fitness: " + ATTACK_BLOCKED);
		write.println("Attacked_Fitness: " + ATTACKED);
		write.println("Block_Ended_Fitness: " + BLOCK_ENDED);
		write.println("Attack_Ended_Fitness: " + ATTACK_ENDED);
		write.println("Normal_Attack_Time: " + NORMAL_ATTACK_TIME);
		write.println("Long_Attack_Time: " + LONG_ATTACK_TIME);
		write.println("Begin_Attack_Time: " + BEGIN_ATTACK_TIME);
		write.println("Neutral_Attack_Time: " + NEUTRAL_ATTACK_TIME);
		write.println("Normal_Block_Time: " + NORMAL_BLOCK_TIME);
		write.println("Long_Block_Time: " + LONG_BLOCK_TIME);
		write.println("Blocking_Time: " + BLOCKING_TIME);
		write.println("Neutral_Block_Time: " + NEUTRAL_BLOCK_TIME);
		write.println("");
		write.println("Seed: " + SEED);
		write.println("");
	}
	
	/**
	 * Set all the configurable details to their default values, does not save any values
	 */
	public static void setDefault(){
		
		MIN_NODES = DEFAULT_MIN_NODES;
		MAX_NODES = DEFAULT_MAX_NODES;
		MIN_START_NODES = DEFAULT_MIN_START_NODES;
		MAX_START_NODES = DEFAULT_MAX_START_NODES;
		MUTABILITY_CHANGE = DEFAULT_MUTABILITY_CHANGE;
		NODE_MUTABILITY_CHANGE = DEFAULT_NODE_MUTABILITY_CHANGE;
		MIN_MUTABILITY = DEFAULT_MIN_MUTABILITY;
		MAX_MUTABILITY = DEFAULT_MAX_MUTABILITY;
		MIN_NODE_MUTABILITY = DEFAULT_MIN_NODE_MUTABILITY;
		MAX_NODE_MUTABILITY = DEFAULT_MAX_NODE_MUTABILITY;
		
		FIGHT_ZONE_WIDTH = DEFAULT_FIGHT_ZONE_WIDTH;
		FIGHT_ZONE_HEIGHT = DEFAULT_FIGHT_ZONE_HEIGHT;
		FIGHTER_WIDTH = DEFAULT_FIGHTER_WIDTH;
		FIGHTER_HEIGHT = DEFAULT_FIGHTER_HEIGHT;
		FIGHTER_MOVE_SPEED = DEFAULT_FIGHTER_MOVE_SPEED;
		FIGHTER_JUMP_SPEED = DEFAULT_FIGHTER_JUMP_SPEED;
		FIGHTER_GRAVITY = DEFAULT_FIGHTER_GRAVITY;
		KILL_PARENT = DEFAULT_KILL_PARENT;
		
		MAX_FIGHT_TIME = DEFAULT_MAX_FIGHT_TIME;
		USE_TORUS = DEFAULT_USE_TORUS;
		
		TEST_TYPE = DEFAULT_TEST_TYPE;
		AI_TYPE = DEFAULT_AI_TYPE;
		DRAW_BEST_LINES = DEFAULT_DRAW_BEST_LINES;
		DRAW_AVERAGE_LINES = DEFAULT_DRAW_AVERAGE_LINES;
		POLY_GRAPH = DEFAULT_POLY_GRAPH;
		DRAW_SEPERATION_LINES = DEFAULT_DRAW_SEPERATION_LINES;
		ALWAYS_UPDATE_GRAPHS = DEFAULT_ALWAYS_UPDATE_GRAPHS;
		TEST_ON_AVERAGE = DEFAULT_TEST_ON_AVERAGE;
		
		LANDED_HIT = DEFAULT_LANDED_HIT;
		BLOCKED = DEFAULT_BLOCKED;
		ATTACK_BLOCKED = DEFAULT_ATTACK_BLOCKED;
		ATTACKED = DEFAULT_ATTACKED;
		BLOCK_ENDED = DEFAULT_BLOCK_ENDED;
		ATTACK_ENDED = DEFAULT_ATTACK_ENDED;
		NORMAL_ATTACK_TIME = DEFAULT_NORMAL_ATTACK_TIME;
		LONG_ATTACK_TIME = DEFAULT_LONG_ATTACK_TIME;
		BEGIN_ATTACK_TIME = DEFAULT_BEGIN_ATTACK_TIME;
		NEUTRAL_ATTACK_TIME = DEFAULT_NEUTRAL_ATTACK_TIME;
		NORMAL_BLOCK_TIME = DEFAULT_NORMAL_BLOCK_TIME;
		LONG_BLOCK_TIME = DEFAULT_LONG_BLOCK_TIME;
		BLOCKING_TIME = DEFAULT_BLOCKING_TIME;
		NEUTRAL_BLOCK_TIME = DEFAULT_NEUTRAL_BLOCK_TIME;
		setRandomSeed();
	}
	
	/**
	 * Write the default values to the given PrintWriter
	 * @param write
	 */
	public static void saveDefault(PrintWriter write){
		write.println("Simulation Settings\n");
		write.println("Min_Nodes: " + DEFAULT_MIN_NODES);
		write.println("Max_Nodes: " + DEFAULT_MAX_NODES);
		write.println("Min_Start_Nodes: " + DEFAULT_MIN_START_NODES);
		write.println("Max_Start_Nodes: " + DEFAULT_MAX_START_NODES);
		write.println("Mutability_Change: " + DEFAULT_MUTABILITY_CHANGE);
		write.println("Node_Mutability_Change: " + DEFAULT_NODE_MUTABILITY_CHANGE);
		write.println("Min_Mutability: " + DEFAULT_MIN_MUTABILITY);
		write.println("Max_Mutability: " + DEFAULT_MAX_MUTABILITY);
		write.println("Min_Node_Mutability: " + DEFAULT_MIN_NODE_MUTABILITY);
		write.println("Max_Node_Mutability: " + DEFAULT_MAX_NODE_MUTABILITY);
		write.println("");
		write.println("Fight_Arena_Width: " + DEFAULT_FIGHT_ZONE_WIDTH);
		write.println("Fight_Arena_Height: " + DEFAULT_FIGHT_ZONE_HEIGHT);
		write.println("Fighter_Width: " + DEFAULT_FIGHTER_WIDTH );
		write.println("Fighter_Height: " + DEFAULT_FIGHTER_HEIGHT);
		write.println("Fighter_Move_Speed: " + DEFAULT_FIGHTER_MOVE_SPEED);
		write.println("Fighter_Jump_Speed: " + DEFAULT_FIGHTER_JUMP_SPEED);
		write.println("Fighter_Gravity: " + DEFAULT_FIGHTER_GRAVITY);
		write.println("");
		write.println("Max_Fight_Time: " + DEFAULT_MAX_FIGHT_TIME);
		write.println("Use_Torus: " + boolToNum(DEFAULT_USE_TORUS));
		write.println("");
		write.println("Test_Type: " + DEFAULT_TEST_TYPE);
		write.println("AI_Type: " + DEFAULT_AI_TYPE);
		write.println("Draw_Best_Lines: " + boolToNum(DEFAULT_DRAW_BEST_LINES));
		write.println("Draw_Average_Lines: " + boolToNum(DEFAULT_DRAW_AVERAGE_LINES));
		write.println("Poly_Graph: " + boolToNum(DEFAULT_POLY_GRAPH));
		write.println("Draw_Seperation_Lines: " + boolToNum(DEFAULT_DRAW_SEPERATION_LINES));
		write.println("Always_Update_Graphs: " + boolToNum(DEFAULT_ALWAYS_UPDATE_GRAPHS));
		write.println("Test_On_Average: " + boolToNum(DEFAULT_TEST_ON_AVERAGE));
		write.println("Kill_Parent: " + boolToNum(DEFAULT_KILL_PARENT));
		write.println("");
		write.println("Hit_Landed_Fitness: " + DEFAULT_LANDED_HIT);
		write.println("Hit_Blocked_Fitness: " + DEFAULT_BLOCKED);
		write.println("Attack_Blocked_Fitness: " + DEFAULT_ATTACK_BLOCKED);
		write.println("Attacked_Fitness: " + DEFAULT_ATTACKED);
		write.println("Block_Ended_Fitness: " + DEFAULT_BLOCK_ENDED);
		write.println("Attack_Ended_Fitness: " + DEFAULT_ATTACK_ENDED);
		write.println("Normal_Attack_Time: " + DEFAULT_NORMAL_ATTACK_TIME);
		write.println("Long_Attack_Time: " + DEFAULT_LONG_ATTACK_TIME);
		write.println("Begin_Attack_Time: " + DEFAULT_BEGIN_ATTACK_TIME);
		write.println("Neutral_Attack_Time: " + DEFAULT_NEUTRAL_ATTACK_TIME);
		write.println("Normal_Block_Time: " + DEFAULT_NORMAL_BLOCK_TIME);
		write.println("Long_Block_Time: " + DEFAULT_LONG_BLOCK_TIME);
		write.println("Blocking_Time: " + DEFAULT_BLOCKING_TIME);
		write.println("Neutral_Block_Time: " + DEFAULT_NEUTRAL_BLOCK_TIME);
		write.println("");
		write.println("Seed: " + SEED);
		write.println("");
	}
	
	/**
	 * COnverts the given boolean to a 1 or 0
	 * @param b
	 * @return 1 if b is true, 0 otherwise
	 */
	public static int boolToNum(boolean b){
		if(b) return 1;
		else return 0;
	}
	
	/**
	 * Select a new completly random seed
	 */
	public static void setRandomSeed(){
		SEED = (int)((Math.random() - .5) * Integer.MAX_VALUE * 2);
	}
	
}
