package fighter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import menu.Config;

public abstract class Fighter{

	private int birthGeneration;
	private int fighterID;
	private int species;
	/**
	 * The id of the last fighter this fighter fought in its last generation test
	 */
	private int lastFoughtID;
	/**
	 * true if this fighter fought on the left side in the last generation test, false if on the right
	 */
	private boolean lastFoughtOnleft;
	/**
	 * The fitness of this fighter in the current fight it is in
	 */
	private double currentFitness;
	/**
	 * The scores of this fighter, goes up when an attack is landed or if an attack is blocked, goes down if this fighter is struck. 
	 * Ordered based on when the fitness scores were added, a new score is added each generation
	 */
	private ArrayList<Double> fitness;
	
	private double x;
	private double y;
	protected double dx;
	protected double dy;
	
	/**
	 * The amount of green in this fighter
	 */
	private int greenColor;
	
	/**
	 * The amount of ticks this fighter has been moving in the same direction for, max of 100
	 */
	private int moveTime;
	
	/**
	 * True if the jump button is held down, false otherwise
	 */
	protected boolean jumpDown;
	/**
	 * True if the left button is held down, false otherwise
	 */
	protected boolean leftDown;
	/**
	 * True if the right button is held down, false otherwise
	 */
	protected boolean rightDown;
	/**
	 * True if the attack button is held down, false otherwise
	 */
	protected boolean attackDown;
	/**
	 * True if the block button is held down, false otherwise
	 */
	protected boolean blockDown;
	
	/**
	 * True if this fighter can jump, false otherwise
	 */
	protected boolean canJump;
	
	/**
	 * The red component of the color of this fighter, also the amount of time until this fighter attacks or can attack
	 */
	private int attackTime;
	/**
	 * The blue component of the color of this fighter, also the amount of time until this fighter can block, or how much time until they can no longer block
	 */
	private int blockTime;
	
	/**
	 * The fighter that this fighter is facing off against
	 */
	protected Fighter partner;
	
	public Fighter(double x, double y, int birthGeneration, int fighterID, int species){
		this.x = x;
		this.y = y;
		fitness = new ArrayList<>();
		
		lastFoughtID = -99;
		lastFoughtOnleft = true;
		
		this.birthGeneration = birthGeneration;
		this.fighterID = fighterID;
		this.species = species;
		
		reset(true);
	}
	
	/**
	 * Reset this fighter for its next fight
	 * @param left true if this fighter should be allined to the left, false if it shoud be on the right
	 */
	public void reset(boolean left){
		if(left){
			setX(10);
			setY(Config.FIGHT_ZONE_HEIGHT - Config.FIGHT_ZONE_HEIGHT);
			setGreenColor(127);
		}
		else{
			setX(Config.FIGHT_ZONE_WIDTH - 10);
			setY(Config.FIGHT_ZONE_HEIGHT - Config.FIGHT_ZONE_HEIGHT);
			setGreenColor(0);
		}
		
		dx = 0;
		dy = 0;
		
		jumpDown = false;
		leftDown = false;
		rightDown = false;
		attackDown = false;
		blockDown = false;
		
		attackTime = Config.NEUTRAL_ATTACK_TIME;
		blockTime = Config.NEUTRAL_BLOCK_TIME;
		
		currentFitness = 0;
	}
	
	/**
	 * Set the partner of this fighter to f
	 * @param f
	 */
	public void setParnter(Fighter f){
		partner = f;
	}
	
	/**
	 *	Causes this fighter to jump upwards
	 */
	private void jump(){
		if(!canJump || isBlocking()) return;
		dy = Config.FIGHTER_JUMP_SPEED;
		canJump = false;
	}
	
	public boolean isBlocking(){
		return blockTime <= Config.BLOCKING_TIME;
	}

	public boolean isAttacking(){
		return attackTime <= Config.BEGIN_ATTACK_TIME;
	}
	
	public boolean canAttack(){
		return attackTime == Config.NEUTRAL_ATTACK_TIME;
	}
	
	/**
	 * Causes this this fighter to move in the direction fo dx, 
	 * if dx = 0, the fighter will stop moving, 
	 * if dx < 0 the fighter moves to the left
	 * otherwise the fighter moves to the right
	 * @param dx
	 */
	private void move(int dx){
		double oldSpeed = this.dx;
		if(dx == 0) this.dx = 0;
		else if(dx < 0) this.dx = -Config.FIGHTER_MOVE_SPEED;
		else this.dx = Config.FIGHTER_MOVE_SPEED;
		if(oldSpeed == 0 || oldSpeed != this.dx) moveTime = 0;
	}
	
	/**
	 * Attempt to attack the fighting partner
	 */
	private void attack(){
		attackTime = Config.NORMAL_ATTACK_TIME;
		if(partner != null){
			if(partner.getBounds().intersects(getBounds())){
				if(partner.isBlocking()){
					partner.addFitness(Config.BLOCKED, Config.FIGHTER_WIDTH);
					addFitness(Config.ATTACK_BLOCKED, Config.FIGHTER_WIDTH);
					partner.blockTime = Config.NORMAL_BLOCK_TIME;
					blockTime = Config.LONG_BLOCK_TIME;
					attackTime = Config.LONG_ATTACK_TIME;
				}
				else{
					partner.attackTime = Config.NORMAL_ATTACK_TIME;
					partner.addFitness(Config.ATTACKED, Config.FIGHTER_WIDTH);
					addFitness(Config.LANDED_HIT, Config.FIGHTER_WIDTH);
				}
			}
			else{
				if(Config.USE_TORUS) addFitness(Config.ATTACK_ENDED, Config.FIGHT_ZONE_WIDTH / 2);
				else addFitness(Config.ATTACK_ENDED, Config.FIGHT_ZONE_WIDTH / 2);
			}
		}
	}
	
	public void setX(int x){
		this.x = x;
	}
	public double getX(){
		return x;
	}
	public double getCenterX(){
		return x + Config.FIGHTER_WIDTH / 2;
	}
	
	public double getDx(){
		return dx;
	}
	
	public double getDy(){
		return dy;
	}
	
	public double getSpeed(){
		//get the acceleration of the fighter
		double addX = -Math.pow(0.92, moveTime) + 1;
		//ensure the fighter has not gone over the limit yet
		if(addX > 1) addX = 1;
		if(dx < 0) addX *= -1;
		else if(dx == 0) addX = 0;
		//if the fighter has jumped and is in the air, they move faster
		if(dy < 0) addX *= 1.5;
		//if the fighter is blocking they move slower
		if(isBlocking()) addX *= .4;
		return addX * Config.FIGHTER_MOVE_SPEED;
	}
	
	public double getY(){
		return y;
	}
	public void setY(int y){
		this.y = y;
	}
	
	public void setGreenColor(int green){
		greenColor = green;
	}
	
	public Rectangle2D.Double getBounds(){
		return new Rectangle2D.Double(x, y, Config.FIGHTER_WIDTH, Config.FIGHTER_HEIGHT);
	}
	
	/**
	 * Adds the specified fitness score to the list of fitness scores of the fighter
	 * @param fitness
	 */
	public void newGeneration(double fitness){
		this.fitness.add(fitness);
	}

	public void setBirthGeneration(int birthGen){
		birthGeneration = birthGen;
	}
	public int getBirthGeneration(){
		return birthGeneration;
	}
	
	public int getFighterID(){
		return fighterID;
	}
	public void setFighterID(int id){
		fighterID = id;
	}
	
	public void setSpecies(int species){
		this.species = species;
	}
	public int getSpecies(){
		return species;
	}
	
	public int getLastFoughtID(){
		return lastFoughtID;
	}
	public void setLastFoughtID(int id){
		lastFoughtID = id;
	}

	public boolean getLastFoughtOnLeft(){
		return lastFoughtOnleft;
	}
	public void setLastFoughtOnLeft(boolean lastFoughtOnleft){
		this.lastFoughtOnleft = lastFoughtOnleft;
	}
	
	/**
	 * Get the fitness of index i
	 * @param i
	 * @return
	 */
	public double getFitness(int i){
		return fitness.get(i);
	}
	
	/**
	 * Get the number of fitness scores stored in the list for calculating averages
	 * @return
	 */
	public int getFitnessSize(){
		return fitness.size();
	}
	
	/**
	 * Gets the last element in the list of fitnesses
	 * @return
	 */
	public double getLastRecordedFitness(){
		if(fitness.size() == 0) return 0;
		else return fitness.get(fitness.size() - 1);
	}
	
	public void setCurrentFitness(double f){
		currentFitness = f;
	}
	/**
	 * Get the fitness of this fighter in the fight it is currently in
	 * @return
	 */
	public double getCurrentFitness(){
		return currentFitness;
	}
	
	public double getAverageFitness(){
		double avg = 0;
		for(Double i : fitness) avg += i;
		return avg / fitness.size();
	}
	
	/**
	 * Add the specified amount of fitness to the current fitness of this fighter
	 * @param fitness the amount of fitness to add
	 * @param widthCompare the width to use to compare how close this fighter, the biggest distance there can be between the fighters
	 */
	public void addFitness(double fitness, double widthCompare){
		double f = fitness;
		double d;
		if(Config.USE_TORUS){
			double d1 = Math.abs(getX() - partner.getX());
			double d2;
			if(getX() < partner.getX()){
				d2 = getX() + Config.FIGHT_ZONE_WIDTH - partner.getX();
			}
			else{
				d2 = Config.FIGHT_ZONE_WIDTH - getX() + partner.getX();
			}
			d = Math.min(d1, d2);
		}
		else d = Math.abs(getX() - partner.getX());
		double percent = d / widthCompare;
		if(percent > 1) percent = 1;
		else if(percent < 0) percent = 0;
		if(f == Config.FIGHTER_WIDTH){
			f *= 1 - percent;
		}
		else{
			f *= percent;
		}
		this.currentFitness += f;
	}
	
	/**
	 * Add a new score to this fighters list of fitness scores
	 * @param fitness
	 */
	public void addFitnessScore(double fitness){
		this.fitness.add(fitness);
	}
	
	/**
	 * Removes all fitness scores from this fighter
	 */
	public void resetFitness(){
		fitness.clear();
	}
	
	public int getAttackTime(){
		return attackTime;
	}
	
	public int getBlockTime(){
		return blockTime;
	}
	
	/**
	 * Updates this fighter
	 */
	public void tick(){
		//if this fighter wants to jump, attempt to jump
		if(jumpDown) jump();
		//move in the direction this fighter wants to move in
		if(leftDown){
			if(!rightDown) move(-1);
			else move(0);
		}
		else if(rightDown){
			if(!leftDown) move(1);
			else move(0);
		}
		else move(0);
		
		//cool down block timer
		if(blockTime > Config.NEUTRAL_BLOCK_TIME || blockTime <= Config.BLOCKING_TIME) blockTime--;
		//if block is held down and this fighter is not attacking, then begin blocking
		if(blockDown && attackTime >= Config.NEUTRAL_ATTACK_TIME){
			if(blockTime == Config.NEUTRAL_BLOCK_TIME) blockTime = Config.BLOCKING_TIME;
		}
		//if this fighter stops blocking or blocks for too long, then stop blocking
		if(blockTime <= 0 || (!blockDown || attackDown) && blockTime <= Config.BLOCKING_TIME){
			if(blockTime <= 0){
				if(Config.USE_TORUS) addFitness(Config.BLOCK_ENDED, Config.FIGHT_ZONE_WIDTH / 2);
				else addFitness(Config.BLOCK_ENDED, Config.FIGHT_ZONE_WIDTH);
			}
			blockTime = Config.NORMAL_BLOCK_TIME;
		}
		
		//cool down the attack timer
		if(attackTime > Config.NEUTRAL_ATTACK_TIME || attackTime <= Config.BEGIN_ATTACK_TIME) attackTime--;
		//if this fighter is able to attack and wants to attack, it will attack
		if(attackDown){
			if(!isBlocking() && attackTime == Config.NEUTRAL_ATTACK_TIME) attackTime = Config.BEGIN_ATTACK_TIME;
		}
		//if this fighter's attack is about to happen, then attack and reset the cool down
		if(attackTime <= 0) attack();
		
		//determine how much x distance the fighter should move
		if(leftDown && !rightDown || rightDown && !leftDown) moveTime++;
		if(moveTime > 100) moveTime = 100;
		
		//update the position of the fighter
		//add the correct change in x
		x += getSpeed();
		//add the change in y
		y += dy;
		
		//add gravity to the fighter
		dy += Config.FIGHTER_GRAVITY;
		
		//code to make the bounds a torus
		if(Config.USE_TORUS){
			if(x < -Config.FIGHTER_WIDTH) x = Config.FIGHT_ZONE_WIDTH;
			if(x > Config.FIGHT_ZONE_WIDTH) x = -Config.FIGHTER_WIDTH;
		}
		else{
			if(x < 0) x = 0;
			if(x > Config.FIGHT_ZONE_WIDTH - Config.FIGHTER_WIDTH) x = Config.FIGHT_ZONE_WIDTH - Config.FIGHTER_WIDTH;
		}
		if(y < 0){
			y = 0;
			dy = 0;
		}
		if(y > Config.FIGHT_ZONE_HEIGHT - Config.FIGHTER_HEIGHT){
			y = Config.FIGHT_ZONE_HEIGHT - Config.FIGHTER_HEIGHT;
			dy = 0;
			canJump = true;
		}
	}
	
	/**
	 * Draws this fighter with g
	 * @param g
	 */
	public void render(Graphics g, int x, int y){
		Color c = getColor();
		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 200));
		g.fillRect((int)this.x + x, (int)this.y + y, Config.FIGHTER_WIDTH, Config.FIGHTER_HEIGHT);
		g.setFont(new Font("Courier New", Font.BOLD, 20));
		g.setColor(getColor());
		String s = "";
		if(isAttacking()) s += "A";
		else s += " ";
		if(greenColor >= 1) s += "1";
		else s += "2";
		if(isBlocking()) s += "B";
		else s += " ";
		g.drawString(s, (int)this.x + x, (int)this.y + y - 2);
	}
	
	/**
	 * Draw the info for this fighter at x and y. 
	 * Info includes: birth generation, fighter id, 
	 * average fitness, last fitness
	 * @param g
	 * @param x
	 * @param y
	 */
	public void renderInfo(Graphics g, int x, int y){
		g.setColor(Color.BLACK);
		g.setFont(new Font("Courier New", Font.BOLD, 15));
		g.drawString("ID: " + fighterID, x, y);
		g.drawString("Birth Gen: " + birthGeneration, x, y + 17);
		g.drawString("Type: " + getClass().getSimpleName(), x, y + 34);
		g.drawString("Species: " + species, x, y + 51);
		if(lastFoughtID != -99){
			g.drawString("Last Fight: " + lastFoughtID, x, y + 68);
			if(lastFoughtOnleft) g.drawString("Last Side: Left", x, y + 85);
			else g.drawString("Last Side: Right", x, y + 85);
		}
		if(fitness.size() > 0){
			g.drawString("Last Fitness: " + fitness.get(fitness.size() - 1), x, y + 102);
			g.drawString("Average Fitness: " + (float)getAverageFitness(), x, y + 119);
		}
	}
	
	/**
	 * Draws a visual display of how this fighter is being controled
	 * @param g
	 * @param x
	 * @param y
	 */
	public void renderDisplayInfo(Graphics g, int x, int y){}
	
	public Color getColor(){
		return new Color(attackTime, greenColor, blockTime);
	}
}
