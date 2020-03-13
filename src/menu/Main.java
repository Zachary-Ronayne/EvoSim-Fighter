package menu;

import java.awt.Graphics;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JFrame;

import sim.Simulation;

/**
 * The main instance of the simulation
 */
public class Main implements Runnable{
	
	public static final int SCREEN_WIDTH = 1800;
	public static final int SCREEN_HEIGHT = 1050;
	
	private static Main instance;
	
	/**
	 * True if the program is currently running, false otherwise
	 */
	private boolean running;
	/**
	 * The screen for the main menu of the simulation
	 */
	private Screen mainScreen;
	/**
	 * The main frame for this simmulation
	 */
	private JFrame frame;
	/**
	 * The main thread that runs the simulation
	 */
	private Thread mainThread;
	
	public enum State{
		/**
		 * The load menu of the simulation, choose to either create a new simulation or load the previous simulation
		 */
		MENU,
		/**
		 * Info screen about the current simulation, shows graphs and has options to move onto the next generation of the simulation
		 */
		INFO,
		/**
		 * Displays a fight in real time
		 */
		FIGHT;
	}
	
	private State simState;
	
	//menus
	private MenuLoad loadMenu;
	private Simulation simulation;
	
	/**
	 * Constructor for main class
	 */
	public Main(){}
	
	public static void main(String[] args){
		//load in the settings from NewSim, if it can't then load the defaults
		try{
			//crate the PrintWriter Object
			Scanner scan = new Scanner(new File("./NewSim.txt"));
			
			//Save the Config data
			Config.load(scan);
			
			//Close the writer
			scan.close();
			
		}catch(Exception e){
			e.printStackTrace();
			Config.setDefault();
			try{
				PrintWriter writer = new PrintWriter(new File("./NewSim.txt"));
				
				Config.saveDefault(writer);
				
				writer.close();
			}catch(Exception e2){
				e.printStackTrace();
			}
		}
		
		instance = new Main();
		instance.mainThread = new Thread(instance);
		instance.mainThread.start();
	}
	
	/**
	 * Set the simulation to a new simulation, based on if it will be loaded in or not
	 * @param loading
	 */
	public void setSimulation(boolean loading){
		simulation = new Simulation(frame, instance, !loading);
	}
	
	public void setSimState(State s){
		if(simState == State.MENU){
			loadMenu.unlinkFromComponent(frame);
		}
		else if(simState == State.INFO){
			simulation.unlinkFromSim(frame, true);
			simulation.unlinkFromSim(frame, false);
		}
		else if(simState == State.FIGHT) simulation.getFighterArena().unlinkFromComponent(frame);
		
		if(s == State.INFO){
			simulation.reset(frame);
		}
		else if(s == State.FIGHT) simulation.getFighterArena().linkToComponent(frame);
		
		simState = s;
	}
	
	/**
	 * Forget all data about the previous simulation, and create a new one
	 */
	public void createNewSim(){
		//make new simulation
		try{
			//declare scanner
			Scanner scanner = new Scanner(new File("./NewSim.txt"));

			//load in the selected values from the NewSim text file
			Config.load(scanner);
			
			//close scanner
			scanner.close();
		}catch(Exception e){
			e.printStackTrace();
			
			//write the default values to the settings file
			try{
				PrintWriter writer = new PrintWriter(new File("./NewSim.txt"));
				Config.saveDefault(writer);
				writer.close();
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}
		simulation = new Simulation(frame, instance, true);
		setSimState(State.INFO);
	}
	
	/**
	 * Saves the current simulation to the Save.txt file
	 */
	public void saveSimulation(){
		try{
			//crate the PrintWriter Object
			PrintWriter writer = new PrintWriter(new File("./Save.txt"));
			
			//Save the Config data
			Config.save(writer);
			
			//Save the simulation Data
			simulation.save(writer);
			
			//Close the writer
			writer.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Load all data about the previous simulation
	 */
	public void loadSim(){
		//make new simulation
		setSimState(State.INFO);
		try{
			//declare scanner
			Scanner scanner = new Scanner(new File("./Save.txt"));
			
			//load in config details
			Config.load(scanner);
			
			//load in the data about the simulation
			simulation.load(scanner);
			
			//close scanner
			scanner.close();
		}catch(Exception e){
			System.err.println("Failed To Load Simulaiton");
		}
	}
	
	public void startFight(){
		if(simulation != null && simulation.getFighterArena() != null) setSimState(State.FIGHT);
	}
	
	/**
	 * Initialize the main program to its default state
	 */
	private void init(){
		simState = State.MENU;
		
		running = true;

		loadMenu = new MenuLoad(instance);
		
		mainScreen = new Screen(SCREEN_WIDTH, SCREEN_HEIGHT){
			@Override
			public void renderOverride(Graphics g){
				super.renderOverride(g);
				switch(simState){
					case MENU:
						loadMenu.render(g);
						break;
					case INFO:
						simulation.render(g);
						break;
					case FIGHT:
						simulation.getFighterArena().render(g);
						break;
				}
			}
		};
		frame = new JFrame("Fighter Evolution Simulation"){
			private static final long serialVersionUID = 1L;
			@Override
			public void paint(Graphics g){
				mainScreen.render(g);
			}
		};
		frame.setVisible(false);
		frame.setUndecorated(false);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.pack();
		frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		frame.setLocationRelativeTo(null);
		
		loadMenu.linkToComponent(frame);
	}
	
	/**
	 * Update the main program
	 */
	public void tick(){
		switch(simState){
			case MENU:
				loadMenu.tick();
				break;
			case INFO:
				simulation.tick();
				break;
			case FIGHT:
				simulation.getFighterArena().tick();
				break;
		}
	}
	
	/**
	 * Render the main program on the screen. 
	 * To modify the rendering, go to the initialization of mainScreen, located in the init() method
	 */
	private void render(){
		frame.repaint();
	}
	
	/**
	 * Responsible for rendering and updating the main program
	 */
	@Override
	public void run(){
		init();
		long lastTime = System.nanoTime();
		final double numTicks = 100;
		final int nanoSecond = 1000000000;
		final double nanoTicks = nanoSecond / numTicks;
		double nanoTime = 0;
		int frames = 0;
		int ticks = 0;
		long timer = System.currentTimeMillis();
		
		long currentTime;
		while(running){
			//general update
			currentTime = System.nanoTime();
			nanoTime += (currentTime - lastTime) / nanoTicks;
			lastTime = currentTime;
			
			//tick and render statements
			if(nanoTime >= 1){
				tick();
				ticks++;
				nanoTime--;

				render();
				frames++;
			}
			
			if(ticks > 101){
				nanoTime = 0;
			}
			
			//console output
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				System.out.println(ticks + " Ticks\tFPS: " + frames);
				ticks = 0;
				frames = 0;
			}
		}
		stop();
	}
	

	public synchronized void stop(){
		if(!running) return;
		running = false;
		terminate();
	}
	
	public static void terminate(){
		System.out.println("Exit successful");
		System.exit(1);
		return;
	}
	
}
