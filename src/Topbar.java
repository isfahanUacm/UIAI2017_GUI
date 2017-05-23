import javax.swing.*;
import java.awt.*;

class Topbar {

    private JPanel parent;
    private int height;
    private String lString, mString, rString;
    private int playerTurn;
    private Color background;

    Topbar(JPanel parent, int height) {
        this.parent = parent;
        this.height = height;
        lString = "Player 1";
        mString = "UI AI";
        rString = "Player 2";
        playerTurn = 0;
    }

    void paint(Graphics g) {
        Color beforeColor = g.getColor();
        if (background == null)
            background = Consts.topbarBackground;
        g.setColor(background);
        g.fillRect(0, 0, parent.getWidth(), height);
        int x, w;

        g.setFont(new Font("Consolas", Font.BOLD, 2 * height / 3));
        x = parent.getWidth() / 100;
        w = g.getFontMetrics().stringWidth(lString);
        g.setColor(playerTurn == 0 ? Consts.player1Highlight : Consts.topbarBackground);
        g.fillRect(0, 0, w + x + 10, height);
        g.setColor(Consts.player1Topbar);
        g.drawString(lString, x, 2 * height / 3);

        g.setColor(Consts.topbarForeground);
        w = g.getFontMetrics().stringWidth(mString);
        x = parent.getWidth() / 2 - w / 2;
        g.drawString(mString, x, 2 * height / 3);

        w = g.getFontMetrics().stringWidth(rString);
        x = 99 * parent.getWidth() / 100 - w;
        g.setColor(playerTurn == 1 ? Consts.player2Highlight : Consts.topbarBackground);
        g.fillRect(x - 10, 0, parent.getWidth() - x + 10, height);
        g.setColor(Consts.player2Topbar);
        g.drawString(rString, x, 2 * height / 3);

        g.setColor(beforeColor);
    }

    void setLeftString(String s) {
        lString = s;
    }

    void setRightString(String s) {
        rString = s;
    }

    void setMiddleString(String s) {
        mString = s;
    }

    void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }

    void setBackground(Color cl) {
        background = cl;
    }

    int getHeight() {
        return height;
    }

}
