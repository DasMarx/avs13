/**
 * 
 */
package avs.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import avs.game.Attributes;
import avs.game.Cell;
import avs.game.CellChanges;
import avs.game.GameGrid;
import avs.game.GameManager;

/**
 * @author HZU
 * 
 */
public class UIRenderer implements Runnable {
	UserInterface userInterface;
	final long timeDelta = 30;
	long lastTime = 0;
	long currentTime = System.currentTimeMillis();
	long timeAccumulator = 0;
	boolean running = true;
	long timeRunning = 0;
	long sleepTime = 0;
	final Font font = new Font("Arial", Font.BOLD, 20);
	Color colorRed = new Color(255, 0, 0);
	Color colorBlack = new Color(0, 0, 0);
	long fps = 0;
	long fpscounter = 0;
	long lastFPSTime = 0;

	private static int gridSize = 5;

	int screenMenuYOffset = 20;

	private BufferedImage imageArrowFriendly = null;
	private BufferedImage imageArrowEnemy = null;
	private BufferedImage imageArrowNeutral = null;
	private BufferedImage imageFogFriendly = null;
	private BufferedImage imageFogEnemy = null;
	private BufferedImage imageFloorFriendly = null;
	private BufferedImage imageFloorEnemy = null;

	private LinkedList<ParticleFog> particlesFog;

	private GameGrid gameGrid = null;
	private GameManager gameManager;
	private boolean initialized;

	public UIRenderer(UserInterface userInterface) {
		this.userInterface = userInterface;

		// Init Images
		try {
			imageArrowFriendly = ImageIO.read(new File("img/arrow_friendly.png"));
			imageArrowEnemy = ImageIO.read(new File("img/arrow_enemy.png"));
			imageArrowNeutral = ImageIO.read(new File("img/arrow_neutral.png"));
			imageFogFriendly = ImageIO.read(new File("img/fog_friendly.png"));
			imageFogEnemy = ImageIO.read(new File("img/fog_enemy.png"));

			imageFloorFriendly = ImageIO.read(new File("img/floor_friendly.png"));
			imageFloorEnemy = ImageIO.read(new File("img/floor_enemy.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Init GameFog
		particlesFog = new LinkedList<ParticleFog>();

	}

	public void initialize(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public void run() {
		lastTime = System.currentTimeMillis();
		lastFPSTime = lastTime;

		while (running) {
			// Akkumulator befüllen
			currentTime = System.currentTimeMillis();

			if ((currentTime - lastTime) > timeDelta) {
				timeAccumulator = (currentTime - lastTime);
				lastTime = currentTime;

			}

			while (timeAccumulator > timeDelta) {
				timeAccumulator -= timeDelta;
				timeRunning += timeDelta;
				calculate();
			}

			userInterface.repaint();

			if (lastFPSTime < currentTime - 500) {
				fps = fpscounter;
				fpscounter = 0;
				lastFPSTime = currentTime;
			}

			sleepTime = lastTime - currentTime + timeDelta;
			if (sleepTime > 0) {
				try {
					// System.out.println(sleepTime);
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private void calculate() {
		if (!initialized) {
			return;
		}

		// TODO Auto-generated method stub

		double sizeX = userInterface.getSize().getWidth();
		double sizeY = userInterface.getSize().getHeight() - screenMenuYOffset;

		// Calculate Fog
		synchronized (particlesFog) {
			// TODO:Remove
			// int x = (int) (Math.random() * gridSize);
			// int y = (int) (Math.random() * gridSize);
			// gameGrid.getCell(x, y).turn();
			int x;
			int y;

			synchronized (gameGrid) {
				Cell cell;
				
				
				cell = gameGrid.getCellsPossessedByPlayer().get((int)Math.random()*gameGrid.getCellsPossessedByPlayer().size());
				particlesFog.add(new ParticleFog((cell.getX()	* (sizeX / gridSize) + (sizeX / gridSize / 2)), (cell.getY() * sizeY / gridSize) + screenMenuYOffset + (sizeY / gridSize / 2), Attributes.PLAYER));
				
				cell = gameGrid.getCellsPossessedByAI().get((int)Math.random()*gameGrid.getCellsPossessedByAI().size());
				particlesFog.add(new ParticleFog((cell.getX() * (sizeX / gridSize) + (sizeX / gridSize / 2)), (cell.getY() * sizeY / gridSize) + screenMenuYOffset + (sizeY / gridSize / 2), Attributes.AI));
					
			}
	
			
			
			Iterator it = particlesFog.iterator();
			ParticleFog fogParticle;
			while (it.hasNext()) {
				fogParticle = (ParticleFog) it.next();
				fogParticle.calculate();
				if (fogParticle.getOpacity() == 0) {
					it.remove();
				}
			}

		}
	}

	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		// set the opacity
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);

		fpscounter++;
		g.clearRect(0, 0, (int) userInterface.getSize().getWidth(),
				(int) userInterface.getSize().getHeight());

		double sizeX = userInterface.getSize().getWidth();
		double sizeY = userInterface.getSize().getHeight() - screenMenuYOffset;

		g2d.setColor(colorBlack);

		// Draw Floor
		// Player
		 for (Cell cells : gameGrid.getCellsPossessedByPlayer()) {
		 g2d.drawImage(imageFloorFriendly,
		 (int) (cells.getX() * sizeX / gridSize - (sizeX / gridSize /2)),
		 (int) (cells.getY() * sizeY / gridSize-(sizeX / gridSize /2)) +
		 screenMenuYOffset,
		 (int) ((cells.getX() * sizeX / gridSize) + (sizeX / gridSize) +
		 (sizeX / gridSize /2)),
		 (int) ((cells.getY() * sizeY / gridSize) + screenMenuYOffset + (sizeY
		 / gridSize) + (sizeX / gridSize /2)), 0, 0,
		 imageFloorFriendly.getWidth(),
		 imageFloorFriendly.getHeight(), null);
		 }

		// AI
		 for (Cell cells : gameGrid.getCellsPossessedByAI()) {
		 g2d.drawImage(imageFloorEnemy,
		 (int) (cells.getX() * sizeX / gridSize - (sizeX / gridSize /2)),
		 (int) (cells.getY() * sizeY / gridSize-(sizeX / gridSize /2)) +
		 screenMenuYOffset,
		 (int) ((cells.getX() * sizeX / gridSize) + (sizeX / gridSize) +
		 (sizeX / gridSize /2)),
		 (int) ((cells.getY() * sizeY / gridSize) + screenMenuYOffset + (sizeY
		 / gridSize) + (sizeX / gridSize /2)), 0, 0,
		 imageFloorEnemy.getWidth(),
		 imageFloorEnemy.getHeight(), null);
		 }

		 
		// Draw Fog
		synchronized (particlesFog) {
			Iterator it = particlesFog.iterator();
			ParticleFog fogParticle;
			int fieldSize = (int) (sizeY / gridSize);
			while (it.hasNext()) {
				fogParticle = (ParticleFog) it.next();
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fogParticle.getOpacity()));
				
				switch (fogParticle.colortype) {
				case Attributes.PLAYER:
					g2d.drawImage(imageFogFriendly,
							(int) (fogParticle.x - fieldSize),
							(int) (fogParticle.y - fieldSize),
							(int) (fogParticle.x + fieldSize),
							(int) (fogParticle.y + fieldSize), 0, 0,
							imageFogFriendly.getWidth(),
							imageFogFriendly.getHeight(), null);
					break;
				case Attributes.AI:
					g2d.drawImage(imageFogEnemy,
							(int) (fogParticle.x - fieldSize),
							(int) (fogParticle.y - fieldSize),
							(int) (fogParticle.x + fieldSize),
							(int) (fogParticle.y + fieldSize), 0, 0,
							imageFogEnemy.getWidth(),
							imageFogEnemy.getHeight(), null);
					break;
				default:
					break;
				}
			}
		}

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		double angle = 0;
		// TODO Auto-generated method stub
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				// Draw Arrow
				switch (gameGrid.getCell(i, j).getDirection()) {
				case Attributes.UP:
					angle = 0;
					break;
				case Attributes.RIGHT:
					angle = 90;
					break;
				case Attributes.DOWN:
					angle = 180;
					break;
				case Attributes.LEFT:
					angle = 270;
					break;
				}

				angle += Math.sin(timeRunning / 150.0) * 45;

				// g.drawRect((int) (i * sizeX / gridSize),
				// (int) (j * sizeY / gridSize) + screenMenuYOffset,
				// (int) (sizeX / gridSize ),
				// (int) (sizeY / gridSize ));

				g2d.rotate(Math.toRadians(angle),
						(int) (i * sizeX / gridSize + (sizeX / gridSize / 2)),
						(int) (j * sizeY / gridSize + screenMenuYOffset)
								+ (sizeY / gridSize / 2));

				switch (gameGrid.getCell(i, j).getOwner()) {
				case Attributes.PLAYER:
					g2d.drawImage(imageArrowFriendly,
							(int) (i * sizeX / gridSize),
							(int) (j * sizeY / gridSize) + screenMenuYOffset,
							(int) (i * sizeX / gridSize)
									+ (int) (sizeX / gridSize), (int) (j
									* sizeY / gridSize)
									+ screenMenuYOffset
									+ (int) (sizeY / gridSize), 0, 0,
							imageArrowFriendly.getWidth(),
							imageArrowFriendly.getHeight(), null);
					break;
				case Attributes.NEUTRAL:
					g2d.drawImage(imageArrowNeutral,
							(int) (i * sizeX / gridSize),
							(int) (j * sizeY / gridSize) + screenMenuYOffset,
							(int) (i * sizeX / gridSize)
									+ (int) (sizeX / gridSize), (int) (j
									* sizeY / gridSize)
									+ screenMenuYOffset
									+ (int) (sizeY / gridSize), 0, 0,
							imageArrowNeutral.getWidth(),
							imageArrowNeutral.getHeight(), null);
					break;
				case Attributes.AI:
					g2d.drawImage(imageArrowEnemy,
							(int) (i * sizeX / gridSize),
							(int) (j * sizeY / gridSize) + screenMenuYOffset,
							(int) (i * sizeX / gridSize)
									+ (int) (sizeX / gridSize), (int) (j
									* sizeY / gridSize)
									+ screenMenuYOffset
									+ (int) (sizeY / gridSize), 0, 0,
							imageArrowEnemy.getWidth(),
							imageArrowEnemy.getHeight(), null);
					break;
				}

				g2d.rotate(-Math.toRadians(angle),
						(int) (i * sizeX / gridSize + (sizeX / gridSize / 2)),
						(int) (j * sizeY / gridSize + screenMenuYOffset)
								+ (sizeY / gridSize / 2));
			}
		}


		g2d.setFont(font);
		g2d.setColor(colorRed);
		g2d.drawString(fps + " FPS" + " --- Particles: " + particlesFog.size(),	5, 18);

	}

	public void setGameGrid(GameGrid gameGrid) {
		this.gameGrid = gameGrid;
		gridSize = gameGrid.getLength();
		initialized = true;
	};

	public void updateGrid(LinkedList<CellChanges> changes) {
		// TODO:
	}

	public void setControl(boolean controlflag) {
		// TODO:
	}

}
