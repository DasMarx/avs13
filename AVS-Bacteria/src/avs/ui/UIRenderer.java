/**
 * 
 */

package avs.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import avs.game.Attributes;
import avs.game.Cell;
import avs.game.CellChange;
import avs.game.GameGrid;
import avs.game.GameManager;

/**
 * @author HZU
 */
public class UIRenderer implements Runnable {

    UserInterface userInterface;

    // geplante fps
    private static final int MAX_FPS = 120;
    // einzelnde Frame Zeit
    private static final int FRAME_PERIOD = 1000 / MAX_FPS;
    
//    final long timeDelta = 30;

    long lastTime = 0;

    long currentTime = System.currentTimeMillis();

    long timeAccumulator = 0;

    boolean running = true;

    long timeRunning = 0;

    long sleepTime = 0;

    final Font font = new Font("Arial", Font.BOLD, 20);

    Color colorRed = new Color(255, 0, 0);

    Color colorGreen = new Color(0, 255, 0);

    Color colorBlack = new Color(0, 0, 0);

    long fps = 0, cps = 0, ips = 0;

    long fpscounter = 0, ipscounter = 0, cpscounter = 0;

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

    private BufferedImage imageBoardGrid = null;

    private BufferedImage imageBoardPlayersTurn = null;

    private BufferedImage imageBoardEnemyTurn = null;

    private BufferedImage imageEnergyBallFriendly = null;

    private BufferedImage imageEnergyBallNeutral = null;

    private BufferedImage imageEnergyBallEnemy = null;

    private GameGrid gameGrid = null;

    private GameManager gameManager;

    private boolean initialized;

    private Rectangle2D.Double gameFieldRectangleCurrent;

    private Rectangle2D.Double gameFieldRectangleDestination;

    public UIRenderer(UserInterface userInterface) {
        this.userInterface = userInterface;

        // Init Images
        try {
            imageArrowFriendly = ImageIO.read(new File("img/arrow_friendly.png"));
            imageArrowEnemy = ImageIO.read(new File("img/arrow_enemy.png"));
            imageArrowNeutral = ImageIO.read(new File("img/arrow_neutral.png"));
            imageArrowChoosen = ImageIO.read(new File("img/arrow_choosen.png"));
            // imageFogFriendly = ImageIO.read(new File("img/fog_friendly.png"));
            // imageFogEnemy = ImageIO.read(new File("img/fog_enemy.png"));

            imageFloorFriendly = ImageIO.read(new File("img/floor_friendly.png"));
            imageFloorEnemy = ImageIO.read(new File("img/floor_enemy.png"));

            imageBackground = ImageIO.read(new File("img/background.jpg"));
            imageBoard = ImageIO.read(new File("img/board_greenlight.png"));
            imageBoardGrid = ImageIO.read(new File("img/grid.png"));
            imageBoardPlayersTurn = ImageIO.read(new File("img/green_light.png"));
            imageBoardEnemyTurn = ImageIO.read(new File("img/red_light.png"));

            imageEnergyBallFriendly = ImageIO.read(new File("img/energyball_friendly.png"));
            imageEnergyBallNeutral = ImageIO.read(new File("img/energyball_neutral.png"));
            imageEnergyBallEnemy = ImageIO.read(new File("img/energyball_enemy.png"));

        } catch (IOException e) {
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
        lastTime = System.currentTimeMillis();
        lastFPSTime = lastTime;
        currentTime = System.currentTimeMillis();
        while (running) {
            // Akkumulator befüllen
            

//            if ((currentTime - lastTime) > FRAME_PERIOD) {
//                timeAccumulator = (currentTime - lastTime);
//                lastTime = currentTime;
//            }
//            
//
//            while (timeAccumulator < FRAME_PERIOD) {
//                currentTime = System.currentTimeMillis();
////                timeAccumulator += core.getFRAME_PERIOD();
//                timeAccumulator += FRAME_PERIOD;
////                timeRunning += FRAME_PERIOD;
//                calculate();
//            }

            currentTime = System.currentTimeMillis();
            
            if ((currentTime - lastTime) > FRAME_PERIOD) {
                timeAccumulator = (currentTime - lastTime);
            }
            lastTime = currentTime;
            while (timeAccumulator > FRAME_PERIOD) {
                timeAccumulator -= FRAME_PERIOD;
                
                calculate();
            }
            
            calculate();
            userInterface.repaint();

            
            if (lastFPSTime < currentTime - 500) {
                fps = fpscounter;
                cps = cpscounter;
                ips = ipscounter;
                fpscounter = 0;
                cpscounter = 0;
                ipscounter = 0;
                lastFPSTime = currentTime;
            }

            ipscounter++;
            
//            long timeDiff = System.currentTimeMillis() - currentTime;
//            sleepTime = (getFRAME_PERIOD() - timeDiff);
//            currentTime = System.currentTimeMillis();
            sleepTime = lastTime - currentTime + FRAME_PERIOD;
//            sleepTime = lastTime - (currentTime + FRAME_PERIOD);
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

        double update = 10.0;

        gameFieldRectangleDestination.setRect(size / 20, size / 20, size - size / 10, size - size / 10);

        // Mousecheck
        if (mouseButtonR) {
            gameFieldRectangleDestination.setRect(size / 4, size / 4, size - size / 2, size - size / 2);
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

        gameFieldRectangleCurrent.setRect(
            gameFieldRectangleCurrent.getX() + setX / update,
            gameFieldRectangleCurrent.getY() + setY / update,
            gameFieldRectangleCurrent.getWidth() + setWidth / update,
            gameFieldRectangleCurrent.getHeight() + setHeight / update);

        // Aktuell gehoverten Pfeil überblenden
        if (mouseLastEvent != null) {

            if (gameFieldRectangleCurrent.contains(mouseLastEvent.getX(), mouseLastEvent.getY())) {

                currentHoveredField.setLocation(
                    Math.floor(gridTiles * (mouseLastEvent.getX() - gameFieldRectangleCurrent.x) / gameFieldRectangleCurrent.width),
                    Math.floor(gridTiles * (mouseLastEvent.getY() - gameFieldRectangleCurrent.y) / gameFieldRectangleCurrent.height));
            } else {
                currentHoveredField.setLocation(-1, -1);
            }

        }

    }

    public void draw(Graphics g) {

        // double a = Math.sin(timeRunning / 900.0) * 0;
        // double b = Math.sin(timeRunning / 700.0) * 0;
        // double c = Math.sin(timeRunning / 500.0) * 0;
        // double d = Math.sin(timeRunning / 300.0) * 0;

        // gameFieldRectangleCurrent.setRect(gameFieldRectangleDestination.getX() + (gameFieldRectangleDestination.getX() -
        // gameFieldRectangleCurrent.getX()) / update, gameFieldRectangleDestination.getY() + (gameFieldRectangleDestination.getY() -
        // gameFieldRectangleCurrent.getY()) / update, gameFieldRectangleDestination.getWidth() + (gameFieldRectangleDestination.getWidth()
        // - gameFieldRectangleCurrent.getWidth()) / update, gameFieldRectangleDestination.getHeight() +
        // (gameFieldRectangleDestination.getHeight() - gameFieldRectangleCurrent.getHeight()) / update);

        // gameFieldRectangleCurrent = new Rectangle((int) (a), (int) b , (int) (c + size), (int) (d + size));

        Graphics2D g2d = (Graphics2D) g;
        // set the opacity
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        fpscounter++;
        // g2d.clearRect(0, 0, (int) userInterface.getSize().getWidth(), (int) userInterface.getSize().getHeight());

        // Draw Background
        g2d.drawImage(
            imageBackground,
            0,
            0,
            (int) userInterface.getSize().getWidth(),
            (int) userInterface.getSize().getHeight(),
            (int) ((imageBackground.getWidth() / 2) - (userInterface.getSize().getWidth() / 2)),
            (int) ((imageBackground.getHeight() / 2) - (userInterface.getSize().getHeight() / 2)),
            (int) ((imageBackground.getWidth() / 2) + (userInterface.getSize().getWidth() / 2)),
            (int) ((imageBackground.getHeight() / 2) + (userInterface.getSize().getHeight() / 2)),
            null);

        // TODO: Draw animated stars for background

        // Draw gameBoard
        g2d.drawImage(
            imageBoard,
            (int) (gameFieldRectangleCurrent.getX() - gameFieldRectangleCurrent.getWidth() / 2),
            (int) (gameFieldRectangleCurrent.getY() - gameFieldRectangleCurrent.getHeight() / 2),
            (int) (gameFieldRectangleCurrent.getX() + gameFieldRectangleCurrent.getWidth() + gameFieldRectangleCurrent.getWidth() / 2),
            (int) (gameFieldRectangleCurrent.getY() + gameFieldRectangleCurrent.getHeight() + gameFieldRectangleCurrent.getHeight() / 2),
            0,
            0,
            imageBoard.getWidth(),
            imageBoard.getHeight(),
            null);

        if (gameManager.isPlayersTurn()) {
            g2d.drawImage(
                imageBoardPlayersTurn,
                (int) (gameFieldRectangleCurrent.getX() - gameFieldRectangleCurrent.getWidth() / 2),
                (int) (gameFieldRectangleCurrent.getY() - gameFieldRectangleCurrent.getHeight() / 2),
                (int) (gameFieldRectangleCurrent.getX() + gameFieldRectangleCurrent.getWidth() + gameFieldRectangleCurrent.getWidth() / 2),
                (int) (gameFieldRectangleCurrent.getY() + gameFieldRectangleCurrent.getHeight() + gameFieldRectangleCurrent.getHeight() / 2),
                0,
                0,
                imageBoardPlayersTurn.getWidth(),
                imageBoardPlayersTurn.getHeight(),
                null);
        } else {
            g2d.drawImage(
                imageBoardEnemyTurn,
                (int) (gameFieldRectangleCurrent.getX() - gameFieldRectangleCurrent.getWidth() / 2),
                (int) (gameFieldRectangleCurrent.getY() - gameFieldRectangleCurrent.getHeight() / 2),
                (int) (gameFieldRectangleCurrent.getX() + gameFieldRectangleCurrent.getWidth() + gameFieldRectangleCurrent.getWidth() / 2),
                (int) (gameFieldRectangleCurrent.getY() + gameFieldRectangleCurrent.getHeight() + gameFieldRectangleCurrent.getHeight() / 2),
                0,
                0,
                imageBoardEnemyTurn.getWidth(),
                imageBoardEnemyTurn.getHeight(),
                null);
        }

        g2d.setColor(colorBlack);

        // Draw Floor
        // Player
        for (Cell cells : gameGrid.getCellsPossessedByPlayer()) {
            g2d.drawImage(
                imageFloorFriendly,
                (int) (cells.getX() * gameFieldRectangleCurrent.getWidth() / gridTiles - (gameFieldRectangleCurrent.getWidth() / gridTiles / 2) + gameFieldRectangleCurrent.getX()),
                (int) (cells.getY() * gameFieldRectangleCurrent.getHeight() / gridTiles - (gameFieldRectangleCurrent.getWidth() / gridTiles / 2) + gameFieldRectangleCurrent.getY()),
                (int) ((cells.getX() * gameFieldRectangleCurrent.getWidth() / gridTiles) + (gameFieldRectangleCurrent.getWidth() / gridTiles) + (gameFieldRectangleCurrent.getWidth() / gridTiles / 2) + gameFieldRectangleCurrent.getX()),
                (int) ((cells.getY() * gameFieldRectangleCurrent.getHeight() / gridTiles) + gameFieldRectangleCurrent.getY() + (gameFieldRectangleCurrent.getHeight() / gridTiles) + (gameFieldRectangleCurrent.getWidth() / gridTiles / 2)),
                0,
                0,
                imageFloorFriendly.getWidth(),
                imageFloorFriendly.getHeight(),
                null);
        }

        // AI
        for (Cell cells : gameGrid.getCellsPossessedByAI()) {
            g2d.drawImage(
                imageFloorEnemy,
                (int) (cells.getX() * gameFieldRectangleCurrent.getWidth() / gridTiles - (gameFieldRectangleCurrent.getWidth() / gridTiles / 2) + gameFieldRectangleCurrent.getX()),
                (int) (cells.getY() * gameFieldRectangleCurrent.getHeight() / gridTiles - (gameFieldRectangleCurrent.getWidth() / gridTiles / 2) + gameFieldRectangleCurrent.getY()),
                (int) ((cells.getX() * gameFieldRectangleCurrent.getWidth() / gridTiles) + (gameFieldRectangleCurrent.getWidth() / gridTiles) + (gameFieldRectangleCurrent.getWidth() / gridTiles / 2) + gameFieldRectangleCurrent.getX()),
                (int) ((cells.getY() * gameFieldRectangleCurrent.getHeight() / gridTiles) + gameFieldRectangleCurrent.getY() + (gameFieldRectangleCurrent.getHeight() / gridTiles) + (gameFieldRectangleCurrent.getWidth() / gridTiles / 2)),
                0,
                0,
                imageFloorEnemy.getWidth(),
                imageFloorEnemy.getHeight(),
                null);
        }

        // Draw Energyflow
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        double angle = 0;
        for (int i = 0; i < gridTiles; i++) {
            for (int j = 0; j < gridTiles; j++) {
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

                g2d.rotate(
                    Math.toRadians(angle),
                    (int) (i * gameFieldRectangleCurrent.getWidth() / gridTiles + gameFieldRectangleCurrent.getX() + (gameFieldRectangleCurrent.getWidth() / gridTiles / 2)),
                    (int) (j * gameFieldRectangleCurrent.getHeight() / gridTiles + gameFieldRectangleCurrent.getY()) + (gameFieldRectangleCurrent.getHeight() / gridTiles / 2));

                double adding;

                adding = (timeRunning / 50 + i * j + i) % (gameFieldRectangleCurrent.getWidth() / gridTiles);

                switch (gameGrid.getCell(i, j).getOwner()) {
                case Attributes.PLAYER:
                    g2d.drawImage(
                        imageEnergyBallFriendly,
                        (int) (i * gameFieldRectangleCurrent.getWidth() / gridTiles + gameFieldRectangleCurrent.getX() + (gameFieldRectangleCurrent.getWidth() / gridTiles / 3)),
                        (int) (j * gameFieldRectangleCurrent.getHeight() / gridTiles - adding + gameFieldRectangleCurrent.getY() + (gameFieldRectangleCurrent.getHeight() / gridTiles / 3)),
                        (int) (i * gameFieldRectangleCurrent.getWidth() / gridTiles + (int) (gameFieldRectangleCurrent.getWidth() / gridTiles) + gameFieldRectangleCurrent.getX() - (gameFieldRectangleCurrent.getWidth() / gridTiles / 3)),
                        (int) (j * gameFieldRectangleCurrent.getHeight() / gridTiles + gameFieldRectangleCurrent.getY() + (int) (gameFieldRectangleCurrent.getHeight() / gridTiles - adding - (gameFieldRectangleCurrent.getHeight() / gridTiles / 3))),
                        0,
                        0,
                        imageEnergyBallFriendly.getWidth(),
                        imageEnergyBallFriendly.getHeight(),
                        null);
                    break;
                case Attributes.NEUTRAL:
                    g2d.drawImage(
                        imageEnergyBallNeutral,
                        (int) (i * gameFieldRectangleCurrent.getWidth() / gridTiles + gameFieldRectangleCurrent.getX() + (gameFieldRectangleCurrent.getWidth() / gridTiles / 3)),
                        (int) (j * gameFieldRectangleCurrent.getHeight() / gridTiles - adding + gameFieldRectangleCurrent.getY() + (gameFieldRectangleCurrent.getHeight() / gridTiles / 3)),
                        (int) (i * gameFieldRectangleCurrent.getWidth() / gridTiles + (int) (gameFieldRectangleCurrent.getWidth() / gridTiles) + gameFieldRectangleCurrent.getX() - (gameFieldRectangleCurrent.getWidth() / gridTiles / 3)),
                        (int) (j * gameFieldRectangleCurrent.getHeight() / gridTiles + gameFieldRectangleCurrent.getY() + (int) (gameFieldRectangleCurrent.getHeight() / gridTiles - adding - (gameFieldRectangleCurrent.getHeight() / gridTiles / 3))),
                        0,
                        0,
                        imageEnergyBallNeutral.getWidth(),
                        imageEnergyBallNeutral.getHeight(),
                        null);
                    break;
                case Attributes.AI:
                    g2d.drawImage(
                        imageEnergyBallEnemy,
                        (int) (i * gameFieldRectangleCurrent.getWidth() / gridTiles + gameFieldRectangleCurrent.getX() + (gameFieldRectangleCurrent.getWidth() / gridTiles / 3)),
                        (int) (j * gameFieldRectangleCurrent.getHeight() / gridTiles - adding + gameFieldRectangleCurrent.getY() + (gameFieldRectangleCurrent.getHeight() / gridTiles / 3)),
                        (int) (i * gameFieldRectangleCurrent.getWidth() / gridTiles + (int) (gameFieldRectangleCurrent.getWidth() / gridTiles) + gameFieldRectangleCurrent.getX() - (gameFieldRectangleCurrent.getWidth() / gridTiles / 3)),
                        (int) (j * gameFieldRectangleCurrent.getHeight() / gridTiles + gameFieldRectangleCurrent.getY() + (int) (gameFieldRectangleCurrent.getHeight() / gridTiles - adding - (gameFieldRectangleCurrent.getHeight() / gridTiles / 3))),
                        0,
                        0,
                        imageEnergyBallEnemy.getWidth(),
                        imageEnergyBallEnemy.getHeight(),
                        null);
                    break;
                }

                g2d.rotate(
                    -Math.toRadians(angle),
                    (int) (i * gameFieldRectangleCurrent.getWidth() / gridTiles + gameFieldRectangleCurrent.getX() + (gameFieldRectangleCurrent.getWidth() / gridTiles / 2)),
                    (int) (j * gameFieldRectangleCurrent.getHeight() / gridTiles + gameFieldRectangleCurrent.getY()) + (gameFieldRectangleCurrent.getHeight() / gridTiles / 2));
            }
        }

        // Draw BoardGrid
        g2d.drawImage(
            imageBoardGrid,
            (int) (gameFieldRectangleCurrent.getX() - gameFieldRectangleCurrent.getWidth() / 2),
            (int) (gameFieldRectangleCurrent.getY() - gameFieldRectangleCurrent.getHeight() / 2),
            (int) (gameFieldRectangleCurrent.getX() + gameFieldRectangleCurrent.getWidth() + gameFieldRectangleCurrent.getWidth() / 2),
            (int) (gameFieldRectangleCurrent.getY() + gameFieldRectangleCurrent.getHeight() + gameFieldRectangleCurrent.getHeight() / 2),
            0,
            0,
            imageBoard.getWidth(),
            imageBoard.getHeight(),
            null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        angle = 0;
        // TODO Auto-generated method stub
        for (int i = 0; i < gridTiles; i++) {
            for (int j = 0; j < gridTiles; j++) {
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

                // angle += Math.sin(timeRunning / 150.0) * 45;

                // g.drawRect((int) (i * gameFieldRectangle.width / gridSize),
                // (int) (j * gameFieldRectangle.height / gridSize) + gameFieldRectangle.y,
                // (int) (gameFieldRectangle.width / gridSize ),
                // (int) (gameFieldRectangle.height / gridSize ));

                g2d.rotate(
                    Math.toRadians(angle),
                    (int) (i * gameFieldRectangleCurrent.getWidth() / gridTiles + gameFieldRectangleCurrent.getX() + (gameFieldRectangleCurrent.getWidth() / gridTiles / 2)),
                    (int) (j * gameFieldRectangleCurrent.getHeight() / gridTiles + gameFieldRectangleCurrent.getY()) + (gameFieldRectangleCurrent.getHeight() / gridTiles / 2));

                switch (gameGrid.getCell(i, j).getOwner()) {
                case Attributes.PLAYER:
                    g2d.drawImage(
                        imageArrowFriendly,
                        (int) (i * gameFieldRectangleCurrent.getWidth() / gridTiles + gameFieldRectangleCurrent.getX()),
                        (int) (j * gameFieldRectangleCurrent.getHeight() / gridTiles + gameFieldRectangleCurrent.getY()),
                        (int) (i * gameFieldRectangleCurrent.getWidth() / gridTiles + (int) (gameFieldRectangleCurrent.getWidth() / gridTiles) + gameFieldRectangleCurrent.getX()),
                        (int) (j * gameFieldRectangleCurrent.getHeight() / gridTiles + gameFieldRectangleCurrent.getY() + (int) (gameFieldRectangleCurrent.getHeight() / gridTiles)),
                        0,
                        0,
                        imageArrowFriendly.getWidth(),
                        imageArrowFriendly.getHeight(),
                        null);
                    break;
                case Attributes.NEUTRAL:
                    g2d.drawImage(
                        imageArrowNeutral,
                        (int) (i * gameFieldRectangleCurrent.getWidth() / gridTiles + gameFieldRectangleCurrent.getX()),
                        (int) (j * gameFieldRectangleCurrent.getHeight() / gridTiles + gameFieldRectangleCurrent.getY()),
                        (int) (i * gameFieldRectangleCurrent.getWidth() / gridTiles + (int) (gameFieldRectangleCurrent.getWidth() / gridTiles) + gameFieldRectangleCurrent.getX()),
                        (int) (j * gameFieldRectangleCurrent.getHeight() / gridTiles + gameFieldRectangleCurrent.getY() + (int) (gameFieldRectangleCurrent.getHeight() / gridTiles)),
                        0,
                        0,
                        imageArrowNeutral.getWidth(),
                        imageArrowNeutral.getHeight(),
                        null);
                    break;
                case Attributes.AI:
                    g2d.drawImage(
                        imageArrowEnemy,
                        (int) (i * gameFieldRectangleCurrent.getWidth() / gridTiles + gameFieldRectangleCurrent.getX()),
                        (int) (j * gameFieldRectangleCurrent.getHeight() / gridTiles + gameFieldRectangleCurrent.getY()),
                        (int) (i * gameFieldRectangleCurrent.getWidth() / gridTiles + (int) (gameFieldRectangleCurrent.getWidth() / gridTiles) + gameFieldRectangleCurrent.getX()),
                        (int) (j * gameFieldRectangleCurrent.getHeight() / gridTiles + gameFieldRectangleCurrent.getY() + (int) (gameFieldRectangleCurrent.getHeight() / gridTiles)),
                        0,
                        0,
                        imageArrowEnemy.getWidth(),
                        imageArrowEnemy.getHeight(),
                        null);
                    break;
                }

                // Aktuell gehoverten Pfeil überblenden
                if (currentHoveredField.x == i && currentHoveredField.y == j) {

                    g2d.drawImage(
                        imageArrowChoosen,
                        (int) (i * gameFieldRectangleCurrent.getWidth() / gridTiles + gameFieldRectangleCurrent.getX()),
                        (int) (j * gameFieldRectangleCurrent.getHeight() / gridTiles + gameFieldRectangleCurrent.getY()),
                        (int) (i * gameFieldRectangleCurrent.getWidth() / gridTiles + (int) (gameFieldRectangleCurrent.getWidth() / gridTiles) + gameFieldRectangleCurrent.getX()),
                        (int) (j * gameFieldRectangleCurrent.getHeight() / gridTiles + gameFieldRectangleCurrent.getY() + (int) (gameFieldRectangleCurrent.getHeight() / gridTiles)),
                        0,
                        0,
                        imageArrowEnemy.getWidth(),
                        imageArrowEnemy.getHeight(),
                        null);

                }

                g2d.rotate(
                    -Math.toRadians(angle),
                    (int) (i * gameFieldRectangleCurrent.getWidth() / gridTiles + gameFieldRectangleCurrent.getX() + (gameFieldRectangleCurrent.getWidth() / gridTiles / 2)),
                    (int) (j * gameFieldRectangleCurrent.getHeight() / gridTiles + gameFieldRectangleCurrent.getY()) + (gameFieldRectangleCurrent.getHeight() / gridTiles / 2));

            }
        }

        g2d.setColor(colorRed);
        g2d.drawRect(
            (int) gameFieldRectangleCurrent.getX(),
            (int) gameFieldRectangleCurrent.getY(),
            (int) gameFieldRectangleCurrent.getWidth(),
            (int) gameFieldRectangleCurrent.getHeight());

        g2d.setFont(font);
        g2d.setColor(colorRed);
        g2d.drawString(fps + " FPS", 5, 20);
        g2d.drawString(cps + " CPS", 5, 40);
        g2d.drawString(ips + " IPS", 5, 60);
        g2d.drawString("Players turn? " + String.valueOf(gameManager.isPlayersTurn()), 100, 20);

    }

    public void setGameGrid(GameGrid gameGrid) {
        this.gameGrid = gameGrid;
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
            return new Point(
                (int) (Math.floor(gridTiles * (mouseLastEvent.getX() - gameFieldRectangleCurrent.x) / gameFieldRectangleCurrent.width)),
                (int) Math.floor(gridTiles * (mouseLastEvent.getY() - gameFieldRectangleCurrent.y) / gameFieldRectangleCurrent.height));
        } else {
            return new Point(-1, -1);
        }

    }

}
