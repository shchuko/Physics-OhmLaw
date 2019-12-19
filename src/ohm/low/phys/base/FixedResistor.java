package ohm.low.phys.base;

import ohm.low.phys.Exception.WrongPhysicsParameterException;

public class FixedResistor implements Resistor {
    private double resistance;

    /**
     * Creates a resistor with fixed resistance value
     * @param resistance Resistance value [ Ohms ]
     * @throws WrongPhysicsParameterException If resistance < 0
     */
    public FixedResistor(double resistance) throws WrongPhysicsParameterException {
        if (resistance < 0) {
            throw new WrongPhysicsParameterException("Resistance value lower than zero");
        }
        this.resistance = resistance;
    }

    @Override
    public double getResistance() {
        return resistance;
    }

    @Override
    public double getCurrentIfVoltage(double voltage) {
        if (resistance == 0 && voltage != 0) {
            return Math.signum(voltage) * Double.POSITIVE_INFINITY;
        }

        if (voltage == 0) {
            return 0;
        }

        return voltage / resistance;
    }

    @Override
    public double getVoltageIfCurrent(double current) {
        return current * resistance;
    }

}
