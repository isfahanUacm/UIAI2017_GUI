import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class LogSaver {

    private JProgressBar pBar;
    private JFrame jf;

    private void init() {
        pBar = new JProgressBar();
        jf = new JFrame("Saving...");
        jf.add(pBar);
        jf.pack();
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setResizable(false);
        jf.setVisible(true);
    }

    boolean saveZip(ArrayList<Image> frames, String fileName, GamePanel parent) {
        boolean result = false;
        JFileChooser jfc = new JFileChooser(LogSaver.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        jfc.setSelectedFile(new File(fileName + ".zip"));
        if (jfc.showSaveDialog(parent) != JFileChooser.CANCEL_OPTION) {
            init();
            String path = jfc.getSelectedFile().getPath();
            if (!path.endsWith(".zip"))
                path += ".zip";
            try {
                FileOutputStream fout = new FileOutputStream(path);
                ZipOutputStream zout = new ZipOutputStream(fout);
                for (int i = 0; i < frames.size(); i++) {
                    String entryName = "frame_" + String.format("%03d", i + 1) + ".png";
                    ZipEntry ze = new ZipEntry(entryName);
                    zout.putNextEntry(ze);
                    BufferedImage bImg = new BufferedImage(frames.get(i).getWidth(parent), frames.get(i).getHeight(parent), BufferedImage.TYPE_INT_ARGB);
                    Graphics g = bImg.createGraphics();
                    g.drawImage(frames.get(i), 0, 0, parent);
                    ImageIO.write(bImg, "png", zout);
                    zout.closeEntry();
                    pBar.setValue(((i + 1) * 100) / frames.size());
                }
                zout.close();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            jf.dispose();
        }
        return result;
    }

}
