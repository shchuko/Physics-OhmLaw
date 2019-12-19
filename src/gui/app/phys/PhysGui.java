package gui.app.phys;

import ohm.low.phys.DCPowerSupplyConnection;
import ohm.low.phys.Exception.WrongPhysicsParameterException;
import ohm.low.phys.base.DCPowerSupply;
import ohm.low.phys.base.FixedResistor;
import ohm.low.phys.base.Potentiometer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PhysGui extends JFrame {
    private enum Status {STOPPED, ACTIVE};
    private Status status = Status.STOPPED;

    private static final int SLIDER_MAX = 10_000_000;
    private static final String LABEL_STOPPED = "STATUS: STOPPED";
    private static final String LABEL_ACTIVE_NORMAL = "STATUS: NORMAL";
    private static final String LABEL_ACTIVE_SHORT_CIRCUIT = "STATUS: SHORT CIRCUIT";

    private JTextField emfField;
    private JTextField intResistanceField;
    private JTextField extResistanceField;
    private JButton modeButton;
    private JTextField currentExtResistance;
    private JTextField pFullField;
    private JTextField pLossField;
    private JTextField pExtField;
    private JSlider resistanceSlider;
    private JLabel statusLabel;
    private JLabel imageLabel;
    private JPanel mainPanel;
    private JTextField currentField;
    private JTextField uExtRField;
    private JButton captureButton;
    private JTextField uIntRField;

    private Potentiometer externalResistor;
    private DCPowerSupply powerSupply;
    private DCPowerSupplyConnection circuit;

    /**
     * Creates GUI of interactive Ohm's law simulator control panel
     */
    public PhysGui() {
        super("Ohm's Law simulator for DC circuit");
        Image img = new ImageIcon("img/ico.png").getImage();
        setIconImage(img);
        setTitle("Ohm's Law simulator for DC circuit");

        loadImage();
        fillFieldsZeroes();
        updateModeButton();
        initActionHandling();
        setLabelStopped();

        setMinimumSize(new Dimension(900, 442));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        pack();
        centring();
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

    /**
     * Fills fields with zeroes
     */
    private void fillFieldsZeroes() {
        emfField.setText("0.0");
        intResistanceField.setText("0.0");
        extResistanceField.setText("0.0");
        currentExtResistance.setText("0.0");
        uExtRField.setText("0.0");
        uIntRField.setText("0.0");
        currentField.setText("0.0");
        pExtField.setText("0.0");
        pFullField.setText("0.0");
        pLossField.setText("0.0");
    }

    /**
     * Updates mode button with actual value from the status variable
     */
    private void updateModeButton() {
        if (status == Status.STOPPED) {
            modeButton.setText("START");
            modeButton.setActionCommand("startSimulate");
        } else {
            modeButton.setText("STOP");
            modeButton.setActionCommand("stopSimulate");
        }
    }


    /**
     * Add action handling for all actions and changes
     */
    private void initActionHandling() {
        ActionListener actionListener = actionEvent -> {
            switch (actionEvent.getActionCommand()) {
                case "startSimulate":
                    startSimulation();
                    break;
                case "stopSimulate":
                    stopSimulation();
                    break;
                case "doCapture":
                    doCapture();
                    break;
            }
        };

        captureButton.setActionCommand("doCapture");
        captureButton.addActionListener(actionListener);

        modeButton.addActionListener(actionListener);

        resistanceSlider.addChangeListener(changeEvent -> handleSliderChange());
        resistanceSlider.setMinimum(0);
        resistanceSlider.setMaximum(SLIDER_MAX);
        resistanceSlider.setValue(SLIDER_MAX / 2);

        currentExtResistance.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                currentExtResistance.setText(actionEvent.getActionCommand());
                handleResistanceFieldChange();
            }
        });
    }

    /**
     * Stop doing simulating
     */
    private void stopSimulation() {
        status = Status.STOPPED;
        updateModeButton();

        setLabelStopped();;

        emfField.setEditable(true);
        extResistanceField.setEditable(true);
        intResistanceField.setEditable(true);
        resistanceSlider.setEnabled(false);
        currentExtResistance.setEditable(false);
    }

    /**
     * Start doing simulating
     */
    private void startSimulation() {
        status = Status.ACTIVE;
        updateModeButton();

        String oldEmf = emfField.getText();
        String oldInternalR = intResistanceField.getText();
        String oldExternalRmax = extResistanceField.getText();

        double emf;
        double internalR;
        double externalRmax;

        try {
            emf = Double.parseDouble(oldEmf.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            showWrongArgumentMessage("Wrong EMF value");
            emfField.setText(oldEmf);
            stopSimulation();
            return;
        }

        try {
            internalR = Double.parseDouble(oldInternalR.trim().replace(',', '.'));
            powerSupply = new DCPowerSupply(emf, new FixedResistor(internalR));
        } catch (Exception e) {
            showWrongArgumentMessage("Wrong internal resistance value");
            intResistanceField.setText(oldInternalR);
            stopSimulation();
            return;
        }

        try {
            externalRmax = Double.parseDouble(oldExternalRmax.trim().replace(',', '.'));
            externalResistor = new Potentiometer(externalRmax);
        } catch (Exception e) {
            showWrongArgumentMessage("Wrong external resistance value");
            extResistanceField.setText(oldExternalRmax);
            stopSimulation();
            return;
        }

        try {
            externalResistor.setPosition(100.0 * (resistanceSlider.getValue() / (double) SLIDER_MAX));
        } catch (WrongPhysicsParameterException e) {
            throw new RuntimeException(e);
        }
        currentExtResistance.setText(Double.toString(externalResistor.getResistance()));

        emfField.setEditable(false);
        extResistanceField.setEditable(false);
        intResistanceField.setEditable(false);
        resistanceSlider.setEnabled(true);
        currentExtResistance.setEditable(true);

        circuit = new DCPowerSupplyConnection(powerSupply, externalResistor);
        updateAll();
    }

    private void showWrongArgumentMessage(String mgs) {
        JOptionPane.showMessageDialog(this, mgs, "Wrong argument", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Capture status of the simulation
     */
    private void doCapture() {
        // TODO
    }

    /**
     * Handle slider change
     */
    private void handleSliderChange() {
        if (externalResistor == null)
            return;

        try {
            externalResistor.setPosition(100.0 * resistanceSlider.getValue() / SLIDER_MAX);
        } catch (WrongPhysicsParameterException e) {
            throw new RuntimeException(e);
        }

        currentExtResistance.setText(Double.toString(externalResistor.getResistance()));
        updateAll();
    }

    /**
     * Handle resistance field change
     */
    private void handleResistanceFieldChange() {
        System.out.println("Handel");
        if (externalResistor == null)
            return;

        double currentResistance;
        try {
            currentResistance = Double.parseDouble(currentExtResistance.getText().trim().replace(',', '.'));
            externalResistor.setResistance(currentResistance);
            System.out.println(externalResistor.getResistance());
        } catch (Exception e) {
            showWrongArgumentMessage("Wrong external resistance value");
            return;
        }
        resistanceSlider.setValue((int) (currentResistance / externalResistor.getMaxResistance() * SLIDER_MAX));
        updateAll();
    }

    /**
     * Update fields values after change
     */
    private void updateAll() {
        if (circuit.isShortCircuit()) {
            setLabelActiveShortCircuit();
        } else {
            setLabelActiveNormal();
        }

        uIntRField.setText(Double.toString(circuit.getInternalResistorVoltage()));
        uExtRField.setText(Double.toString(circuit.getExternalResistorVoltage()));
        currentField.setText(Double.toString(circuit.getCurrent()));
        pFullField.setText(Double.toString(circuit.getFullPower()));
        pLossField.setText(Double.toString(circuit.getPowerLoss()));
        pExtField.setText(Double.toString(circuit.getExternalPower()));
    }

    private void setLabelStopped() {
        statusLabel.setText(LABEL_STOPPED);
        statusLabel.setForeground(Color.black);
    }

    private void setLabelActiveNormal() {
        statusLabel.setText(LABEL_ACTIVE_NORMAL);
        statusLabel.setForeground(Color.blue);
    }

    private void setLabelActiveShortCircuit() {
        statusLabel.setText(LABEL_ACTIVE_SHORT_CIRCUIT);
        statusLabel.setForeground(Color.red);
    }


}
