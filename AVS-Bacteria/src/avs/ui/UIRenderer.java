/**
 * 
 */
package avs.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;



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
	
	private static int tilesX = 10;
	private static int tilesY = 10;
	
	int screenMenuYOffset = 20;
	
	private BufferedImage iconArrowFriendly = null;
	private BufferedImage iconArrowEnemy = null;
	private BufferedImage iconArrowNeutral = null;
	
	private int dreh =0;
	
	public UIRenderer(UserInterface userInterface) {
		this.userInterface = userInterface;
		
		//Init Images
		try {
			iconArrowFriendly = ImageIO.read(new File("img/arrow_friendly.png"));
			iconArrowEnemy = ImageIO.read(new File("img/arrow_enemy.png"));
			iconArrowNeutral = ImageIO.read(new File("img/arrow_neutral.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
		Graphics2D g2d = (Graphics2D)g;
		fpscounter++;
		g.clearRect(0, 0, (int)userInterface.getSize().getWidth(), (int)userInterface.getSize().getHeight());
		
		double sizeX = userInterface.getSize().getWidth();
		double sizeY = userInterface.getSize().getHeight() -screenMenuYOffset;
		
		g.setColor(colorBlack);
		
		dreh= (dreh+2) % 360;
		
		// TODO Auto-generated method stub
		for (int i = 0; i < tilesX;i++) {
			for (int j = 0;j < tilesY; j++) {
			g.drawRect((int)(i*sizeX / tilesX), (int)(j*sizeY / tilesY)+screenMenuYOffset, (int)(sizeX/tilesX-2), (int)(sizeY/tilesY-2));
			
			
			
			
			g2d.rotate(Math.toRadians(dreh+i*j),(int)(i*sizeX / tilesX +(sizeX / tilesX /2)),(int)(j*sizeY / tilesY+screenMenuYOffset)+(sizeY / tilesY /2));
			
			switch ((i+j) % 3) {
			case 0:
				g2d.drawImage(iconArrowFriendly, (int)(i*sizeX / tilesX), (int)(j*sizeY / tilesY)+screenMenuYOffset, (int)(i*sizeX / tilesX)+(int)(sizeX/tilesX-2), (int)(j*sizeY / tilesY)+screenMenuYOffset+(int)(sizeY/tilesY-2), 0, 0, iconArrowFriendly.getWidth(), iconArrowFriendly.getHeight(), null);				
				break;
			case 1:
				g2d.drawImage(iconArrowNeutral, (int)(i*sizeX / tilesX), (int)(j*sizeY / tilesY)+screenMenuYOffset, (int)(i*sizeX / tilesX)+(int)(sizeX/tilesX-2), (int)(j*sizeY / tilesY)+screenMenuYOffset+(int)(sizeY/tilesY-2), 0, 0, iconArrowFriendly.getWidth(), iconArrowFriendly.getHeight(), null);				
				break;
			case 2:
				g2d.drawImage(iconArrowEnemy, (int)(i*sizeX / tilesX), (int)(j*sizeY / tilesY)+screenMenuYOffset, (int)(i*sizeX / tilesX)+(int)(sizeX/tilesX-2), (int)(j*sizeY / tilesY)+screenMenuYOffset+(int)(sizeY/tilesY-2), 0, 0, iconArrowFriendly.getWidth(), iconArrowFriendly.getHeight(), null);				
				break;
			default:
				break;
			}
			
			g2d.rotate(-Math.toRadians(dreh+i*j),(int)(i*sizeX / tilesX+(sizeX / tilesX /2)),(int)(j*sizeY / tilesY+screenMenuYOffset)+(sizeY / tilesY /2));
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
