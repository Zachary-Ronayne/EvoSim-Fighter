package fighter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import menu.Config;

public class AIFighter extends Fighter{

	/**
	 * Use the smart AI to fight. This AI actively tries to block attacks, chase down the opponent, and attack when the opponent isn't blocking
	 */
	public static final int SMART = 0;

	/**
	 * This AI will never press any buttons for any reason
	 */
	public static final int NONE = 1;
	
	/**
	 * This AI holds down attack and left and nothing else
	 */
	public static final int GO_LEFT = 2;
	
	/**
	 * This AI randomly moves from left to right and randomly jumps
	 */
	public static final int MOVE_RANDOM = 3;
	
	/**
	 * This AI holds down jump, right, and attack
	 */
	public static final int JUMP_RIGHT = 4;
	
	/**
	 * This AI will only attack when its opponent cannot block by the time the attack happens, only blocks when it is about to be hit, and follows the enemy as best it can
	 */
	public static final int EXPERT = 5;
	
	/**
	 * This AI actively tries to run away from the opponent and block attacks
	 */
	public static final int DEFENSE = 6;
	
	/**
	 * This AI pseudo randomly picks a direction to move in and attacks constantly and tries to block the opponent's attacks
	 */
	public static final int RANDOM_ATTACK = 7;
	
	public static final int NUM_AI = 8;
	
	private int timer;
	private int maxTimer;
	
	private int aItype;
	
	public AIFighter(double x, double y, int birthGeneration, int fighterID, int species, int AItype){
		super(x, y, birthGeneration, fighterID, species);
		this.aItype = AItype;
		reset(true);
	}
	
	@Override
	public void reset(boolean left){
		super.reset(left);
		timer = 0;
		maxTimer = 1;
	}
	
	public int getAItype(){
		return aItype;
	}
	
	@Override
	public void tick(){
		
		if(aItype == SMART){
			attackDown = true;
			
			blockDown = partner.isAttacking();
			if(blockDown) attackDown = false;
			
			if(Config.USE_TORUS){
				double d1 = Math.abs(getX() - partner.getX());
				double d2;
				boolean onLeft;
				
				if(getX() < partner.getX()){
					onLeft = true;
					d2 = getX() + Config.FIGHT_ZONE_WIDTH - partner.getX();
				}
				else{
					onLeft = false;
					d2 = Config.FIGHT_ZONE_WIDTH - getX() + partner.getX();
				}
				
				if(onLeft && d1 > d2 || !onLeft && d1 < d2){
					leftDown = true;
					rightDown = false;
				}
				else if(!onLeft && d1 > d2 || onLeft && d1 < d2){
					leftDown = false;
					rightDown = true;
				}
				else{
					leftDown = false;
					rightDown = false;
				}
			}
			else{
				if(getX() < partner.getX()){
					leftDown = false;
					rightDown = true;
				}
				else if(getX() > partner.getX()){
					leftDown = true;
					rightDown = false;
				}
				else{
					leftDown = false;
					rightDown = false;
				}
			}
			
			if(!isAttacking() && getBounds().intersects(partner.getBounds())){
				leftDown = !leftDown;
				rightDown = !rightDown;
				jumpDown = !jumpDown;
			}
			
			jumpDown = partner.getY() < getY();
		}
		else if(aItype == NONE){
			leftDown = false;
			rightDown = false;
			jumpDown = false;
			attackDown = false;
			blockDown = false;
		}
		else if(aItype == GO_LEFT){
			leftDown = true;
			rightDown = false;
			jumpDown = false;
			attackDown = true;
			blockDown = false;
		}
		else if(aItype == MOVE_RANDOM){
			timer++;
			if(timer > maxTimer){
				timer = 0;
				maxTimer = (int)(Math.random() * 50);
				if(Math.random() > .5){
					leftDown = true;
					rightDown = false;
				}
				else{
					leftDown = false;
					rightDown = true;
				}
				jumpDown = Math.random() < .1;
			}
			
			attackDown = false;
			blockDown = false;
		}
		else if(aItype == JUMP_RIGHT){
			leftDown = false;
			rightDown = true;
			jumpDown = true;
			attackDown = true;
			blockDown = false;
		}
		
		else if(aItype == EXPERT){
			boolean intersecting = getBounds().intersects(partner.getBounds());
			
			//block if they are about to be hit
			blockDown = partner.getAttackTime() <= 1;

			//attack if the opponent cannot block 
			attackDown = partner.getBlockTime() < Config.BEGIN_ATTACK_TIME && intersecting && !blockDown;
			
			//follow the opponent
			if(Config.USE_TORUS){
				double d1 = Math.abs(getX() - partner.getX());
				double d2;
				boolean onLeft;
				
				if(getX() < partner.getX()){
					onLeft = true;
					d2 = getX() + Config.FIGHT_ZONE_WIDTH - partner.getX();
				}
				else{
					onLeft = false;
					d2 = Config.FIGHT_ZONE_WIDTH - getX() + partner.getX();
				}
				
				if(onLeft && d1 > d2 || !onLeft && d1 < d2){
					leftDown = true;
					rightDown = false;
				}
				else if(!onLeft && d1 > d2 || onLeft && d1 < d2){
					leftDown = false;
					rightDown = true;
				}
				else{
					leftDown = false;
					rightDown = false;
				}
			}
			else{
				if(getX() < partner.getX()){
					leftDown = false;
					rightDown = true;
				}
				else if(getX() > partner.getX()){
					leftDown = true;
					rightDown = false;
				}
				else{
					leftDown = false;
					rightDown = false;
				}
			}
			
			jumpDown = partner.getY() < getY();
		}
		else if(aItype == DEFENSE){
			//run from the opponent
			if(Config.USE_TORUS){
				double d1 = Math.abs(getX() - partner.getX());
				double d2;
				boolean onLeft;
				
				if(getX() < partner.getX()){
					onLeft = true;
					d2 = getX() + Config.FIGHT_ZONE_WIDTH - partner.getX();
				}
				else{
					onLeft = false;
					d2 = Config.FIGHT_ZONE_WIDTH - getX() + partner.getX();
				}
				
				if(onLeft && d1 > d2 || !onLeft && d1 < d2){
					leftDown = false;
					rightDown = true;
				}
				else{
					leftDown = true;
					rightDown = false;
				}
			}
			else{
				if(!getBounds().intersects(partner.getBounds())){
					if(getX() < partner.getX()){
						leftDown = true;
						rightDown = false;
					}
					else if(getX() > partner.getX()){
						leftDown = false;
						rightDown = true;
					}
				}
				else{
					if(getX() < Config.FIGHT_ZONE_WIDTH / 2){
						leftDown = false;
						rightDown = true;
					}
					else{
						leftDown = true;
						rightDown = false;
					}
				}
			}
			jumpDown = true;
			blockDown = partner.canAttack() || partner.isAttacking();
			attackDown = false;
		}
		else if(aItype == RANDOM_ATTACK){
			
			attackDown = true;
			blockDown = partner.getAttackTime() <= 20;
			jumpDown = false;
			
			double num = getX() * 5 + partner.getX() * 53 + getY() * 958 + partner.getY() * 35 + getSpeed() + partner.getSpeed() * 45 + getDy() * 45 + partner.getDy() * 36 +
						 getAttackTime() * 31 + partner.getAttackTime() * 540 + getBlockTime() * 342 + partner.getBlockTime() * 806;
			
			timer++;
			if(timer > maxTimer){
				timer = 0;
				int result = (int)(Math.abs(num + 17)) % 30;
				maxTimer = (int)(30 + num % 90);
				if(result < 10){
					leftDown = true;
					rightDown = false;
				}
				else if(result < 20){
					leftDown = false;
					rightDown = true;
				}
				else{
					leftDown = false;
					rightDown = false;
				}
			}
		}
		super.tick();
	}
	
	@Override
	public void renderDisplayInfo(Graphics g, int x, int y){
		g.setColor(Color.BLACK);
		g.setFont(new Font("Courier New", Font.BOLD, 30));
		if(aItype == SMART){
			g.drawString("Smart AI", x + 10, y + 40);
			g.setFont(new Font("Courier New", Font.BOLD, 16));
			g.drawString("This AI actively tries to block attacks,", x + 10, y + 65);
			g.drawString("chase down its opponent, and attack", x + 10, y + 90);
			g.drawString("when the opponent isn't blocking.", x + 10, y + 115);
		}
		else if(aItype == NONE){
			g.drawString("No AI", x + 10, y + 40);
			g.setFont(new Font("Courier New", Font.BOLD, 16));
			g.drawString("This AI causes the fighter to stand", x + 10, y + 65);
			g.drawString("still and not attack or block.", x + 10, y + 90);
		}
		else if(aItype == GO_LEFT){
			g.drawString("Go Left AI", x + 10, y + 40);
			g.setFont(new Font("Courier New", Font.BOLD, 16));
			g.drawString("This AI presses the attack and move", x + 10, y + 65);
			g.drawString("left buttons, no matter what.", x + 10, y + 90);
		}
		else if(aItype == MOVE_RANDOM){
			g.drawString("Random AI", x + 10, y + 40);
			g.setFont(new Font("Courier New", Font.BOLD, 16));
			g.drawString("This AI randomly moves left and right", x + 10, y + 65);
			g.drawString("and randomly jumps. These random events", x + 10, y + 90);
			g.drawString("are independent of the simulation seed,", x + 10, y + 115);
			g.drawString("which means this AI will not act the", x + 10, y + 140);
			g.drawString("same on the same seed.", x + 10, y + 165);
			g.drawString("If you use this AI to test the fighters,", x + 10, y + 190);
			g.drawString("remember that you will be unable to", x + 10, y + 215);
			g.drawString("replicate the results from any given", x + 10, y + 240);
			g.drawString("simulation.", x + 10, y + 265);
		}
		else if(aItype == JUMP_RIGHT){
			g.drawString("Jump Right AI", x + 10, y + 40);
			g.setFont(new Font("Courier New", Font.BOLD, 16));
			g.drawString("This AI holds down the jump, attack", x + 10, y + 65);
			g.drawString("and move right buttons no matter what.", x + 10, y + 90);
		}
		else if(aItype == EXPERT){
			g.drawString("Expert AI", x + 10, y + 40);
			g.setFont(new Font("Courier New", Font.BOLD, 16));
			g.drawString("This AI will only attack when its opponent", x + 10, y + 65);
			g.drawString("cannot block by the time it's attack happens,", x + 10, y + 90);
			g.drawString("will only block when it is about to be hit,", x + 10, y + 115);
			g.drawString("and follows the enemy as best it can.", x + 10, y + 140);
		}
		else if(aItype == DEFENSE){
			g.drawString("Defense AI", x + 10, y + 40);
			g.setFont(new Font("Courier New", Font.BOLD, 16));
			g.drawString("This AI actively tries to run away from", x + 10, y + 65);
			g.drawString("the opponent and block attacks.", x + 10, y + 90);
		}
		else if(aItype == RANDOM_ATTACK){
			g.drawString("Pseudo Random AI", x + 10, y + 40);
			g.setFont(new Font("Courier New", Font.BOLD, 16));
			g.drawString("This AI pseudo randomly picks a direction", x + 10, y + 65);
			g.drawString("to move in, holds down attack, and tries", x + 10, y + 90);
			g.drawString("to block the opponent's attacks.", x + 10, y + 115);
		}
	}
	
}
