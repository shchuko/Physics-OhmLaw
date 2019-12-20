package gui.app.phys;

import ohm.low.phys.DCPowerSupplyConnection;
import ohm.low.phys.Exception.WrongPhysicsParameterException;
import ohm.low.phys.base.Potentiometer;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XChartPanel;

import javax.swing.*;
import java.awt.*;

class GraphsCreator {
    private DCPowerSupplyConnection circuit;
    private Potentiometer potentiometer;

    private double[] current;
    private double[] uExt;
    private double[] uInt;
    private double[] pFull;
    private double[] pLoss;
    private double[] pExt;

    private JPanel uPanel;
    private JPanel pPanel;
    private Container container = new Container();


    /**
     * Creates object to print graphs describe circuit
     * @param connection Circuit with potentiometer connected as extenral resistor
     */
    GraphsCreator(DCPowerSupplyConnection connection) throws IllegalArgumentException {
        if (! (connection.getExternalResistor() instanceof Potentiometer)) {
            throw new IllegalArgumentException("Wrong argument: External resistor not a potentiometer");
        }

        var pTemp = (Potentiometer) connection.getExternalResistor();
        try {
            potentiometer = new Potentiometer(pTemp.getMaxResistance());
        } catch (WrongPhysicsParameterException e) {
            throw new RuntimeException();
        }

        circuit = new DCPowerSupplyConnection(connection.getPowerSupply(), potentiometer);

        calculateValues();
        createPanels();
        container.setLayout (new GridLayout(1, 2, 10, 10));

        container.add(uPanel, BorderLayout.WEST);
        container.add(pPanel, BorderLayout.EAST);
    }

    private void calculateValues() {
        double resistanceValue = potentiometer.getMaxResistance();

        int STEPS;
        if (resistanceValue < 1000) {
            STEPS = 1000;
        } else if (resistanceValue < 5000) {
            STEPS = 5000;
        } else if (resistanceValue < 10000){
            STEPS = 20000;
        } else {
            STEPS = 40000;
        }

        current = new double[STEPS];
        uExt = new double[STEPS];
        uInt = new double[STEPS];
        pFull = new double[STEPS];
        pLoss = new double[STEPS];
        pExt = new double[STEPS];

        for (int i = 0; i < STEPS; ++i) {
            double position = (double) i / STEPS * 100;
            try {
                potentiometer.setPosition(position);
            } catch (WrongPhysicsParameterException e) {
                throw new RuntimeException(e);
            }

            if (circuit.getCurrent() == Double.POSITIVE_INFINITY) {
                try {
                    potentiometer.setResistance(potentiometer.getResistance() + 0.000001);
                } catch (WrongPhysicsParameterException e) {
                    throw new RuntimeException(e);
                }
            }

            current[i] = circuit.getCurrent();
            uExt[i] = circuit.getExternalResistorVoltage();
            uInt[i] = circuit.getInternalResistorVoltage();
            pFull[i] = circuit.getFullPower();
            pExt[i] = circuit.getExternalPower();
            pLoss[i] = circuit.getPowerLoss();
        }
    }

    private void createPanels() {
        var uChart = QuickChart.getChart("U(I)",
                "I, A",
                "U, V",
                new String[] {"Uexternal", "Uinternal"},
                current,
                new double[][] { uExt, uInt });
        uPanel = new XChartPanel<>(uChart);

        var pChart = QuickChart.getChart("P(I)",
                "I, A",
                "P, Watt",
                new String[]{"Pexternal", "Ploss", "Pfull"},
                current,
                new double[][] { pExt, pLoss, pFull });
        pPanel = new XChartPanel<>(pChart);
    }

    Container getContainer() {
        return container;
    }

}
