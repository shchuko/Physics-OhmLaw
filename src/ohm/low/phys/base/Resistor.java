package ohm.low.phys.base;

public interface Resistor {
    /**
     * Get current resistance of the resistor
     * @return Current resistance [ Ohms ]
     */
    double getResistance();

    /**
     * Calculates current through the resistor for some voltage value on it
     * @param voltage Voltage value on resistor [ Volts ]
     * @return Current value [ Amps ]
     */
    double getCurrentIfVoltage(double voltage);

    /**
     * Calculates voltage on the resistor if the current value is known
     * @param current Current pushed through the resistor [ Amps ]
     * @return Voltage value on the resistor [ Volts ]
     */
    double getVoltageIfCurrent(double current);
}
