import java.awt.*;

final class Consts {

    static Color background = Color.decode("#a0a0a0");
    static Color boardBackground = Color.decode("0xDCB579");
    static Color topbarBackground = Color.DARK_GRAY;
    static Color topbarForeground = Color.WHITE;
    static Color emptyCell = Color.WHITE;
    static Color popCell = Color.BLACK;
    static Color outline = Color.BLACK;
    static Color rndMove = Color.decode("#ff2121");
    static Color lineMove = Color.decode("#2da518");
    static Color paused = Color.decode("#00ba7f");
    static Color draw = Color.DARK_GRAY;

    static Color player1Checker = Color.decode("#00aad1");
    static Color player1Topbar = Color.WHITE;
    static Color player1Put = Color.decode("#1e80a0");
    static Color player1Highlight = Color.decode("#2193bc");
    static Color player1Line = Color.decode("#89e9ff");

    static Color player2Checker = Color.decode("#e20000");
    static Color player2Topbar = Color.WHITE;
    static Color player2Highlight = Color.decode("#b52020");
    static Color player2Put = Color.decode("#a51521");
    static Color player2Line = Color.decode("#ff8989");

    static int cycleSleepTime = 750;

    // assign in Main
    static Image pop;

}
