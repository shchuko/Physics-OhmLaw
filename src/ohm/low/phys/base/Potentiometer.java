package ohm.low.phys.base;

import ohm.low.phys.Exception.WrongPhysicsParameterException;

public class Potentiometer implements Resistor {
    private double resistance;
    private double maxResistance;

    /**
     * Creates a potentiometer
     * @param maxResistance Max value of potentiometer's resistance
     * @throws WrongPhysicsParameterException If maxResistance value < 0
     */
    public Potentiometer(double maxResistance) throws WrongPhysicsParameterException {
        if (maxResistance < 0) {
            throw new WrongPhysicsParameterException("Resistance value is lesser than 0");
        }

        this.resistance = maxResistance / 2;
        this.maxResistance = maxResistance;
    }

    /**
     * @return Value of max resistance
     */
    public double getMaxResistance() {
        return maxResistance;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public double getCurrentIfVoltage(double voltage) {
        return 0;
    }

    @Override
    public double getVoltageIfCurrent(double current) {
        return 0;
    }

    /**
     * Set position of a potentiometer 'stick'
     * @param coefficient Position in range [0..100], 0 - min value, 100 - maxValue
     * @return Current resistance
     * @throws WrongPhysicsParameterException If coefficient is not in range [0.100]
     */
    public double setPosition(double coefficient) throws WrongPhysicsParameterException {
        if (coefficient < 0 || coefficient > 100) {
            throw new WrongPhysicsParameterException("Position coefficient is not in range 1..100");
        }

        resistance = maxResistance * coefficient / 100;
        return resistance;
    }
}
