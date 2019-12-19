package ohm.low.phys.base;

import ohm.low.phys.Exception.WrongPhysicsParameterException;

public class DCPowerSupply {
    private double emf;
    private Resistor internalResistor;

    /**
     * Creates power supply element
     * @param emf Electromotive force value [ Volts ]
     * @param internalResistor Internal resistor installed in supply
     * @throws WrongPhysicsParameterException If EMF value < 0
     */
    public DCPowerSupply(double emf, Resistor internalResistor) throws WrongPhysicsParameterException {
        if (emf < 0) {
            throw new WrongPhysicsParameterException("EMF is lesser than zero");
        }

        this.emf = emf;
        this.internalResistor = internalResistor;
    }

    /**
     * @return EMF value [ Volts ]
     */
    public double getEmf() {
        return emf;
    }

    /**
     * @return Internal resistor
     */
    public Resistor getInternalResistor() {
        return internalResistor;
    }

}
