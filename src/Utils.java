import java.awt.*;

final class Utils {

    static void drawCirlce(Graphics g, int x, int y, int radius, Color cl, boolean outline) {
        Color beforeColor = g.getColor();
        g.setColor(cl);
        g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
        if (outline) {
            g.setColor(Color.BLACK);
            g.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);
        }
        g.setColor(beforeColor);
    }

}
