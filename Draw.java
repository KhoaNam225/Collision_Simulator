import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;

import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;


public class Draw {
    public static final Color RED = Color.RED;
    public static final Color BLUE = Color.BLUE;
    public static final Color GREEN = Color.GREEN;
    public static final Color YELLOW = Color.YELLOW;
    public static final Color WHITE = Color.WHITE;
    public static final Color BLACK = Color.BLACK;
    public static final Color GREY = Color.GRAY;
    public static final Color ORANGE = Color.ORANGE;

    public static final int DEFAULT_SIZE = 512;

    private static final Color DEFAULT_CLEAR_COLOR = Color.WHITE;
    private static final Color DEFAULT_PEN_COLOR = Color.BLACK;

    private static int frameWidth = DEFAULT_SIZE, frameHeight = DEFAULT_SIZE;
    private static BufferedImage onScreenImage, offScreenImage;
    private static Graphics2D onScreen, offScreen;
    private static JFrame frame;
    private static Color penColor = DEFAULT_PEN_COLOR;

    private static boolean doubleBuffering = false;

    public Draw() {}

    static {
        init();
    }

    private static void init() {
        if (frame != null) frame.setVisible(false);
        frame = new JFrame();
        onScreenImage = new BufferedImage(DEFAULT_SIZE, DEFAULT_SIZE, BufferedImage.TYPE_INT_ARGB);
        offScreenImage = new BufferedImage(DEFAULT_SIZE, DEFAULT_SIZE, BufferedImage.TYPE_INT_ARGB);
        onScreen = onScreenImage.createGraphics();
        offScreen = offScreenImage.createGraphics();

        offScreen.setColor(DEFAULT_CLEAR_COLOR);
        offScreen.fillRect(0, 0, frameWidth, frameHeight);

        clear(DEFAULT_CLEAR_COLOR);

        ImageIcon icon = new ImageIcon(onScreenImage);
        JLabel draw = new JLabel(icon);

        frame.setContentPane(draw);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Drawing");
        frame.pack();
        frame.setVisible(true);
    }

    public static void clear() {
        clear(DEFAULT_CLEAR_COLOR);
    }

    private static void clear(Color color) {
        if (color == null)
            throw new IllegalArgumentException("Color is null");

        offScreen.setColor(color);
        offScreen.fillRect(0, 0, frameWidth, frameHeight);
        offScreen.setColor(penColor);
    }

    public static void setPenColor() {
        setPenColor(DEFAULT_PEN_COLOR);
    }

    public static void setPenColor(Color color) {
        if (color == null)
            throw new IllegalArgumentException("Color is null when calling setPencolor()");

        penColor = color;
        offScreen.setColor(color);
    }

    public static void setPenColor(int red, int green, int blue) {
        if (red < 0) throw new IllegalArgumentException("Red must not negative.");
        if (green < 0) throw new IllegalArgumentException("Green must not negative.");
        if (blue < 0) throw new IllegalArgumentException("Blue must not negative.");

        setPenColor(new Color(red, green, blue));
    }

    public static void setLabel(String label) {
        if (label == null) throw new IllegalArgumentException("Calling setLabel() with null label.");
        
        frame.setTitle(label);
    }

    public static void circle(int x, int y, int radius) {
        offScreen.draw(new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius));
        draw();
    }

    public static void fillCircle(int x, int y, int radius) {
        offScreen.fill(new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius));
        draw();
    }

    public static void rectangle(int x, int y, int width, int height) {
        offScreen.draw(new Rectangle2D.Double(x, y, width, height));
        draw();
    }

    public static void fillRectangle(int x, int y, int width, int height) {
        offScreen.fill(new Rectangle2D.Double(x, y, width, height));
        draw();
    }

    public static void draw() {
        if (!doubleBuffering) show();
    }

    public static void show() {
        onScreen.drawImage(offScreenImage, 0, 0, null);
        frame.repaint();
    }

    public static void pause(int time) {
        try {
            Thread.sleep(time);
        }
        catch (InterruptedException exception) {
            System.out.println("Sleep error");
        }
    }

    public static void enableDoubleBuffering() {
        doubleBuffering = true;
    }

    public static void disableDoubleBuffering() {
        doubleBuffering = false;
    }

    public static void main(String[] args) {
        Draw.enableDoubleBuffering();
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