import java.awt.*;

class Board {

    private int x, y, size;
    private BoardCell[][] cells;
    private GamePanel parent;

    Board(GamePanel parent, int x, int y, int size) {
        this.parent = parent;
        updateSize(x, y, size);
        cells = new BoardCell[3][8];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 8; j++)
                cells[i][j] = new BoardCell(0, 0, 0);
        updatePoints();
    }

    private void draw(Graphics g) {
        updatePoints();
        g.setColor(Consts.boardBackground);
        g.fillRect(x + size / 15, y + size / 15, size - 2 * size / 15, size - 2 * size / 15);
        g.setColor(Consts.outline);
        for (int i = 0; i < 3; i++) {
            int squareSize = cells[i][4].getX() - cells[i][0].getX();
            g.drawRect(cells[i][0].getX(), cells[i][0].getY(), squareSize, squareSize);
            if (i < 2)
                for (int j = 0; j < 4; j++)
                    g.drawLine(cells[i][j].getX(), cells[i][j].getY(), cells[i][j + 4].getX(), cells[i][j + 4].getY());
        }
        g.setColor(Consts.boardBackground);
        int squareSize = cells[2][4].getX() - cells[2][0].getX() - 2;
        g.fillRect(cells[2][0].getX() + 1, cells[2][0].getY() + 1, squareSize, squareSize);
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 8; j++)
                cells[i][j].draw(g, parent);
    }

    void draw(Graphics g, int boardX, int boardY, int boardSize) {
        updateSize(boardX, boardY, boardSize);
        draw(g);
    }

    private void move(int srcI, int srcJ, int destI, int destJ, Color cl) {
        int startWidth = parent.getWidth();
        int startHeight = parent.getHeight();
        int x0 = cells[srcI][srcJ].getX();
        int y0 = cells[srcI][srcJ].getY();
        int x1 = cells[destI][destJ].getX();
        int y1 = cells[destI][destJ].getY();
        int k = 30;
        int dx = (x1 - x0) / k;
        int dy = (y1 - y0) / k;
        int playerNum = this.cells[srcI][srcJ].getPlayerNum();
        this.cells[srcI][srcJ].setPlayerNum(0);
        for (int i = 0; i < k && (parent.getWidth() == startWidth && parent.getHeight() == startHeight); i++) {
            Image offImg = parent.createImage(parent.getWidth(), parent.getHeight());
            Graphics offG = offImg.getGraphics();
            parent.paintScreen(offG);
            Utils.drawCirlce(offG, x0 + dx * i, y0 + dy * i, cells[0][0].getSize() / 2, cl, true);
            parent.getGraphics().drawImage(offImg, 0, 0, parent);
            try {
                Thread.sleep(Consts.cycleSleepTime / 75);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.cells[srcI][srcJ].setPlayerNum(playerNum);
    }

    private void updatePoints() {
        int margin = size / 15;
        for (int i = 0; i < 3; i++) {
            int squareX = x + (2 * i + 1) * margin;
            int squareY = y + (2 * i + 1) * margin;
            int squareSize = size - (2 * i + 1) * 2 * margin;
            int pointSize = size / 15;
            for (int j = 0; j < 8; j++) {
                cells[i][0].update(squareX, squareY, pointSize);
                cells[i][1].update(squareX + squareSize / 2, squareY, pointSize);
                cells[i][2].update(squareX + squareSize, squareY, pointSize);
                cells[i][3].update(squareX + squareSize, squareY + squareSize / 2, pointSize);
                cells[i][4].update(squareX + squareSize, squareY + squareSize, pointSize);
                cells[i][5].update(squareX + squareSize / 2, squareY + squareSize, pointSize);
                cells[i][6].update(squareX, squareY + squareSize, pointSize);
                cells[i][7].update(squareX, squareY + squareSize / 2, pointSize);
            }
        }
    }

    private void updateSize(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    void update(BoardCell[][] cells, boolean checkerUsed) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 8; j++) {
                if (cells[i][j].getPlayerNum() != this.cells[i][j].getPlayerNum() && this.cells[i][j].getPlayerNum() == 0 && checkerUsed)
                    cells[i][j].setState(State.PUT);
                else if (cells[i][j].getPlayerNum() != this.cells[i][j].getPlayerNum() && this.cells[i][j].getPlayerNum() == 0) {
                    cells[i][j].setState(State.MOVE_DEST);
                } else if (cells[i][j].getPlayerNum() != this.cells[i][j].getPlayerNum() && cells[i][j].getPlayerNum() == 0) {
                    cells[i][j].setState(State.MOVE_SRC);
                } else
                    cells[i][j].setState(State.NORMAL);
            }
        int src[] = {-1, -1}, dest[] = {-1, -1};
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 8; j++) {
                if (cells[i][j].getState() == State.MOVE_SRC) {
                    src[0] = i;
                    src[1] = j;
                }
                if (cells[i][j].getState() == State.MOVE_DEST) {
                    dest[0] = i;
                    dest[1] = j;
                }
            }
        if (src[0] != -1 && dest[0] == -1) {
            this.cells[src[0]][src[1]].setState(State.POP);
            parent.repaint();
            try {
                Thread.sleep(Consts.cycleSleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.cells[src[0]][src[1]].setState(State.NORMAL);
        } else if (src[0] != -1 && dest[0] != -1) {
            parent.repaint();
            move(src[0], src[1], dest[0], dest[1], cells[dest[0]][dest[1]].getColor());
        }
        parent.repaint();
        checkLine(cells);
        this.cells = cells;
        updatePoints();
    }

    private boolean checkLine(BoardCell[][] cells) {
        int di = -1, dj = -1;
        boolean found = false;
        for (int i = 0; i < 3 && !found; i++)
            for (int j = 0; j < 8 && !found; j++)
                if (cells[i][j].getPlayerNum() != this.cells[i][j].getPlayerNum() && this.cells[i][j].getPlayerNum() == 0) {
                    di = i;
                    dj = j;
                    found = true;
                }
        if (!found)
            return false;
        found = false;
        BoardCell line[] = new BoardCell[3];
        for (int i = 0; i < 8 && !found; i += 2)
            if (cells[di][i].getPlayerNum() == cells[di][i + 1].getPlayerNum()
                    && cells[di][i].getPlayerNum() == cells[di][(i + 2) % 8].getPlayerNum()
                    && (i == dj || i + 1 == dj || (i + 2) % 8 == dj)) {
                found = true;
                line[0] = cells[di][i];
                line[1] = cells[di][i + 1];
                line[2] = cells[di][(i + 2) % 8];
            }
        if (!found && cells[0][dj].getPlayerNum() == cells[1][dj].getPlayerNum() && cells[1][dj].getPlayerNum() == cells[2][dj].getPlayerNum()) {
            found = true;
            line[0] = cells[0][dj];
            line[1] = cells[1][dj];
            line[2] = cells[2][dj];
        }
        if (found) {
            this.cells = cells;
            for (int i = 0; i < 3; i++) {
                try {
                    Thread.sleep(Consts.cycleSleepTime / 4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < 3; j++)
                    line[j].setState(State.LINED);
                parent.repaint();
                try {
                    Thread.sleep(Consts.cycleSleepTime / 4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < 3; j++)
                    line[j].setState(State.NORMAL);
                parent.repaint();
            }
        }
        return found;
    }

}
