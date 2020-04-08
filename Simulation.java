/**
 * Simulation.java
 * 
 * HOW TO RUN:
 * type the following in the terminal: "java Simulation".
 * No command-line arguments are required, as all information
 * is read using run-time input.
 * 
 * Simulates a situation in which a virus spreads through
 * a population of people, spread by contact. The details
 * of the virus are provided by the user, as well as the
 * properties of the population. The virus is spread by
 * contact. Each person is represented by a ball with a
 * random position and velocity, and contact between
 * people occurs through elastic collisions between them.
 * This program simulates the spread of the disease as well
 * its decay as more and more people become cured or die.
 *
 * @author Krish Agarwal (with starter code by Mr. DeRuiter)
 * @since 8/28/2019
 */

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

public class Simulation
{
	private int numberOfPeople, numRooms;
	private double infectRate, shelterInPlaceRate, deathRate;
	private ArrayList<Person> people, dead;
	private ArrayList<Wall> walls;
	private ArrayList<Double> percentInfected, percentCured, percentHealthy, percentDead;
	public static double totalPercentUninfected = 0, totalPercentDead = 0, totalPercentCured = 0, numSimulations = 0;

	/**
	 * Instantiates a Simulation object
	 * @param numberOfPeople the number of people in the simulation
	 * @param infectRate the infection rate of the virus
	 * @param shelterInPlaceRate the fraction of people sheltering-in-place
	 * @param deathRate the fraction of people who die from the virus
	 * @param numRooms the number of public, enclosed spaces in the simulation
	 */
	public Simulation (int numberOfPeople, double infectRate, double shelterInPlaceRate, double deathRate, int numRooms)
	{
		this.numberOfPeople = numberOfPeople;
		this.infectRate = infectRate;
		this.shelterInPlaceRate = shelterInPlaceRate;
		this.deathRate = deathRate;
		this.numRooms = numRooms;

		walls = new ArrayList<Wall>();
		putRooms(numRooms);

		people = new ArrayList<Person>();
		dead = new ArrayList<Person>();
		for(int i = 0; i < numberOfPeople - 1; i++)
			addNew(numberOfPeople, 0, infectRate, shelterInPlaceRate, deathRate);
		if (shelterInPlaceRate < 1)
			addNew(numberOfPeople, 1, infectRate, 0, deathRate);
		else
			addNew(numberOfPeople, 1, infectRate, shelterInPlaceRate, deathRate);
		percentInfected = new ArrayList<Double>();
		percentCured = new ArrayList<Double>();
		percentHealthy = new ArrayList<Double>();
		percentDead = new ArrayList<Double>();
	}

	/**
	 * Adds a new person at a location where they will not overlap with
	 * other people or walls
	 * @param numExpected the number of people expected for the simulation
	 * @param viralState the viral state that the new person should have
	 * @param infectRate the infection rate of the virus
	 * @param shelterInPlaceRate the fraction of people sheltering-in-place
	 * @param deathRate the fraction of people who die from contracting the virus
	 */
	public void addNew(int numExpected, int viralState, double infectRate, double shelterInPlaceRate, double deathRate)
	{
		Person add = new Person(viralState, infectRate, shelterInPlaceRate, deathRate);
		while (numExpected <= 2000 && hasOverLaps(add))
			add = new Person(viralState, infectRate, shelterInPlaceRate, deathRate);
		people.add(add);	
	}

	/**
	 * Adds the given number of public, enclosed spaces in an arrangement
	 * where their centers are equidistant from the origin
	 * @param numRooms the number of public, enclosed spaces to add
	 */
	public void putRooms(int numRooms)
	{
		if (numRooms == 1)
		{
			addRoom(0.0, 0.0);
			return;
		}

		for (int i = 0; i < numRooms; i++)
		{
			double theta = Math.PI / 2 + 2 * Math.PI / numRooms * i;
			addRoom(6 * Math.cos(theta), 6 * Math.sin(theta));
		}
	}

	/**
	 * Given the center coordinates of the public/enclosed space, adds the
	 * public/enclosed space using a set of walls
	 * @param centerX the center x coordinate
	 * @param centerY the center y coordinate
	 */
	public void addRoom(double centerX, double centerY)
	{
		walls.add(new Wall(centerX, centerY + 2.5, 2.5, 0.3));
		walls.add(new Wall(centerX + -2.2, centerY, 0.3, 2.2));
		walls.add(new Wall(centerX + 2.2, centerY, 0.3, 2.2));
		walls.add(new Wall(centerX + -1.5, centerY + -2.5, 1.0, .3));
		walls.add(new Wall(centerX + 1.5, centerY + -2.5, 1.0, .3));
	}

	/**
	 * Checks if the given person overlaps with any other elements
	 * in the simulation (eg. other people, walls)
	 * @param check the person to check
	 * @return whether or not the person overlaps with anything
	 */
	public boolean hasOverLaps(Person check)
	{
		for (Person curr : people)
			if (curr.collidesWith(check))
				return true;
		for (Wall curr : walls)
			if (curr.collidesHorizontally(check) || curr.collidesVertically(check))
				return true;
		return false;
	}

	/**
	 * The main entry point to the program
	 * @param args the command-line arguments (none used here)
	 */
	public static void main(String [] args) 
	{
		System.out.println("\n\n\nCREATE A VIRUS SIMULATION\n");
		int numberOfPeople = Prompt.getInt("Enter the number of people in the simulation", 1, 2000);
		double infectRate = Prompt.getDouble("Enter the infection rate of the virus", 0.0, 1.0);
		double shelterInPlaceRate = Prompt.getDouble("Enter the fraction of people who are sheltering-in-place", 0.0, 1.0);
		double deathRate = Prompt.getDouble("Enter the death rate of the virus", 0.0, 1.0);
		int numberRooms = Prompt.getInt("Enter the number of public, enclosed spaces (eg. grocery stores) in the simulation", 0, 5);
		Simulation run = new Simulation(numberOfPeople, infectRate, shelterInPlaceRate, deathRate, numberRooms);
		run.setUp();
		run.runLoop();
		System.out.println("\n\n");
		double percentUninfected = totalPercentUninfected / numSimulations * 100,
			percentDead = totalPercentDead / numSimulations * 100,
			percentCured = totalPercentCured / numSimulations * 100, percentInfected = percentCured + percentDead;
		StdDraw.clear(StdDraw.LIGHT_GRAY);
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.text(0, 8, "STATISTICS");
		StdDraw.textLeft(-9, 5, String.format("Average percent uninfected: %.1f%%", percentUninfected));
		StdDraw.setPenColor(StdDraw.RED);
		StdDraw.textLeft(-9, 2, String.format("Average percent infected: %.1f%%", percentInfected));
		StdDraw.setPenColor(StdDraw.BOOK_BLUE);
		StdDraw.textLeft(-7, -1, String.format("Average percent cured: %.1f%%", percentCured));
		StdDraw.setPenColor(StdDraw.YELLOW);
		StdDraw.textLeft(-7, -4, String.format("Average percent dead: %.1f%%", percentDead));
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.setPenRadius(.008);
		StdDraw.line(-8, 1.5, -8, -4);
		StdDraw.line(-8, -1, -7.25, -1);
		StdDraw.line(-8, -4, -7.25, -4);
		StdDraw.show();
	}

	/**
	 * Sets up and runs the simulation
	 */
	public void start()
	{
		setUp();
		runLoop();
	}

	/**
	 * Calculates the percent of people uninfected, cured,
	 * dead, and infected
	 */
	public void calculateRatios()
	{
		int[] values = new int[3];
		for (Person curr : people)
			values[curr.getViralState()]++;
		double total = values[0] + values[1] + values[2] + dead.size();
		percentHealthy.add(values[0] / total);
		percentInfected.add(values[1] / total);
		percentCured.add(values[2] / total);
		percentDead.add(dead.size() / total);
	}

	/**
	 * Sets up the window to show the simulation
	 */
	public void setUp ()
	{
		StdDraw.setCanvasSize(600,600);
		StdDraw.setXscale(-10.0, 10.0);
		StdDraw.setYscale(-10.0, 10.0);
		StdDraw.enableDoubleBuffering();
		StdDraw.setFont(new Font("SansSerif", Font.BOLD, 30));
	}

	/**
	 * Runs an infinite loop to run the simulation
	 */
	public void runLoop ()
	{
		while (true)
		{
			StdDraw.clear(StdDraw.LIGHT_GRAY);

			calculateRatios();
			int slices = 1000;
			boolean allowRestart = percentInfected.get(percentInfected.size() - 1) == 0.0;
			if (percentCured.size() > slices)
			{
				percentCured.remove(0);
				percentHealthy.remove(0);
				percentInfected.remove(0);
				percentDead.remove(0);
			}
			for (int i = percentHealthy.size() - 1, count = 0; i >= 0 && i >= percentHealthy.size() - slices; i--, count++)
			{
				double y = 10.0, centerX = 10 - 20 * count / (double)slices - 1 / (double)slices / 2.0;
				y = drawGraphSection(percentCured.get(i) * 20, new Color(170, 250, 200), centerX, y, slices);
				y -= percentHealthy.get(i) * 20;
				y = drawGraphSection(percentInfected.get(i) * 20, new Color(250, 170, 170), centerX, y, slices);
				drawGraphSection(percentDead.get(i) * 20, new Color(245, 236, 176), centerX, y, slices);
			}

			for (int i = 0; i < people.size(); i++)
			{
				Person curr = people.get(i);
				for (int j = i + 1; j < people.size(); j++)
					curr.checkCollision(people.get(j));
				checkWallCollisions(curr);
				curr.updateViralState();
				if (curr.getViralState() == 3)
					dead.add(people.remove(i--));
				curr.changePosition();
			}
			for (Wall curr : walls)
				curr.draw();
			for (Person curr : dead)
				curr.draw();
			for (Person curr : people)
				curr.draw();
			
			if (allowRestart)
			{
				StdDraw.setPenColor(new Color(160, 255, 160));
				StdDraw.filledRectangle(-5.0, 0.0, 2.0, 1.0);
				StdDraw.setPenColor(new Color(40, 130, 40));
				StdDraw.text(-5.0, 0.0, "Restart");
				StdDraw.setPenColor(new Color(255, 160, 160));
				StdDraw.filledRectangle(5.0, 0.0, 2.0, 1.0);
				StdDraw.setPenColor(new Color(130, 40, 40));
				StdDraw.text(5.0, 0.0, "End");
			}
			if (StdDraw.isMousePressed() && allowRestart)
			{
				double x = StdDraw.mouseX(), y = StdDraw.mouseY();
				boolean restart = x >= -7 && x <= -3 && y >= -1 && y <= 1, end = x >= 3 && x <= 7 && y >= -1 && y <= 1;
				if (restart)
					new Simulation(numberOfPeople, infectRate, shelterInPlaceRate, deathRate, numRooms).runLoop();
				if (restart || end)
				{
					numSimulations++;
					totalPercentCured += percentCured.get(percentCured.size() - 1);
					totalPercentDead += percentDead.get(percentDead.size() - 1);
					totalPercentUninfected += percentHealthy.get(percentHealthy.size() - 1);
					return;
				}
			}
			
			StdDraw.show();
			StdDraw.pause(20);
		}
	}

	/**
	 * Checks if the given person collides with any
	 * walls in the simulation
	 * @param p the person to check
	 */
	public void checkWallCollisions(Person p)
	{
		for (Wall currWall : walls)
			if (currWall.checkCollision(p))
				return;
	}

	/**
	 * Draws the given section of the background graph
	 * @param amount the height of the current bar in the graph
	 * @param fill the color to fill the bar
	 * @param centerX the center x coordinate of the bar
	 * @param topY the top y coordinate of the bar
	 * @param slices the number of slices shown (to determine the width of the bar)
	 * @return the y coordinate at the bottom of the bar
	 */
	public double drawGraphSection(double amount, Color fill, double centerX, double topY, int slices)
	{
		if (amount == 0.0)
			return topY;
		StdDraw.setPenColor(fill);
		StdDraw.filledRectangle(centerX, topY - amount / 2, 20 / (double)slices / 2.0, amount / 2);
		return topY - amount;
	}
}