
package avs.game;

import java.io.Serializable;

public class Attributes implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5962095539900609258L;

    public static final int UP = 0;

    public static final int RIGHT = 1;

    public static final int DOWN = 2;

    public static final int LEFT = 3;

    public static final int PLAYER = -1;

    public static final int NEUTRAL = 0;

    public static final int AI = 1;

    public static final double UP_THETA = Math.toRadians(0);

    public static final double RIGHT_THETA = Math.toRadians(90);

    public static final double DOWN_THETA = Math.toRadians(180);

    public static final double LEFT_THETA = Math.toRadians(270);

}
