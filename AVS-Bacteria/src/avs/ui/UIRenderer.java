/**
 * 
 */

package avs.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;

import javax.imageio.ImageIO;

import avs.game.Attributes;
import avs.game.Cell;
import avs.game.CellChange;
import avs.game.GameGrid;
import avs.game.GameManager;

import com.hazelcast.core.Member;
import com.hazelcast.monitor.LocalExecutorStats;

/**
 * @author HZU
 */
public class UIRenderer implements Runnable {

	UserInterface userInterface;

	// geplante fps
	private static final int MAX_FPS = 40;

	// einzelnde Frame Zeit
	private static final int FRAME_PERIOD = 1000 / MAX_FPS;

	// final long timeDelta = 30;

	long lastTime = 0;

	long currentTime = System.currentTimeMillis();

	long timeAccumulator = 0;

	boolean running = true;

	long timeRunning = 0;

	long sleepTime = 0;

	Font font = null;

	Color colorRed = new Color(255, 0, 0);

	Color colorGreen = new Color(0, 255, 0);

	Color colorBlack = new Color(0, 0, 0);

	long fps = 0, cps = 0, runningLoopps = 0;

	long fpscounter = 0, runningCounter = 0, cpscounter = 0;

	long lastFPSTime = 0;

	long updateGridCounter = 0;

	private LinkedList<CellChange> changes = new LinkedList<CellChange>();

	public MouseEvent mouseLastEvent = null;

	public boolean mouseButtonR = false;

	private Point currentHoveredField = new Point(-1, -1);

	private static int gridTiles = 30;

	private BufferedImage imageArrowFriendly = null;

	private BufferedImage imageArrowEnemy = null;

	private BufferedImage imageArrowNeutral = null;

	private BufferedImage imageArrowChoosen = null;

	// private BufferedImage imageFogFriendly = null;
	// private BufferedImage imageFogEnemy = null;
	private BufferedImage imageFloorFriendly = null;

	private BufferedImage imageFloorEnemy = null;

	private BufferedImage imageBackground = null;

	private BufferedImage imageBoard = null;

//	private BufferedImage imageBoardGrid = null;

	private BufferedImage imageBoardPlayersTurn = null;

	private BufferedImage imageBoardEnemyTurn = null;

	private BufferedImage imageEnergyBallFriendly = null;

	private BufferedImage imageEnergyBallNeutral = null;

	private BufferedImage imageEnergyBallEnemy = null;

	private BufferedImage imageBoardLCDDisplay = null;

	private BufferedImage imageProgressBar1 = null;

	private BufferedImage imageProgressBar2 = null;

	private GameGrid showGameGrid = null;

	private GameGrid gameGrid = null;

	private GameManager gameManager;

	private boolean initialized;

	private Rectangle2D.Double gameFieldRectangleCurrent;

	private Rectangle2D.Double gameFieldRectangleDestination;

	private int work = 0, workDone = 0;

	private LocalExecutorStats localExecutorStats;

	private Set<Member> members;

	// private int avaiblePermits = 0;

	private BufferedImage createImage(String position) {
		// prepare a original Image source
		BufferedImage image;
		try {
			image = ImageIO.read(this.getClass().getResourceAsStream(position));
			// Get current GraphicsConfiguration
			GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

			// Create a Compatible BufferedImage
			BufferedImage bufferedImage = graphicsConfiguration.createCompatibleImage(image.getWidth(null), image.getHeight(null), image.getTransparency());
			// Copy from original Image to new Compatible BufferedImage
			Graphics2D tempGraphics = bufferedImage.createGraphics();
			tempGraphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
			tempGraphics.dispose();

			return bufferedImage;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public UIRenderer(UserInterface userInterface) {
		this.userInterface = userInterface;

		// Init Images
		try {
			imageArrowFriendly = createImage("/arrow_friendly.png");
			imageArrowEnemy = createImage("/arrow_enemy.png");
			imageArrowNeutral = createImage("/arrow_neutral.png");
			imageArrowChoosen = createImage("/arrow_choosen.png");
			// imageFogFriendly = ImageIO.read(new File("img/fog_friendly.png"));
			// imageFogEnemy = ImageIO.read(new File("img/fog_enemy.png"));

			imageFloorFriendly = createImage("/floor_friendly.png");
			imageFloorEnemy = createImage("/floor_enemy.png");

			imageBackground = createImage("/background.jpg");
			imageBoard = createImage("/board.png");
			// imageBoardGrid = createImage("/grid.png");
			imageBoardPlayersTurn = createImage("/green_light.png");
			imageBoardEnemyTurn = createImage("/red_light.png");

			imageBoardLCDDisplay = createImage("/lcd_display.png");

			imageProgressBar1 = createImage("/progressbar1.png");
			imageProgressBar2 = createImage("/progressbar2.png");

			imageEnergyBallFriendly = createImage("/energyball_friendly.png");
			imageEnergyBallNeutral = createImage("/energyball_neutral.png");
			imageEnergyBallEnemy = createImage("/energyball_enemy.png");

			font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/DS-DIGI.TTF")).deriveFont(Font.BOLD, 24);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void initialize(GameManager gameManager) {
		this.gameManager = gameManager;

		gameFieldRectangleCurrent = new Rectangle2D.Double();

		gameFieldRectangleCurrent.setRect(userInterface.getWidth() / 2, userInterface.getHeight() / 2, 0, 0);
		gameFieldRectangleDestination = new Rectangle2D.Double();

	}

	@Override
	public void run() {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		Thread.currentThread().setName("AVS:UIRenderer Thread");
		lastTime = System.currentTimeMillis();
		lastFPSTime = lastTime;
		currentTime = System.currentTimeMillis();
		while (running) {
			// Akkumulator befüllen

			currentTime = System.currentTimeMillis();
			if ((currentTime - lastTime) >= FRAME_PERIOD) {
				timeAccumulator += (currentTime - lastTime);
				lastTime = currentTime;
			}

			while (timeAccumulator >= FRAME_PERIOD) {
				timeAccumulator -= FRAME_PERIOD;
				calculate();
			}

			userInterface.repaint();

			if (lastFPSTime <= currentTime - 1000) {
				fps = fpscounter;
				cps = cpscounter;
				runningLoopps = runningCounter;
				fpscounter = 0;
				cpscounter = 0;
				runningCounter = 0;
				lastFPSTime = currentTime;
			}

			runningCounter++;

			// long timeDiff = System.currentTimeMillis() - currentTime;
			// sleepTime = (getFRAME_PERIOD() - timeDiff);
			currentTime = System.currentTimeMillis();
			sleepTime = (currentTime - lastTime) + FRAME_PERIOD;
			// sleepTime = lastTime - (currentTime + FRAME_PERIOD);
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
		synchronized (this) {

			timeRunning += FRAME_PERIOD;
			cpscounter++;
			if (!initialized) {
				return;
			}

			if (updateGridCounter > (2)) {
				if (!changes.isEmpty()) {
					CellChange currentchange = changes.removeFirst();
					if (null != currentchange) {
						gameGrid.consumeCellChange(currentchange);
					}
				}
				updateGridCounter = 0;
			}
			updateGridCounter++;

			// TODO Auto-generated method stub

			int size = userInterface.getWidth();
			if (size > userInterface.getHeight()) {
				size = userInterface.getHeight();
			}

			double update = 20.0;

			// Mousecheck
			if (mouseButtonR) {
				gameFieldRectangleDestination.setRect(size / 4, size / 4, size - size / 2, size - size / 2);
			} else if (!gameManager.isPlayersTurn()) {
				gameFieldRectangleDestination.setRect(size / 8, size / 8, size - size / 4, size - size / 4);
			} else {
				gameFieldRectangleDestination.setRect(size / 20, size / 20, size - size / 10, size - size / 10);
			}

			double setX = ((gameFieldRectangleDestination.getX() - gameFieldRectangleCurrent.getX()));
			if (Math.abs(setX) < 2) {
				setX = 0;
			}
			if (Math.abs(setX) > 50) {
				setX = 50 * Math.signum(setX);
			}

			double setY = ((gameFieldRectangleDestination.getY() - gameFieldRectangleCurrent.getY()));
			if (Math.abs(setY) < 2) {
				setY = 0;
			}
			if (Math.abs(setY) > 50) {
				setY = 50 * Math.signum(setY);
			}

			double setWidth = ((gameFieldRectangleDestination.getWidth() - gameFieldRectangleCurrent.getWidth()));
			if (Math.abs(setWidth) < 2) {
				setWidth = 0;
			}
			if (Math.abs(setWidth) > 100) {
				setWidth = 100 * Math.signum(setWidth);
			}

			double setHeight = ((gameFieldRectangleDestination.getHeight() - gameFieldRectangleCurrent.getHeight()));
			if (Math.abs(setHeight) < 2) {
				setHeight = 0;
			}
			if (Math.abs(setHeight) > 100) {
				setHeight = 100 * Math.signum(setHeight);
			}

			gameFieldRectangleCurrent.setRect(gameFieldRectangleCurrent.getX() + setX / update, gameFieldRectangleCurrent.getY() + setY / update, gameFieldRectangleCurrent.getWidth() + setWidth / update, gameFieldRectangleCurrent.getHeight() + setHeight / update);

			// Aktuell gehoverten Pfeil überblenden
			if (mouseLastEvent != null) {

				if (gameFieldRectangleCurrent.contains(mouseLastEvent.getX(), mouseLastEvent.getY())) {

					currentHoveredField.setLocation(Math.floor(gridTiles * (mouseLastEvent.getX() - gameFieldRectangleCurrent.getX()) / gameFieldRectangleCurrent.width), Math.floor(gridTiles * (mouseLastEvent.getY() - gameFieldRectangleCurrent.getY()) / gameFieldRectangleCurrent.height));
				} else {
					currentHoveredField.setLocation(-1, -1);
				}

			}

			GameGrid tmpGameGrid = gameGrid.getCopy();
			Cell tmpCell = tmpGameGrid.getCell((int) currentHoveredField.getX(), (int) currentHoveredField.getY());
			if (null != tmpCell) {
				if (tmpCell.getOwner() == Attributes.PLAYER) {
					tmpCell.setOwner(Attributes.HOVER);
					tmpGameGrid.processChanges(tmpCell, false);
				} else {
					tmpCell.setOwner(Attributes.HOVER);
				}

			}
			showGameGrid = tmpGameGrid;

		}
	}

	public void draw(Graphics g) {

		final Double gameFieldRectangleCurrentTmp;
		final Dimension userInterfaceTmpSize;
		synchronized (this) {
			gameFieldRectangleCurrentTmp = new Double();
			gameFieldRectangleCurrentTmp.setRect(gameFieldRectangleCurrent);
			userInterfaceTmpSize = new Dimension(userInterface.getSize());
		}

		Graphics2D g2d = (Graphics2D) g;
		// set the opacity
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

		fpscounter++;

		final double lengthX = gameFieldRectangleCurrentTmp.getWidth() / gridTiles;
		final double lengthY = gameFieldRectangleCurrentTmp.getHeight() / gridTiles;

		drawBackground(userInterfaceTmpSize, g2d);
		// the following method takes nearly 1/3 time to render
		drawGameBoard(gameFieldRectangleCurrentTmp, g2d);

		g2d.setColor(colorBlack);

		drawFloor(gameFieldRectangleCurrentTmp, g2d, lengthX, lengthY);

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

		drawEnergyflow(gameFieldRectangleCurrentTmp, g2d, lengthX, lengthY);

		// the following method takes nearly 1/3 time to render
		// drawBoardGrid(gameFieldRectangleCurrentTmp, g2d);

		drawArrows(gameFieldRectangleCurrentTmp, g2d, lengthX, lengthY);

		drawProgressBar(gameFieldRectangleCurrentTmp, g2d, lengthY);

		// drawRectangleAroundGrid(gameFieldRectangleCurrentTmp, g2d);

		drawStatistics(g2d);

		g2d.dispose();
		// }

	}

	private void drawProgressBar(Double gameFieldRectangleCurrentTmp, Graphics2D g2d, double lengthY) {
		if (!gameManager.isPlayersTurn() && work != workDone) {
            g2d.drawImage(
                imageProgressBar1,
                (int) (gameFieldRectangleCurrentTmp.getX()),
                (int) (gameFieldRectangleCurrentTmp.getY()+gameFieldRectangleCurrentTmp.getHeight()+10),
                (int) (gameFieldRectangleCurrentTmp.getX()+gameFieldRectangleCurrentTmp.getWidth()),
                (int) (gameFieldRectangleCurrentTmp.getY()+gameFieldRectangleCurrentTmp.getHeight()+10+lengthY),
                0,
                0,
                imageProgressBar1.getWidth(),
                imageProgressBar1.getHeight(),
                null);
            

            g2d.drawImage(
                    imageProgressBar2,
                    (int) (gameFieldRectangleCurrentTmp.getX()),
                    (int) (gameFieldRectangleCurrentTmp.getY()+gameFieldRectangleCurrentTmp.getHeight()+10),
                    (int) (gameFieldRectangleCurrentTmp.getX()+gameFieldRectangleCurrentTmp.getWidth()*workDone/work),
                    (int) (gameFieldRectangleCurrentTmp.getY()+gameFieldRectangleCurrentTmp.getHeight()+10+lengthY),
                    0,
                    0,
                    (int)(imageProgressBar2.getWidth() *workDone/work),
                    imageProgressBar2.getHeight(),
                    null);

            
            g2d.setColor(colorRed);
            g2d.drawString("The AI is thinking...", (int) (gameFieldRectangleCurrentTmp.getX()+100),(int) (gameFieldRectangleCurrentTmp.getY()+gameFieldRectangleCurrentTmp.getHeight()+25));
		}
	}

//	/**
//	 * @param gameFieldRectangleCurrentTmp
//	 * @param g2d
//	 */
//	private void drawBoardGrid(final Double gameFieldRectangleCurrentTmp, Graphics2D g2d) {
//		g2d.drawImage(imageBoardGrid, (int) (gameFieldRectangleCurrentTmp.getX() - gameFieldRectangleCurrentTmp.getWidth() / 2), (int) (gameFieldRectangleCurrentTmp.getY() - gameFieldRectangleCurrentTmp.getHeight() / 2), (int) (gameFieldRectangleCurrentTmp.getX() + gameFieldRectangleCurrentTmp.getWidth() + gameFieldRectangleCurrentTmp.getWidth() / 2), (int) (gameFieldRectangleCurrentTmp.getY() + gameFieldRectangleCurrentTmp.getHeight() + gameFieldRectangleCurrentTmp.getHeight() / 2), 0, 0, imageBoard.getWidth(), imageBoard.getHeight(), null);
//	}

	/**
	 * @param g2d
	 */
	private void drawStatistics(Graphics2D g2d) {
		g2d.setFont(font);
		g2d.setColor(colorRed);
		g2d.drawString(fps + " FPS", 5, 20);
//		g2d.drawString(cps + " CPS", 5, 40);
//		g2d.drawString(runningLoopps + " RunningLoopsPS", 5, 60);
//		g2d.drawString(work + "/" + workDone + " diff: " + (work - workDone), 100, 20);
//		if (null != localExecutorStats) {
//			g2d.drawString(localExecutorStats.getStartedTaskCount() + " " + localExecutorStats.getPendingTaskCount(), 100, 40);
//		}
		if (null != members) {
			g2d.drawString("Members active: " + members.size(), 5, 40);
			// Iterator<Member> it = members.iterator();
			// int i = 80;
			// while (it.hasNext()) {
			// Member tmpMember = it.next();
			// g2d.drawString(tmpMember.toString() + " : " + tmpMember.getUuid(), 5, i);
			// i += 20;
			// }

		}
		// g2d.drawString("Avaible Permits: " + avaiblePermits, 5, 80);
	}

//	/**
//	 * @param gameFieldRectangleCurrentTmp
//	 * @param g2d
//	 */
//	private void drawRectangleAroundGrid(final Double gameFieldRectangleCurrentTmp, Graphics2D g2d) {
//		g2d.setColor(colorRed);
//		g2d.drawRect((int) gameFieldRectangleCurrentTmp.getX(), (int) gameFieldRectangleCurrentTmp.getY(), (int) gameFieldRectangleCurrentTmp.getWidth(), (int) gameFieldRectangleCurrentTmp.getHeight());
//	}

	/**
	 * @param gameFieldRectangleCurrentTmp
	 * @param g2d
	 * @param lengthX
	 * @param lengthY
	 */
	private void drawArrows(final Double gameFieldRectangleCurrentTmp, Graphics2D g2d, final double lengthX, final double lengthY) {

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

		for (int i = 0; i < gridTiles; i++) {
			for (int j = 0; j < gridTiles; j++) {
				// Draw Arrow
				final double theta;
				final Cell tmpCell = showGameGrid.getCell(i, j);
				switch (tmpCell.getDirection()) {
				case Attributes.UP:
					theta = Attributes.UP_THETA;
					break;
				case Attributes.RIGHT:
					theta = Attributes.RIGHT_THETA;
					break;
				case Attributes.DOWN:
					theta = Attributes.DOWN_THETA;
					break;
				case Attributes.LEFT:
					theta = Attributes.LEFT_THETA;
					break;
				default:
					theta = 0;
				}

				final double positionX = i * lengthX + gameFieldRectangleCurrentTmp.getX();
				final double positionY = j * lengthY + gameFieldRectangleCurrentTmp.getY();
				final double rotateX = positionX + (lengthX / 2);
				final double rotateY = positionY + (lengthY / 2);

				g2d.rotate(theta, rotateX, rotateY);

				switch (tmpCell.getOwner()) {
				case Attributes.PLAYER:
					g2d.drawImage(imageArrowFriendly, (int) positionX, (int) positionY, (int) (positionX + lengthX), (int) (positionY + lengthY), 0, 0, imageArrowFriendly.getWidth(), imageArrowFriendly.getHeight(), null);
					break;
				case Attributes.NEUTRAL:
					g2d.drawImage(imageArrowNeutral, (int) positionX, (int) positionY, (int) (positionX + lengthX), (int) (positionY + lengthY), 0, 0, imageArrowNeutral.getWidth(), imageArrowNeutral.getHeight(), null);
					break;
				case Attributes.AI:
					g2d.drawImage(imageArrowEnemy, (int) positionX, (int) positionY, (int) (positionX + lengthX), (int) (positionY + lengthY), 0, 0, imageArrowEnemy.getWidth(), imageArrowEnemy.getHeight(), null);
					break;
				case Attributes.HOVER:
					g2d.drawImage(imageArrowChoosen, (int) positionX, (int) positionY, (int) (positionX + lengthX), (int) (positionY + lengthY), 0, 0, imageArrowEnemy.getWidth(), imageArrowEnemy.getHeight(), null);
				}

				g2d.rotate(-theta, rotateX, rotateY);
			}
		}
	}

	/**
	 * @param gameFieldRectangleCurrentTmp
	 * @param g2d
	 * @param lengthX
	 * @param lengthY
	 */
	private void drawEnergyflow(final Double gameFieldRectangleCurrentTmp, Graphics2D g2d, final double lengthX, final double lengthY) {
		for (int i = 0; i < gridTiles; i++) {
			for (int j = 0; j < gridTiles; j++) {
				final double theta;
				final Cell tmpCell = showGameGrid.getCell(i, j);
				switch (tmpCell.getDirection()) {
				case Attributes.UP:
					theta = Attributes.UP_THETA;
					break;
				case Attributes.RIGHT:
					theta = Attributes.RIGHT_THETA;
					break;
				case Attributes.DOWN:
					theta = Attributes.DOWN_THETA;
					break;
				case Attributes.LEFT:
					theta = Attributes.LEFT_THETA;
					break;
				default:
					theta = 0;
				}
				final double positionX = i * lengthX + gameFieldRectangleCurrentTmp.getX();
				final double positionY = j * lengthY + gameFieldRectangleCurrentTmp.getY();
				final double rotateX = positionX + (lengthX / 2);
				final double rotateY = positionY + (lengthY / 2);
				g2d.rotate(theta, rotateX, rotateY);

				final double adding = (lengthY) / 2000.0 * (((timeRunning + i * 10 * j * 10 + i * 10) % 2000));

				switch (tmpCell.getOwner()) {
				case Attributes.PLAYER:
					g2d.drawImage(imageEnergyBallFriendly, (int) (positionX + (lengthX / 3)), (int) (positionY - adding + (lengthY / 3)), (int) (positionX + lengthX - (lengthX / 3)), (int) (positionY + lengthY - adding - (lengthX / 3)), 0, 0, imageEnergyBallFriendly.getWidth(), imageEnergyBallFriendly.getHeight(), null);
					break;
				case Attributes.NEUTRAL:
					g2d.drawImage(imageEnergyBallNeutral, (int) (positionX + (lengthX / 3)), (int) (positionY - adding + (lengthY / 3)), (int) (positionX + lengthX - (lengthX / 3)), (int) (positionY + lengthY - adding - (lengthX / 3)), 0, 0, imageEnergyBallNeutral.getWidth(), imageEnergyBallNeutral.getHeight(), null);
					break;
				case Attributes.AI:
					g2d.drawImage(imageEnergyBallEnemy, (int) (positionX + (lengthX / 3)), (int) (positionY - adding + (lengthY / 3)), (int) (positionX + lengthX - (lengthX / 3)), (int) (positionY + lengthY - adding - (lengthX / 3)), 0, 0, imageEnergyBallEnemy.getWidth(), imageEnergyBallEnemy.getHeight(), null);
					break;
				}

				g2d.rotate(-theta, rotateX, rotateY);
			}
		}
	}

	/**
	 * @param gameFieldRectangleCurrentTmp
	 * @param g2d
	 * @param lengthX
	 * @param lengthY
	 */
	private void drawFloor(final Double gameFieldRectangleCurrentTmp, Graphics2D g2d, final double lengthX, final double lengthY) {
		// Player
		for (final Cell cells : showGameGrid.getCellsPossessedByPlayer()) {
			g2d.drawImage(imageFloorFriendly, (int) (cells.getX() * lengthX - (lengthX / 2) + gameFieldRectangleCurrentTmp.getX()), (int) (cells.getY() * lengthY - (lengthY / 2) + gameFieldRectangleCurrentTmp.getY()), (int) ((cells.getX() * lengthX) + (lengthX) + (lengthX / 2) + gameFieldRectangleCurrentTmp.getX()), (int) ((cells.getY() * lengthY) + (lengthY) + (lengthY / 2) + gameFieldRectangleCurrentTmp.getY()), 0, 0, imageFloorFriendly.getWidth(), imageFloorFriendly.getHeight(), null);
		}

		// AI
		for (final Cell cells : showGameGrid.getCellsPossessedByAI()) {
			g2d.drawImage(imageFloorEnemy, (int) (cells.getX() * lengthX - (lengthX / 2) + gameFieldRectangleCurrentTmp.getX()), (int) (cells.getY() * lengthY - (lengthY / 2) + gameFieldRectangleCurrentTmp.getY()), (int) ((cells.getX() * lengthX) + (lengthX) + (lengthX / 2) + gameFieldRectangleCurrentTmp.getX()), (int) ((cells.getY() * lengthY) + (lengthY) + (lengthY / 2) + gameFieldRectangleCurrentTmp.getY()), 0, 0, imageFloorEnemy.getWidth(), imageFloorEnemy.getHeight(), null);
		}
	}

	/**
	 * @param gameFieldRectangleCurrentTmp
	 * @param g2d
	 */
	private void drawGameBoard(final Double gameFieldRectangleCurrentTmp, Graphics2D g2d) {
		g2d.drawImage(imageBoard, (int) (gameFieldRectangleCurrentTmp.getX() - gameFieldRectangleCurrentTmp.getWidth() / 2), (int) (gameFieldRectangleCurrentTmp.getY() - gameFieldRectangleCurrentTmp.getHeight() / 2), (int) (gameFieldRectangleCurrentTmp.getX() + gameFieldRectangleCurrentTmp.getWidth() + gameFieldRectangleCurrentTmp.getWidth() / 2), (int) (gameFieldRectangleCurrentTmp.getY() + gameFieldRectangleCurrentTmp.getHeight() + gameFieldRectangleCurrentTmp.getHeight() / 2), 0, 0, imageBoard.getWidth(), imageBoard.getHeight(), null);

		if (gameManager.isPlayersTurn()) {
			g2d.drawImage(imageBoardPlayersTurn, (int) (gameFieldRectangleCurrentTmp.getX() - gameFieldRectangleCurrentTmp.getWidth() / 2), (int) (gameFieldRectangleCurrentTmp.getY() - gameFieldRectangleCurrentTmp.getHeight() / 2), (int) (gameFieldRectangleCurrentTmp.getX() + gameFieldRectangleCurrentTmp.getWidth() + gameFieldRectangleCurrentTmp.getWidth() / 2), (int) (gameFieldRectangleCurrentTmp.getY() + gameFieldRectangleCurrentTmp.getHeight() + gameFieldRectangleCurrentTmp.getHeight() / 2), 0, 0, imageBoardPlayersTurn.getWidth(), imageBoardPlayersTurn.getHeight(), null);
		} else {
			g2d.drawImage(imageBoardEnemyTurn, (int) (gameFieldRectangleCurrentTmp.getX() - gameFieldRectangleCurrentTmp.getWidth() / 2), (int) (gameFieldRectangleCurrentTmp.getY() - gameFieldRectangleCurrentTmp.getHeight() / 2), (int) (gameFieldRectangleCurrentTmp.getX() + gameFieldRectangleCurrentTmp.getWidth() + gameFieldRectangleCurrentTmp.getWidth() / 2), (int) (gameFieldRectangleCurrentTmp.getY() + gameFieldRectangleCurrentTmp.getHeight() + gameFieldRectangleCurrentTmp.getHeight() / 2), 0, 0, imageBoardEnemyTurn.getWidth(), imageBoardEnemyTurn.getHeight(), null);
		}
		g2d.drawImage(imageBoardLCDDisplay, (int) (gameFieldRectangleCurrentTmp.getX() - gameFieldRectangleCurrentTmp.getWidth() / 2), (int) (gameFieldRectangleCurrentTmp.getY() - gameFieldRectangleCurrentTmp.getHeight() / 2), (int) (gameFieldRectangleCurrentTmp.getX() + gameFieldRectangleCurrentTmp.getWidth() + gameFieldRectangleCurrentTmp.getWidth() / 2), (int) (gameFieldRectangleCurrentTmp.getY() + gameFieldRectangleCurrentTmp.getHeight() + gameFieldRectangleCurrentTmp.getHeight() / 2), 0, 0, imageBoardLCDDisplay.getWidth(), imageBoardLCDDisplay.getHeight(), null);
	}

	/**
	 * @param userInterfaceTmpSize
	 * @param g2d
	 */
	private void drawBackground(final Dimension userInterfaceTmpSize, Graphics2D g2d) {
		g2d.drawImage(imageBackground, 0, 0, (int) userInterfaceTmpSize.getWidth(), (int) userInterfaceTmpSize.getHeight(), (int) ((imageBackground.getWidth() / 2) - (userInterfaceTmpSize.getWidth() / 2)), (int) ((imageBackground.getHeight() / 2) - (userInterfaceTmpSize.getHeight() / 2)), (int) ((imageBackground.getWidth() / 2) + (userInterfaceTmpSize.getWidth() / 2)), (int) ((imageBackground.getHeight() / 2) + (userInterfaceTmpSize.getHeight() / 2)), null);
	}

	public void setGameGrid(GameGrid gameGrid) {
		this.gameGrid = gameGrid;
		this.showGameGrid = this.gameGrid.getCopy();
		gridTiles = gameGrid.getLength();
		initialized = true;
	};

	public void updateGrid(LinkedList<CellChange> changes) {
		this.changes.addAll(changes);
	}

	public void setControl(boolean controlflag) {
		// TODO:
	}

	public Point getClickedMouseField(MouseEvent e) {
		if (gameFieldRectangleCurrent.contains(mouseLastEvent.getX(), mouseLastEvent.getY())) {
			return new Point((int) (Math.floor(gridTiles * (mouseLastEvent.getX() - gameFieldRectangleCurrent.x) / gameFieldRectangleCurrent.width)), (int) Math.floor(gridTiles * (mouseLastEvent.getY() - gameFieldRectangleCurrent.y) / gameFieldRectangleCurrent.height));
		} else {
			return new Point(-1, -1);
		}

	}

	public void setWork(int work) {
		this.work = work;
	}

	public void setWorkDone(int workDone) {
		this.workDone = workDone;
	}

	public void setStats(LocalExecutorStats localExecutorStats) {
		this.setLocalExecutorStats(localExecutorStats);
	}

	public void setMemberStats(Set<Member> members) {
		this.members = members;
	}

    public LocalExecutorStats getLocalExecutorStats() {
        return localExecutorStats;
    }

    public void setLocalExecutorStats(LocalExecutorStats localExecutorStats) {
        this.localExecutorStats = localExecutorStats;
    }
}
