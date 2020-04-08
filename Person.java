/**
 * Person.java
 * 	
 * Represents a person in the simulation with the person
 * able to move, react to collisions, and undergo different
 * stages of the virus (infection, being cured/death). The
 * person behaves as a ball that has a random velocity, one
 * which is changed when it collides with another person or
 * a wall. When a person dies, they immediately stop and no
 * longer participate in collisions with alive people.
 *
 * @author Krish Agarwal (with starter code by Mr. DeRuiter)
 * @since 3/28/2020
 */

import java.awt.Color;

public class Person
{
	private double x, y, dx, dy, radius, infectRate;
	private int viralState;     //  0 uninfected, 1 infected, 2 recovered, 3 dead
	private int daysSick, alpha;
	private boolean shelterInPlace, willDie;
	private Person lastCollided;
	private Wall lastCollidedWall;
	
	/**
	 * Initializes a Person object.
	 * @param viralState the initial viral state of the person
	 * @param infectRate the probability of infecting another person
	 * @param shelterInPlaceRate the rate of people who are sheltering-in-place
	 * @param deathRate the rate of infected people who die from the virus
	 */
	public Person (int viralState, double infectRate, double shelterInPlaceRate, double deathRate)
	{
		radius = 0.2;
		x = Math.random() * (20.0 - 2 * radius) - (10.0 - radius);
		y = Math.random() * (20.0 - 2 * radius) - (10.0 - radius);
		dx = Math.random() * radius - radius / 2;
		dy = Math.random() * radius - radius / 2;
		this.viralState = viralState;
		daysSick = 0;
		this.shelterInPlace = ratePasses(shelterInPlaceRate);
		this.infectRate = infectRate;
		willDie = ratePasses(deathRate);
		lastCollided = null;
		lastCollidedWall = null;
		alpha = 255;
	}

	/**
	 * Updates the lastCollidedWall variable, which stores the
	 * wall that the person last collided with
	 * @param wall the new lastCollidedWall
	 */
	public void setLastCollidedWall(Wall wall)
	{
		lastCollidedWall = wall;
	}

	/**
	 * Returns the wall which the person last collided with
	 * @return the last collided wall
	 */
	public Wall getLastCollidedWall()
	{
		return lastCollidedWall;
	}

	/**
	 * Negates the horizontal velocity
	 */
	public void negateHorizontalSpeed()
	{
		dx *= -1;
	}

	/**
	 * Negates the vertical velocity
	 */
	public void negateVerticalSpeed()
	{
		dy *= -1;
	}

	/**
	 * Retuns the x position
	 * @return the x position
	 */
	public double getX()
	{
		return x;
	}

	/**
	 * Returns the y position
	 * @return the y position
	 */
	public double getY()
	{
		return y;
	}

	/**
	 * Returns the horizontal component of velocity
	 * @return the horizontal component of velocity
	 */
	public double getDX()
	{
		return dx;
	}

	/**
	 * Returns the vertical component of velocity
	 * @return the vertical component of velocity
	 */
	public double getDY()
	{
		return dy;
	}

	/**
	 * Returns the radius of the ball representing the person
	 * @return the radius
	 */
	public double getRadius()
	{
		return radius;
	}

	/**
	 * Has the probability of the given rate to return true and the
	 * probavility of 1 - rate to return false
	 * @param rate the rate/probability
	 * @return true or false based on probability
	 */
	private static boolean ratePasses(double rate)
	{
		return (int)(Math.random() * 100) < (int)(rate * 100);
	}

	/**
	 * Changes the position of the person and updates the lastCollided
	 * variable, which determines the last person collided with
	 */
	public void changePosition ()
	{
		if (lastCollided != null && !collidesWith(lastCollided))
			lastCollided = null;
		if (shelterInPlace)
			return;
		if (Math.abs(x + dx) > 10.0 - radius)
			dx = -dx;
		if (Math.abs(y + dy) > 10.0 - radius)
			dy = -dy;

		x = x + dx;
		y = y + dy;
	}

	/**
	 * Updates the viral state of the person
	 */
	public void updateViralState()
	{
		if (viralState == 1)
			daysSick++;
		if (daysSick == 600 && willDie)
			viralState = 3;
		else if (daysSick == 600)
			viralState = 2;
	}

	/**
	 * Assuming that the current person is not sheltering-in-place,
	 * determines the new velocity components due to colliding with
	 * a given person that is sheltering-in-place
	 * @param shelterer the person sheltering-in-place
	 */
	private void setCollisionWithShelterInPlace(Person shelterer)
	{
		double theta = Math.atan2(y - shelterer.y, x - shelterer.x), speed = Math.sqrt(dx * dx + dy * dy);
		theta += Math.random() * Math.PI / 8 - Math.PI / 16;
		dx = speed * Math.cos(theta);
		dy = speed * Math.sin(theta);
	}

	/**
	 * Determines if the current person is colliding with the given person
	 * @param other the person to check collision with
	 * @return whether or not the two are colliding
	 */
	public boolean collidesWith(Person other)
	{
		return viralState != 3 && other.viralState != 3 && Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2)) <= radius + other.radius;
	}

	/**
	 * Checks if the current person collides with the given person and updates
	 * the velocities of both people if they are colliding
	 * @param other the person to check collision with
	 */
	public void checkCollision(Person other)
	{
		if (other == lastCollided || !collidesWith(other))
			return;
		if (shelterInPlace)
			other.setCollisionWithShelterInPlace(this);
		else if (other.shelterInPlace)
			setCollisionWithShelterInPlace(other);
		else
		{
			double tempdx = other.dx, tempdy = other.dy;
			other.dx = dx;
			other.dy = dy;
			dx = tempdx;
			dy = tempdy;
		}
		if (other.viralState == 1 && viralState == 0 && ratePasses(infectRate))
			viralState = other.viralState;
		else if (viralState == 1 && other.viralState == 0 && ratePasses(infectRate))
			other.viralState = viralState;
		lastCollided = other;
		other.lastCollided = this;
	}

	/**
	 * Returns the person's viral state
	 * @return the viral state
	 */
	public int getViralState()
	{
		return viralState;
	}
	
	/**
	 * Draws the person, using a ball to represent the person
	 * and different colors to show the viral state (black - uninfected,
	 * red - infected, green - cured, yellow with a red X - dead)
	 */
	public void draw ()
	{
		StdDraw.setPenColor(StdDraw.BLACK);
		if(viralState == 1)
			StdDraw.setPenColor(StdDraw.RED);
		else if(viralState == 2)
			StdDraw.setPenColor(StdDraw.GREEN);
		else if (viralState == 3)
			StdDraw.setPenColor(new Color(240, 215, 50, alpha));
		
		StdDraw.filledCircle(x, y, radius);
		StdDraw.setPenColor(new Color(0, 0, 0, alpha));
		StdDraw.circle(x, y, radius);
		
		if (viralState != 3)
			return;
		StdDraw.setPenColor(new Color(255, 0, 0, alpha));
		StdDraw.setPenRadius(.004);
		StdDraw.line(x - radius, y - radius, x + radius, y + radius);
		StdDraw.line(x + radius, y - radius, x - radius, y + radius);
		if (alpha > 100)
			alpha--;
		StdDraw.setPenRadius();
	}
}