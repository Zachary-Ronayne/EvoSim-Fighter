package menu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.PrintWriter;

public class MenuLoad{
	
	//buttons
	private MenuButton newSim;
	private MenuButton loadSim;
	private MenuButton newSeed;
	
	public MenuLoad(Main instance){

		newSim = new MenuButton(Main.SCREEN_WIDTH / 2 - 200, 150, 400, 75){
			@Override
			public void render(Graphics g){
				super.render(g);
				g.setColor(Color.BLACK);
				g.setFont(new Font("Courier New", Font.BOLD, 40));
				g.drawString("New Simulaiton", x + 10, y + 50);
			}
			
			@Override
			public void click(MouseEvent e){
				super.click(e);
				instance.createNewSim();
			}
		};
		loadSim = new MenuButton(Main.SCREEN_WIDTH / 2 - 200, 250, 400, 75){
			@Override
			public void render(Graphics g){
				super.render(g);
				g.setColor(Color.BLACK);
				g.setFont(new Font("Courier New", Font.BOLD, 40));
				g.drawString("Load Simulaiton", x + 10, y + 50);
			}
			
			@Override
			public void click(MouseEvent e){
				super.click(e);
				instance.setSimulation(true);
				instance.loadSim();
			}
		};
		newSeed = new MenuButton(Main.SCREEN_WIDTH / 2 - 150, 500, 300, 100){
			@Override
			public void render(Graphics g){
				super.render(g);
				g.setColor(Color.BLACK);
				g.setFont(new Font("Courier New", Font.BOLD, 30));
				g.drawString("Select", x + 92, y + 50);
				g.drawString("new seed", x + 75, y + 80);
			}
			
			@Override
			public void click(MouseEvent e){
				super.click(e);
				Config.setRandomSeed();
				try{
					//declare scanner
					PrintWriter write = new PrintWriter(new File("./NewSim.txt"));

					//load in the selected values from the NewSim text file
					Config.save(write);
					
					//close scanner
					write.close();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		};
	}
	
	public void linkToComponent(Component c){
		newSim.linkToComponent(c);
		loadSim.linkToComponent(c);
		newSeed.linkToComponent(c);
	}
	
	public void unlinkFromComponent(Component c){
		newSim.unlinkFromComponent(c);
		loadSim.unlinkFromComponent(c);
		newSeed.linkToComponent(c);
	}
	
	public void tick(){}
	
	public void render(Graphics g){
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);

		newSim.render(g);
		loadSim.render(g);
		newSeed.render(g);
	}
}
