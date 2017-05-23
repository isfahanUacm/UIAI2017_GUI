import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private final Dimension PREF_SIZE = new Dimension(600, 610);

    private Topbar topbar;
    private Board board;

    void paintScreen(Graphics g) {
        Image offImg = getScreen(getWidth(), getHeight());
        g.drawImage(offImg, 0, 0, this);
    }

    Image getScreen(int width, int height) {
        Image offImg = createImage(width, height);
        Graphics offG = offImg.getGraphics();
        draw(offG);
        return offImg;
    }

    @Override
    protected void paintComponent(Graphics g) {
        paintScreen(g);
    }

    private void draw(Graphics g) {
        g.setColor(Consts.background);
        g.fillRect(0, 0, getWidth(), getHeight());
        int boardSize = Math.min(getWidth(), getHeight());
        int boardX = getWidth() / 2 - boardSize / 2;
        int boardY = getHeight() / 2 - boardSize / 2 + topbar.getHeight() / 2;
        topbar.paint(g);
        board.draw(g, boardX, boardY, boardSize);
    }

    void init(Topbar t, Board b) {
        topbar = t;
        board = b;
    }

    @Override
    public Dimension getPreferredSize() {
        return PREF_SIZE;
    }
}
