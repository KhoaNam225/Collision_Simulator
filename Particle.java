/**
 *  Author:         Khoa Nam Pham
 *  Date modified:  02/03/2020
 *  Purpose:        This class represent the particles in the simulator.
 *                  Each particle will have the coordinate, velocity, radius, mass and color.
 *                  Also, each particle will keep track of the number of events that
 *                  it participated in. This is used to validate the events in the queue.
 */

import java.awt.Color;
import java.util.Random;

public class Particle {
    // All the default properties of a particle
    public static final int DEFAULT_MAX_VELOCITY = 3;
    public static final int DEFAULT_RADIUS = 10;
    public static final double DEFAULT_MASS = 250;
    public static final Color DEFAULT_COLOR = Draw.RED;
    
    private int x, y; // Coordinate
    private int vx, vy; // Velocity
    private int radius; // Radius
    private double mass;  // Mass
    private Color color; // The color
    private int count;  // The number of events already participated

    private static final Random rand = new Random();

    /**
     * Create a new Particle with a random position and velocity
     * @param frameWidth The width of the canvas
     * @param frameHeight The height of the canvas
     * @throws IllegalArgumentException when the given frameWidth or frameHeight is negative.
     */
    public Particle(int frameWidth, int frameHeight) {
        if (frameWidth < 0 || frameHeight < 0)
            throw new IllegalArgumentException("Invalid frameWidth or frameHeight");

        x = rand.nextInt(frameWidth);
        y = rand.nextInt(frameHeight);
        vx = rand.nextInt(DEFAULT_MAX_VELOCITY) + -5;
        vy = rand.nextInt(DEFAULT_MAX_VELOCITY) + -5;
        radius = DEFAULT_RADIUS;
        mass = DEFAULT_MASS;
        color = DEFAULT_COLOR;
        count = 0;
    }

    /**
     * Create a new Particle with all the given properties.
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param vx The horizontal velocity
     * @param vy The vertical velocity
     * @param radius The radius of the particle
     * @param mass The mass 
     * @param color The color
     */
    public Particle(int x, int y, int vx, int vy, int radius, double mass, Color color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;
        this.mass = mass;
        this.color = color;
        this.count = 0;
    }

    /**
     * Draw the particle on the canvas.
     */
    public void draw() {
        Draw.setPenColor(color);
        Draw.fillCircle(x, y, radius);
    }

    /**
     * Returns the number of events that the current particle has participated in.
     * @return The number of events that consists of this particle.
     */
    public int count() {
        return count;
    }

    /**
     * Calculate and return the amount of time this particle will collide with another particle.
     * @param that The other particle
     * @return The amount of time this particle will hit the given particle
     */
    public double timeToHit(Particle that) {
        if (that != this) {
            double dx  = that.x - this.x;
            double dy  = that.y - this.y;
            double dvx = that.vx - this.vx;
            double dvy = that.vy - this.vy;
            double dvdr = dx*dvx + dy*dvy;

            if (dvdr > 0) return Double.POSITIVE_INFINITY;
            double dvdv = dvx*dvx + dvy*dvy;
            if (dvdv == 0) return Double.POSITIVE_INFINITY;
            double drdr = dx*dx + dy*dy;
            double sigma = this.radius + that.radius;
            double d = (dvdr*dvdr) - dvdv * (drdr - sigma*sigma);
            if (d < 0) return Double.POSITIVE_INFINITY;
            return -(dvdr + Math.sqrt(d)) / dvdv;
        }

        return Double.POSITIVE_INFINITY;
    }

    /**
     * Calculate and return the amount of time for this particle to hit the vertical wall 
     * @param frameWidth The width of the canvas
     * @return The amount fo time this particle will hit the vertical wall.
     */
    public double timeToHitVerticalWall(int frameWidth) {
        double time = Double.POSITIVE_INFINITY;
        if (vx > 0)
            time = (frameWidth - radius - x) / (double)vx;
        else if (vx < 0)
            time = -(x - radius) / (double)vx;

        return time;
    }

    /**
     * Calculate and return the amount of time for this particle to hit the horizontal wall
     * @param frameHeight The height of the canvas
     * @return The amount of time this particle will hit the horizontal wall.
     */
    public double timeToHitHorizontalWall(int frameHeight) {
        double time = Double.POSITIVE_INFINITY;
        if (vy > 0)
            time = (frameHeight - radius - y) / (double)vy;
        else if (vy < 0)
            time = -(y - radius) / (double)vy;

        return time;
    }

    /**
     * Move the current particle over a distance that it can travel in the given amount of time.
     * @param time The amount of time that this particle will travel.
     */
    public void move(double time) {
        x += vx * time;
        y += vy * time;
    }

    /**
     * Change the velocity of the particle when it hits the vertical wall.
     */
    public void bounceOffVerticalWall() {
        vx *= -1;
        count++;
    }

    /**
     * Change the velocity of the particle when it hits the horizontal wall.
     */
    public void bounceOffHorizontalWall() {
        vy *= -1;
        count++;
    }

    /**
     * Change the velocity of the particle when it hits another particle.
     * @param that Another particle
     */
    public void bounceOff(Particle that) {
        double dx  = that.x - this.x;
        double dy  = that.y - this.y;
        double dvx = that.vx - this.vx;
        double dvy = that.vy - this.vy;
        double dvdr = dx*dvx + dy*dvy;             // dv dot dr
        double dist = this.radius + that.radius;   // distance between particle centers at collison

        // magnitude of normal force
        double magnitude = 2 * this.mass * that.mass * dvdr / ((this.mass + that.mass) * dist);

        // normal force, and in x and y directions
        double fx = magnitude * dx / dist;
        double fy = magnitude * dy / dist;

        // update velocities according to normal force
        this.vx += fx / this.mass;
        this.vy += fy / this.mass;
        that.vx -= fx / that.mass;
        that.vy -= fy / that.mass;

        // update collision counts
        this.count++;
        that.count++;
    }
}