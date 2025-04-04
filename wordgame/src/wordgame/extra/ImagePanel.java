package wordgame.extra;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private Image image;

    public ImagePanel(String resourcePath) {
        try {
            // JAR 내부 리소스를 읽기 위해 getResourceAsStream 사용
            image = ImageIO.read(getClass().getResourceAsStream(resourcePath));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
