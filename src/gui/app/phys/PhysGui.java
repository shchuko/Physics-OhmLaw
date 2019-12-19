package gui.app.phys;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PhysGui extends JFrame {
    private JTextField epfField;
    private JTextField intResistanceField;
    private JTextField extResistanceField;
    private JButton STARTButton;
    private JTextField currentExtResistance;
    private JTextField pFullField;
    private JTextField pLossField;
    private JTextField pExtField;
    private JSlider resistanceSlider;
    private JLabel statusLabel;
    private JLabel imageLabel;
    private JPanel mainPanel;
    private JButton stopButton;
    private JTextField currentField;
    private JTextField uExtRField;
    private JButton captureButton;
    private JTextField uIntRField;

    /**
     * Creates GUI of interactive Ohm's law simulator control panel
     */
    public PhysGui() {
        loadImage();
        setMinimumSize(new Dimension(900, 442));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        pack();
        centring();
        System.out.println(getSize());
        setVisible(true);
    }

    /**
     * Loads image with circuit from /imp/circuit.png
     */
    private void loadImage() {
        BufferedImage imageBuf;
        try {
            imageBuf = ImageIO.read(new File("img/circuit.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Can't open circuit image", "File Reading Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Image image = new ImageIcon(imageBuf).getImage();
        int width = 350;
        int height = 200;
        image = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(image));
    }

    /**
     * Places frame to the screen center
     */
    private void centring() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - getWidth() / 2,
                dim.height / 2 - getHeight() / 2);
    }
}
