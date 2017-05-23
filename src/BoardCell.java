import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import javax.swing.*;
import java.awt.*;

enum State {
    PUT, NORMAL, POP, MOVE_SRC, MOVE_DEST, LINED
}

class BoardCell {

    private int x, y, size, playerNum;
    private State state;

    BoardCell(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        playerNum = 0;
    }

    BoardCell(int playerNum) {
        x = 0;
        y = 0;
        size = 0;
        this.playerNum = playerNum;
    }

    void draw(Graphics g, JPanel parent) {
        Utils.drawCirlce(g, x, y, size / 2, getColor(), true);
        if (state == State.POP)
            g.drawImage(Consts.pop, x - size / 2, y - size / 2, size, size, parent);
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    int getPlayerNum() {
        return playerNum;
    }

    void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }

    int getSize() {
        return size;
    }

    void update(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    Color getColor() {
        if (playerNum == 1 &&
                (state == State.NORMAL || state == State.POP ||
                        state == State.MOVE_SRC || state == State.MOVE_DEST))
            return Consts.player1Checker;
        else if (playerNum == 1 && state == State.PUT)
            return Consts.player1Put;
        else if (playerNum == 2 &&
                (state == State.NORMAL || state == State.POP ||
                        state == State.MOVE_SRC || state == State.MOVE_DEST))
            return Consts.player2Checker;
        else if (playerNum == 2 && state == State.PUT)
            return Consts.player2Put;
        else if (state == State.POP)
            return Consts.popCell;
        else if (playerNum == 1 && state == State.LINED)
            return Consts.player1Line;
        else if (playerNum == 2 && state == State.LINED)
            return Consts.player2Line;
        else
            return Consts.emptyCell;
    }

    void setState(State state) {
        this.state = state;
    }

    State getState() {
        return state;
    }

}
