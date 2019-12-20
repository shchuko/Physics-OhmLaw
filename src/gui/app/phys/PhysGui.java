package gui.app.phys;

import ohm.low.phys.DCPowerSupplyConnection;
import ohm.low.phys.Exception.WrongPhysicsParameterException;
import ohm.low.phys.base.DCPowerSupply;
import ohm.low.phys.base.FixedResistor;
import ohm.low.phys.base.Potentiometer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PhysGui extends JFrame {
    private enum Status {STOPPED, ACTIVE}
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

    private JFrame graphsFrame = new JFrame();

    /**
     * Creates GUI for interactive Ohm's law simulator control panel
     */
    public PhysGui() {
        super("Ohm's Law simulator for DC circuit");

        Image img = Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/ico.png"));
        setIconImage(img);
        setTitle("Ohm's Law simulator for DC circuit");

        graphsFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        graphsFrame.setVisible(false);
        graphsFrame.setIconImage(img);
        graphsFrame.setTitle("Ohm's Law simulator graphs");

        loadImage();
        fillFieldsZeroes();
        updateModeButton();
        captureButton.setEnabled(false);
        initActionHandling();
        setLabelStopped();

        setMinimumSize(new Dimension(900, 442));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        pack();
        centring(this);
        setVisible(true);
    }

    /**
     * Loads image with circuit from img/circuit.png
     */
    private void loadImage() {
        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/circuit.png"));

        int width = 350;
        int height = 200;
        image = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(image));
    }

    /**
     * Places frame to the screen center
     */
    private static void centring(JFrame frame) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getWidth() / 2,
                dim.height / 2 - frame.getHeight() / 2);
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
                    showGraphs();
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
        captureButton.setEnabled(false);

        setLabelStopped();

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
        captureButton.setEnabled(true);

        double emf;
        double internalR;
        double externalRmax;

        try {
            emf = Double.parseDouble(emfField.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            showWrongArgumentMessage("Wrong EMF value");
            emfField.setText("0.0");
            stopSimulation();
            return;
        }

        try {
            internalR = Double.parseDouble(intResistanceField.getText().trim().replace(',', '.'));
            powerSupply = new DCPowerSupply(emf, new FixedResistor(internalR));
        } catch (Exception e) {
            showWrongArgumentMessage("Wrong internal resistance value");
            intResistanceField.setText("0.0");
            stopSimulation();
            return;
        }

        try {
            externalRmax = Double.parseDouble(extResistanceField.getText().trim().replace(',', '.'));
            externalResistor = new Potentiometer(externalRmax);
        } catch (Exception e) {
            showWrongArgumentMessage("Wrong external resistance value");
            extResistanceField.setText("0.0");
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
    private void showGraphs() {
        if (circuit.getCurrent() == Double.POSITIVE_INFINITY) {
            showWrongArgumentMessage("Can't create graphs for this parameters");
            return;
        }
        graphsFrame.setContentPane(new GraphsCreator(circuit).getContainer());
        graphsFrame.pack();
        centring(graphsFrame);
        graphsFrame.setVisible(true);
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
        if (externalResistor == null)
            return;

        double currentResistance;
        try {
            currentResistance = Double.parseDouble(currentExtResistance.getText().trim().replace(',', '.'));
            externalResistor.setResistance(currentResistance);
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

    /**
     * Set status label to stop state
     */
    private void setLabelStopped() {
        statusLabel.setText(LABEL_STOPPED);
        statusLabel.setForeground(Color.black);
    }

    /**
     * Set status label to active state
     */
    private void setLabelActiveNormal() {
        statusLabel.setText(LABEL_ACTIVE_NORMAL);
        statusLabel.setForeground(Color.blue);
    }

    /**
     * Set status label to short circuit state
     */
    private void setLabelActiveShortCircuit() {
        statusLabel.setText(LABEL_ACTIVE_SHORT_CIRCUIT);
        statusLabel.setForeground(Color.red);
    }


}
