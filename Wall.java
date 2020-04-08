/**
 * Wall.java
 * 	
 * Represents a wall that allows people to bounce
 * off of them. A set of walls can be used to form
 * an enclosed space; this implementation is used to
 * form the public, enclosed spaces in the simulation.
 *
 * @author Krish Agarwal
 * @since 4/7/2020
 */

public class Wall
{
    private double centerX, centerY, halfLength, halfWidth;

    /**
     * Instantiates a wWall object
     * @param centerX the center x coordinate
     * @param centerY the center y coordinate
     * @param halfLength half the length (horizontally)
     * @param halfWidth half the width (vertically)
     */
    public Wall(double centerX, double centerY, double halfLength, double halfWidth)
    {
        this.centerX = centerX;
        this.centerY = centerY;
        this.halfLength = halfLength;
        this.halfWidth = halfWidth;
    }

    /**
     * Draws the wall
     */
    public void draw()
    {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.filledRectangle(centerX, centerY, halfLength, halfWidth);
    }

    /**
     * Checks if a given test value is inclusively within a
     * given range of values
     * @param testVal the value to test
     * @param lowerBound the lower bound of the range
     * @param upperBound the upper bound of the range
     * @return whether or not the test value us inclusively within the range
     */
    private static boolean isInRange(double testVal, double lowerBound, double upperBound)
    {
        return testVal >= lowerBound && testVal <= upperBound;
    }

    /**
     * Checks if the given person collides with the wall, and updates
     * the given person's velocity if there is a collision
     * @param p the person to check
     * @return whether or not the collision has occurred
     */
    public boolean checkCollision(Person p)
    {
        boolean sameWall = p.getLastCollidedWall() == this, vertical = collidesVertically(p), horizontal = collidesHorizontally(p);
        if (!sameWall && vertical)
            p.negateVerticalSpeed();
        else if (!sameWall && horizontal)
            p.negateHorizontalSpeed();
        if (sameWall && !vertical && !horizontal)
            p.setLastCollidedWall(null);
        if (sameWall || !vertical || !horizontal)
            return false;
        p.setLastCollidedWall(this);
        return true;
    }

    /**
     * Determines if the given person collides with the wall vertically
     * @param p the person to check
     * @return wheter or not there is a vertical collision
     */
    public boolean collidesVertically(Person p)
    {
        double x = p.getX() + p.getDX(), y = p.getY() + p.getDY();
        return isInRange(x, centerX - halfLength, centerX + halfLength) &&
            (isInRange(y + p.getRadius(), centerY - halfWidth, centerY + halfWidth)
            || isInRange(y - p.getRadius(), centerY - halfWidth, centerY + halfWidth));
    }

    /**
     * Determines if the given person collides with the wall horizontally
     * @param p the person to check
     * @return whether or not there is a horizontal collision
     */
    public boolean collidesHorizontally(Person p)
    {
        double x = p.getX() + 2 * p.getDX(), y = p.getY() + 2 * p.getDY();
        return isInRange(y, centerY - halfWidth, centerY + halfWidth) &&
            (isInRange(x + p.getRadius(), centerX - halfLength, centerX + halfLength)
            || isInRange(x - p.getRadius(), centerX - halfLength, centerX + halfLength));
    }
}