import java.awt.Color;
import java.util.Random;

public class Particle {
    public static final int DEFAULT_MAX_VELOCITY = 3;
    public static final int DEFAULT_RADIUS = 10;
    public static final double DEFAULT_MASS = 250;
    public static final Color DEFAULT_COLOR = Draw.RED;
    
    private int x, y;
    private int vx, vy;
    private int radius;
    private double mass;
    private Color color;
    private int count;

    private static final Random rand = new Random();

    public Particle(int frameWidth, int frameHeight) {
        x = rand.nextInt(frameWidth);
        y = rand.nextInt(frameHeight);
        vx = rand.nextInt(DEFAULT_MAX_VELOCITY) + -5;
        vy = rand.nextInt(DEFAULT_MAX_VELOCITY) + -5;
        radius = DEFAULT_RADIUS;
        mass = DEFAULT_MASS;
        color = DEFAULT_COLOR;
        count = 0;
    }

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

    public void draw() {
        Draw.setPenColor(color);
        Draw.fillCircle(x, y, radius);
    }

    public int count() {
        return count;
    }

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
            // if (drdr < sigma*sigma) StdOut.println("overlapping particles");
            if (d < 0) return Double.POSITIVE_INFINITY;
            return -(dvdr + Math.sqrt(d)) / dvdv;
        }

        return Double.POSITIVE_INFINITY;
    }

    public double timeToHitVerticalWall(int frameWidth) {
        double time = Double.POSITIVE_INFINITY;
        if (vx > 0)
            time = (frameWidth - radius - x) / (double)vx;
        else if (vx < 0)
            time = -(x - radius) / (double)vx;

        return time;
    }

    public double timeToHitHorizontalWall(int frameHeight) {
        double time = Double.POSITIVE_INFINITY;
        if (vy > 0)
            time = (frameHeight - radius - y) / (double)vy;
        else if (vy < 0)
            time = -(y - radius) / (double)vy;

        return time;
    }

    public void move(double time) {
        x += vx * time;
        y += vy * time;
    }

    public void bounceOffVerticalWall() {
        vx *= -1;
        count++;
    }

    public void bounceOffHorizontalWall() {
        vy *= -1;
        count++;
    }

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