package ohm.low.phys.base;

import ohm.low.phys.Exception.WrongPhysicsParameterException;

public class ResistorConnection {
    private ResistorConnection() {

    }

    /**
     * Calculates full resistance of parallel connected resistors
     * @param resistors List of connected resistors
     * @return Resistor with result resistance value
     */
    public static FixedResistor ConnectParallel(Resistor ... resistors) {
        double invertedSum = 0;

        for (Resistor resistor : resistors) {
            if (resistor.getResistance() == 0) {
                try {
                    return new FixedResistor(0);
                } catch (WrongPhysicsParameterException e) {
                    throw new RuntimeException(e);
                }
            }

            invertedSum += 1 / resistor.getResistance();
        }

        double resistanceResult = (invertedSum == 0) ? Double.POSITIVE_INFINITY : 1 / invertedSum;
        try {
            return new FixedResistor(resistanceResult);
        } catch (WrongPhysicsParameterException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calculates full resistance of resistors connected in series
     * @param resistors List of connected resistors
     * @return Resistor with result resistance value
     */
    public static FixedResistor ConnectInSeries(Resistor ... resistors) {
        double resistanceSum = 0;
        for (Resistor r: resistors) {
            resistanceSum += r.getResistance();
        }

        try {
            return new FixedResistor(resistanceSum);
        } catch (WrongPhysicsParameterException e) {
            throw new RuntimeException(e);
        }
    }
}
