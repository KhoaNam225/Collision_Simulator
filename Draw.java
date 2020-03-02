/**
 *  Author:     Khoa Nam Pham
 *  Date modified:  03/02/2020
 *  Purpose:    This class provides useful methods to support 2D graphics 
 *              operations such as drawing different shapes, set drawing pen color, etc.
 *              This class used Java Swing library to support some operations related to 
 *              frame.   
 */
import java.awt.Color;
import java.awt.Graphics2D;

// Shape classes
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

// Double buffering
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;


public class Draw {
    // All the supported colors
    public static final Color RED = Color.RED;
    public static final Color BLUE = Color.BLUE;
    public static final Color GREEN = Color.GREEN;
    public static final Color YELLOW = Color.YELLOW;
    public static final Color WHITE = Color.WHITE;
    public static final Color BLACK = Color.BLACK;
    public static final Color GREY = Color.GRAY;
    public static final Color ORANGE = Color.ORANGE;

    // Default canvas size
    public static final int DEFAULT_SIZE = 512;

    // Default pen and background color
    private static final Color DEFAULT_CLEAR_COLOR = Color.WHITE;
    private static final Color DEFAULT_PEN_COLOR = Color.BLACK;

    private static int frameWidth = DEFAULT_SIZE, frameHeight = DEFAULT_SIZE;  // Current canvas size
    // Using onScreen and offScreen to enable double buffering drawing mode
    // Useful when there are a lot of objects that need to be drawn on the screen
    private static BufferedImage onScreenImage, offScreenImage;  
    private static Graphics2D onScreen, offScreen;
    private static JFrame frame;
    private static Color penColor = DEFAULT_PEN_COLOR;

    private static boolean doubleBuffering = false;

    // Singleton, can only create on instance of the class
    public Draw() {}

    static {
        init();
    }

    /**
     *  Initalize all the neccessary objects to prepare for drawing
     */
    private static void init() {
        if (frame != null) frame.setVisible(false);
        frame = new JFrame();

        // Create double buffering image to provide smooth animation when there are a lot of 
        // objects need updating on the canvas
        onScreenImage = new BufferedImage(DEFAULT_SIZE, DEFAULT_SIZE, BufferedImage.TYPE_INT_ARGB);
        offScreenImage = new BufferedImage(DEFAULT_SIZE, DEFAULT_SIZE, BufferedImage.TYPE_INT_ARGB);
        onScreen = onScreenImage.createGraphics();
        offScreen = offScreenImage.createGraphics();

        // Make the screen blank
        offScreen.setColor(DEFAULT_CLEAR_COLOR);
        offScreen.fillRect(0, 0, frameWidth, frameHeight);

        clear(DEFAULT_CLEAR_COLOR);

        // Drawing area
        ImageIcon icon = new ImageIcon(onScreenImage);
        JLabel draw = new JLabel(icon);

        // Frame stuffs
        frame.setContentPane(draw);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Drawing");
        frame.pack();
        frame.setVisible(true);
    }

    /**
     *  Clear everything on the canvas and set the background to the default background
     *  color.
     */
    public static void clear() {
        clear(DEFAULT_CLEAR_COLOR);
    }

    /**
     * Clear everything on the canvas and set the background to the given color.
     *
     * @param color  The color of the background after clearing evrything
     * @throws IllegalArgumentException if the given Color is null
     */
    private static void clear(Color color) {
        if (color == null)
            throw new IllegalArgumentException("Color is null");

        offScreen.setColor(color);
        offScreen.fillRect(0, 0, frameWidth, frameHeight);
        offScreen.setColor(penColor);
    }

    /**
     *  Set the paint color to the DEFAULT_PAINT_COLOR
     */
    public static void setPenColor() {
        setPenColor(DEFAULT_PEN_COLOR);
    }

    /**
     *  Set the paint color to the given Color
     * 
     *  @param color The new color of the pen used to paint objects
     *  @throws IllegalArgumentException when the given color is null
     */
    public static void setPenColor(Color color) {
        if (color == null)
            throw new IllegalArgumentException("Color is null when calling setPencolor()");

        penColor = color;
        offScreen.setColor(color);
    }

    /**
     *  Set the paint color to the given color with its RGB code. 
     * @param red The value for red 
     * @param green The value for green
     * @param blue The value for blue
     * 
     * @throws IllegalArgumentException when any of the three requried values less than 0
     */
    public static void setPenColor(int red, int green, int blue) {
        if (red < 0) throw new IllegalArgumentException("Red must not negative.");
        if (green < 0) throw new IllegalArgumentException("Green must not negative.");
        if (blue < 0) throw new IllegalArgumentException("Blue must not negative.");

        setPenColor(new Color(red, green, blue));
    }

    /**
     * Set the title for the drawing canvas
     * @param label The title to set to the canvas window
     * @throws IllegalArgumentException when the label is null
     */
    public static void setLabel(String label) {
        if (label == null) throw new IllegalArgumentException("Calling setLabel() with null label.");
        
        frame.setTitle(label);
    }

    /**
     * Draw a hollow circle on the canvas given its center (x and y coordinates) and radius
     * @param x The x-coordinate of the center
     * @param y The y-coordinate of the center
     * @param radius The radius of the circle
     * 
     * @throws IllegalArgumentException when the raidus is negative
     */
    public static void circle(int x, int y, int radius) {
        if (radius < 0)
            throw new IllegalArgumentException("Invalid radius, radius cannot be negative");

        offScreen.draw(new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius));
        draw();
    }

    /**
     * Draw a solid circle on the canvas given its center (x and y coordinate) and radius
     * @param x The x-coordinate of the center
     * @param y The y-coordinate of the center
     * @param radius The radius of the circle
     * 
     * @throws IllegalArgumentException when the raidus is negative
     */
    public static void fillCircle(int x, int y, int radius) {
        if (radius < 0)
            throw new IllegalArgumentException("Invalid radius, radius cannot be negative");

        offScreen.fill(new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius));
        draw();
    }

    /**
     * Draw a hollow rectangle given the coordinate of the upper left corner and the width and height.
     *  
     * @param x The x-coordinate of the upper-left corner
     * @param y The y-coordinate of the upper-left corner
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * 
     * @throws IllegalArgumentException when the width and height is negative
     */
    public static void rectangle(int x, int y, int width, int height) {
        if (width < 0)
            throw new IllegalArgumentException("Width cannot be negative");
        if (height < 0)
            throw new IllegalArgumentException("Height cannot be negative");

        offScreen.draw(new Rectangle2D.Double(x, y, width, height));
        draw();
    }

    /**
     * Draw a solid rectangle given the coordinate of the upper left corner and the width and height.
     *  
     * @param x The x-coordinate of the upper-left corner
     * @param y The y-coordinate of the upper-left corner
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * 
     * @throws IllegalArgumentException when the width and height is negative
     */
    public static void fillRectangle(int x, int y, int width, int height) {
        if (width < 0)
            throw new IllegalArgumentException("Width cannot be negative");
        if (height < 0)
            throw new IllegalArgumentException("Height cannot be negative");

        offScreen.fill(new Rectangle2D.Double(x, y, width, height));
        draw();
    }

    /**
     *  Show all the objects on the canvas (if not in double buffering mode).
     */
    public static void draw() {
        if (!doubleBuffering) show();
    }

    /**
     *  Draw all the objects on the canvas.
     */
    public static void show() {
        onScreen.drawImage(offScreenImage, 0, 0, null);
        frame.repaint();
    }

    /**
     * Pause the current program for the given amount of time in milliseconds.
     * @param time The amount of time the program needs to pause in milliseconds.
     */
    public static void pause(int time) {
        try {
            Thread.sleep(time);
        }
        catch (InterruptedException exception) {
            System.out.println("Sleep error");
        }
    }

    /**
     * Enable double buffering, this is useful when there are many objects that need to
     * be drawn to allow smooth animation.
     */
    public static void enableDoubleBuffering() {
        doubleBuffering = true;
    }

    /**
     * Turn off double buffering mode, everything will be draw immediately after the call to
     * draw() is executed.
     */
    public static void disableDoubleBuffering() {
        doubleBuffering = false;
    }

    // Test client
    public static void main(String[] args) {
        Draw.enableDoubleBuffering();
        // Draw 2 bouncing balls on the canvas
        int x = 50;
        int y = 100;
        int vx = 5;
        int vy = -1;

        int x2 = 100;
        int y2 = 200;
        int vx2 = 1;
        int vy2 = 5;

        Draw.setPenColor(44, 149, 219);
        Draw.setLabel("Testing");

        while (true) {
            x += vx;
            y += vy;
            x2 += vx2;
            y2 += vy2;

            if (x >= DEFAULT_SIZE - 20 || x <= 20) vx *= -1;
            if (y >= DEFAULT_SIZE - 20 || y <= 20) vy *= -1;

            if (x2 >= DEFAULT_SIZE - 20 || x2 <= 20) vx2 *= -1;
            if (y2 >= DEFAULT_SIZE - 20 || y2 <= 20) vy2 *= -1;

            Draw.fillCircle(x, y, 20);
            Draw.fillCircle(x2, y2, 20);
            Draw.show();
            Draw.pause(20);
            Draw.clear();
        }
    }
}