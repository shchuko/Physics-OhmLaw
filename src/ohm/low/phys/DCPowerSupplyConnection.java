package ohm.low.phys;


import ohm.low.phys.base.DCPowerSupply;
import ohm.low.phys.base.Resistor;
import ohm.low.phys.base.ResistorConnection;

public class DCPowerSupplyConnection {
    private DCPowerSupply powerSupply;
    private Resistor externalResistor;

    /**
     * Creates circuit with resistor connected to power supply
     * @param powerSupply Power supply installed into circuit
     * @param externalResistor External resistor installed into circuit
     */
    public DCPowerSupplyConnection(DCPowerSupply powerSupply, Resistor externalResistor) {
        this.powerSupply = powerSupply;
        this.externalResistor = externalResistor;
    }

    /**
     * Calculates voltage on the external resistor
     * @return External resistor voltage value [ Volts ]
     */
    public double getExternalResistorVoltage() {
        return getCurrent() * externalResistor.getResistance();
    }

    /**
     * Calculates voltage on the internal resistor
     * @return Internal resistor voltage value [ Volts ]
     */
    public double getInternalResistorVoltage() {
        return getCurrent() * powerSupply.getInternalResistor().getResistance();
    }

    /**
     * Calculates current through the circuit
     * @return Current value [ Amps ]
     */
    public double getCurrent() {
        Resistor resistor = ResistorConnection.ConnectInSeries(powerSupply.getInternalResistor(), externalResistor);
        if (resistor.getResistance() == 0) {
            return Double.POSITIVE_INFINITY;
        }

        return powerSupply.getEmf() / resistor.getResistance();
    }

    /**
     * @return true if circuit is short, false if not
     */
    public boolean isShortCircuit() {
        return externalResistor.getResistance() == 0;
    }

    /**
     * Get full power produced into circuit
     * @return Power value [ Watts ]
     */
    public double getFullPower() {
        return powerSupply.getEmf() * getCurrent();
    }

    /**
     * Get power loss on internal resistor
     * @return Power loss value [ Watts ]
     */
    public double getPowerLoss() {
        return getCurrent() * getCurrent() * powerSupply.getInternalResistor().getResistance();
    }

    /**
     * Get power produced in external part of the circuit
     * @return Power external value [ Watts ]
     */
    public double getExternalPower() {
        return getCurrent() * getExternalResistorVoltage();
    }

    /**
     * Get power supply connected to circuit
     * @return Connected power supply
     */
    public DCPowerSupply getPowerSupply() {
        return powerSupply;
    }

    /**
     * Get external resistor connected to circuit
     * @return Connected external resistor
     */
    public Resistor getExternalResistor() {
        return externalResistor;
    }
}
