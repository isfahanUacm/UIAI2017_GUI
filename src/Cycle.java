enum CycleType {
    NORMAL, DOOZ, RANDOM, BOTH
}

class Cycle {

    private int cycleNo, playerTurn, player1Checkers, player2Checkers;
    private BoardCell[][] board;
    private CycleType type;

    Cycle(int cycleNo, int playerTurn, String boardState, int player1Checkers, int player2Checkers, CycleType type) {
        this.cycleNo = cycleNo;
        this.playerTurn = playerTurn;
        this.player1Checkers = player1Checkers;
        this.player2Checkers = player2Checkers;
        String[] cells = boardState.split(",");
        board = new BoardCell[3][8];
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 3; j++)
                if (cells[3 * i + j].equals("e"))
                    board[j][i] = new BoardCell(0);
                else if (cells[3 * i + j].equals("0"))
                    board[j][i] = new BoardCell(1);
                else if (cells[3 * i + j].equals("1"))
                    board[j][i] = new BoardCell(2);
        this.type = type;
    }

    int getCycleNo() {
        return cycleNo;
    }

    int getPlayerTurn() {
        return playerTurn;
    }

    int getPlayer1Checkers() {
        return player1Checkers;
    }

    int getPlayer2Checkers() {
        return player2Checkers;
    }

    BoardCell[][] getBoard() {
        return board;
    }

    CycleType getType() {
        return type;
    }

}
