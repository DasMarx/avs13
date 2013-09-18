/**
 * 
 */
package avs.ui;


/**
 * @author HZU
 *
 */
public class ParticleFog {
public double x;
public double y;
private double lifespan;
private double opacity;
private double directionX;
private double directionY;
public int colortype;

public ParticleFog(double x, double y, int colortype) {
	this.x = x;
	this.y = y;
	this.colortype=colortype;
	directionX = Math.random()*0.01-.005;
	directionY = Math.random()*0.01-.005;
	lifespan = (int)(Math.random()*200)+200;
	opacity=0;
}

public void calculate() {
	if (lifespan > 100) {
		opacity++;
		if (opacity > 100) {opacity=100;}
	} else {
		opacity = lifespan;
	}
	x += directionX;
	y += directionY;
	if (lifespan > 0) {lifespan--;}
	}

/**
 * @return the opacity
 */
public float getOpacity() {
	return (float) (opacity/100);
}




}
