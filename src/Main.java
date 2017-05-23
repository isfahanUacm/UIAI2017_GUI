import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class Main {

    private static Board board;
    private static Topbar topbar;
    private static Queue<Cycle> cycles;
    private static String winner, player1name, player2name, title;
    private static JFrame window;
    private static File f;
    private static volatile boolean isPaused;
    private static boolean toLog;

    public static void main(String[] args) {
        toLog = true;
        isPaused = false;
        if (args.length > 3)
            isPaused = args[3].toLowerCase().equals("true");
        if (args.length > 2)
            toLog = args[2].toLowerCase().equals("true");
        if (args.length > 1)
            Consts.cycleSleepTime = Integer.parseInt(args[1]);
        if (args.length > 0) {
            f = new File(args[0]);
            if (!f.exists())
                f = null;
        }
        java.awt.EventQueue.invokeLater(Main::init);
    }

    private static void init() {
        GamePanel gamePanel = new GamePanel();

        window = new JFrame();

        window.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_P)
                    isPaused = !isPaused;
            }
        });
        window.setFocusable(true);

        try {
            Consts.pop = ImageIO.read(Main.class.getResource("img_pop.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(window, "Can't open images", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        window.setJMenuBar(menu());

        window.getContentPane().add(gamePanel);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
        int boardSize = Math.min(window.getWidth(), window.getHeight());
        int boardX = window.getWidth() / 2 - boardSize / 2;
        int boardY = window.getHeight() - 14 * boardSize / 15;
        board = new Board(gamePanel, boardX, boardY, boardSize);
        topbar = new Topbar(gamePanel, boardSize / 20);
        gamePanel.init(topbar, board);
        loadGame();
        window.setTitle("UIAI2017: " + title);

        Thread game = new Thread(() -> {
            int p1c = 12, p2c = 12;
            ArrayList<Image> log = new ArrayList<>();
            int logFrameWidth = gamePanel.getWidth(), logFrameHeight = gamePanel.getHeight();
            while (cycles.size() > 0) {
                if (!isPaused) {
                    try {
                        Thread.sleep(Consts.cycleSleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Cycle current = cycles.remove();
                    boolean put = (current.getPlayerTurn() == 0 && current.getPlayer1Checkers() < p1c)
                            || (current.getPlayerTurn() == 1 && current.getPlayer2Checkers() < p2c);
                    board.update(current.getBoard(), put);
                    topbar.setLeftString(player1name + ": " + current.getPlayer1Checkers());
                    topbar.setRightString(player2name + ": " + current.getPlayer2Checkers());
                    String mid = "Cycle #" + current.getCycleNo();
                    if (isPaused) {
                        mid = "GAME PAUSED";
                        topbar.setBackground(Consts.paused);
                    } else if (current.getType() == CycleType.DOOZ || current.getType() == CycleType.BOTH)
                        topbar.setBackground(Consts.lineMove);
                    else if (current.getType() == CycleType.RANDOM)
                        topbar.setBackground(Consts.rndMove);
                    else
                        topbar.setBackground(Consts.topbarBackground);
                    topbar.setMiddleString(mid);
                    topbar.setPlayerTurn(current.getPlayerTurn());

                    if (toLog)
                        log.add(gamePanel.getScreen(logFrameWidth, logFrameHeight));

                    p1c = current.getPlayer1Checkers();
                    p2c = current.getPlayer2Checkers();
                    gamePanel.repaint();
                    System.gc();
                }
            }
            if (winner.equals("DRAW"))
                topbar.setBackground(Consts.draw);
            else if (winner.equals("WINNER: " + player1name))
                topbar.setBackground(Consts.player1Highlight);
            else if (winner.equals("WINNER: " + player2name))
                topbar.setBackground(Consts.player2Highlight);
            topbar.setMiddleString(winner);
            gamePanel.repaint();

            boolean logResult = false;
            if (toLog) {
                LogSaver ls = new LogSaver();
                logResult = ls.saveZip(log, title, gamePanel);
            }
            if (logResult)
                JOptionPane.showMessageDialog(window, "Game images saved!", "Log saved"
                        , JOptionPane.INFORMATION_MESSAGE);

        });
        game.start();
    }

    private static void loadGame() {
        cycles = new LinkedList<>();
        if (f == null)
            f = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "\\game.txt");
        if (!f.exists()) {
            JOptionPane.showMessageDialog(window, "Can't open game file", "Error", JOptionPane.ERROR_MESSAGE);
            JFileChooser jf = new JFileChooser(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            if (jf.showOpenDialog(window) != JFileChooser.CANCEL_OPTION)
                f = jf.getSelectedFile();
            else
                System.exit(0);
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            ArrayList<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null)
                lines.add(line);

            line = lines.get(0);
            lines.remove(0);
            String[] words = line.split(" +");
            player1name = words[1];
            player2name = words[2];
            title = words[3];

            if (lines.get(lines.size() - 1).equals("draw"))
                winner = "DRAW";
            else
                winner = "WINNER: " + (lines.get(lines.size() - 1).split(" ")[1].equals("0") ? player1name : player2name);
            lines.remove(lines.size() - 1);

            for (String ln : lines) {
                String[] parts = ln.split(" ");
                String[] checkerNo = parts[3].split(",");
                CycleType type = CycleType.NORMAL;
                if (parts.length == 5 && parts[4].equals("R"))
                    type = CycleType.RANDOM;
                else if (parts.length == 5 && parts[4].equals("D"))
                    type = CycleType.DOOZ;
                else if (parts.length == 5 && parts[4].equals("RD"))
                    type = CycleType.BOTH;
                cycles.add(new Cycle(Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]),
                        parts[2],
                        Integer.parseInt(checkerNo[0]),
                        Integer.parseInt(checkerNo[1]), type));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JMenuBar menu() {
        JMenuBar mBar = new JMenuBar();
        JMenu mnActions = new JMenu("Actions");
        JMenu mnSpeed = new JMenu("Game Speed");
        JMenu mnAbout = new JMenu("About");
        JCheckBoxMenuItem miActionPause = new JCheckBoxMenuItem("Pause Game", isPaused);
        JCheckBoxMenuItem miActionLog = new JCheckBoxMenuItem("Log game", toLog);
        JMenuItem miActionExit = new JMenuItem("Close");
        JCheckBoxMenuItem miSpeedSlow = new JCheckBoxMenuItem("Slow", Consts.cycleSleepTime == 1000);
        JCheckBoxMenuItem miSpeedNormal = new JCheckBoxMenuItem("Normal", Consts.cycleSleepTime == 750);
        JCheckBoxMenuItem miSpeedFast = new JCheckBoxMenuItem("Fast", Consts.cycleSleepTime == 500);
        JCheckBoxMenuItem miSpeedInstant = new JCheckBoxMenuItem("Instant", Consts.cycleSleepTime == 0);
        JMenuItem miAbout = new JMenuItem("About");

        mnActions.add(miActionPause);
        mnActions.add(miActionLog);
        mnActions.add(miActionExit);
        mnSpeed.add(miSpeedSlow);
        mnSpeed.add(miSpeedNormal);
        mnSpeed.add(miSpeedFast);
        mnSpeed.add(miSpeedInstant);
        mnAbout.add(miAbout);
        mBar.add(mnActions);
        mBar.add(mnSpeed);
        mBar.add(mnAbout);

        miActionPause.addActionListener((ActionEvent e) -> {
            isPaused = !isPaused;
            miActionPause.setState(isPaused);
        });

        miActionLog.addActionListener((ActionEvent e) -> {
            toLog = !toLog;
            miActionLog.setState(toLog);
        });

        miActionExit.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });

        miSpeedSlow.addActionListener((ActionEvent e) -> {
            Consts.cycleSleepTime = 1000;
            miSpeedSlow.setState(true);
            miSpeedNormal.setState(false);
            miSpeedFast.setState(false);
            miSpeedInstant.setState(false);
        });

        miSpeedNormal.addActionListener((ActionEvent e) -> {
            Consts.cycleSleepTime = 750;
            miSpeedSlow.setState(false);
            miSpeedNormal.setState(true);
            miSpeedFast.setState(false);
            miSpeedInstant.setState(false);
        });

        miSpeedFast.addActionListener((ActionEvent e) -> {
            Consts.cycleSleepTime = 500;
            miSpeedSlow.setState(false);
            miSpeedNormal.setState(false);
            miSpeedFast.setState(true);
            miSpeedInstant.setState(false);
        });

        miSpeedInstant.addActionListener((ActionEvent e) -> {
            Consts.cycleSleepTime = 0;
            miSpeedSlow.setState(false);
            miSpeedNormal.setState(false);
            miSpeedFast.setState(false);
            miSpeedInstant.setState(true);
        });

        miAbout.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(window,
                    "University of Isfahan AI Challenge 2017\n" +
                            "Server GUI v2", "About",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        return mBar;
    }

}
