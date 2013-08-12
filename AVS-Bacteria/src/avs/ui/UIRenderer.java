/**
 * 
 */
package avs.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * @author HZU
 *
 */
public class UIRenderer implements Runnable {
	UserInterface userInterface;
	static final long timeDelta = 1000;
	long lastTime = 0;
	long currentTime = System.currentTimeMillis();
	long timeAccumulator = 0;
	boolean running = true;
	long timeRunning = 0;
	long sleepTime = 0;
	final Font font = new Font("Arial", Font.BOLD, 20);
	Color colorRed = new Color(255,0,0);
	Color colorBlack = new Color(0,0,0);
	long fps = 0;
	long fpscounter = 0;
	long lastFPSTime = 0;
	
	int screenMenuYOffset = 20;
	
	public UIRenderer(UserInterface userInterface) {
		this.userInterface = userInterface;
	}

	@Override
	public void run() {
		lastTime = System.currentTimeMillis();
		lastFPSTime = lastTime;
		
		while (running) {
			//Akkumulator befüllen
			currentTime = System.currentTimeMillis();
			if (currentTime > lastTime) {
				timeAccumulator = (currentTime-lastTime);
				lastTime = currentTime;
				
				}
			
			while (timeAccumulator > timeDelta) {
				timeAccumulator -= timeDelta;
				timeRunning += timeDelta;
				calculate();
			}
			
			
			userInterface.repaint();
			
			if (lastFPSTime < currentTime-1000) {
				fps = fpscounter;
				fpscounter=0;
				lastFPSTime = currentTime;
			}
			
			
			sleepTime = lastTime-currentTime+timeDelta;
			if (sleepTime > 0) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
		
		
	}

	private void calculate() {
		// TODO Auto-generated method stub
		
	}

	public void draw(Graphics g) {
		fpscounter++;
		g.clearRect(0, 0, (int)userInterface.getSize().getWidth(), (int)userInterface.getSize().getHeight());
		
		double sizeX = userInterface.getSize().getWidth();
		double sizeY = userInterface.getSize().getHeight() -screenMenuYOffset;
		
		g.setColor(colorBlack);
		
		// TODO Auto-generated method stub
		for (int i = 0; i < 30;i++) {
			for (int j = 0;j < 30; j++) {
			g.drawRect((int)(i*sizeX / 30), (int)(j*sizeY / 30)+screenMenuYOffset, (int)(sizeX/30-2), (int)(sizeY/30-2));
			}
		}
		
		for (int i = 0; i < 10000; i++) {
				g.drawOval((int)(Math.random()*userInterface.getSize().getWidth()), (int)(Math.random()*userInterface.getSize().getHeight())+screenMenuYOffset, 1, 1);
		}
		
		g.setFont(font);
		g.setColor(colorRed);
		g.drawString(fps + " FPS", 5,18);
		
	}


}
