package ohm.low.phys.Exception;

public class WrongPhysicsParameterException extends Exception {
    public WrongPhysicsParameterException() {

    }

    public WrongPhysicsParameterException(String message) {
        super(message);
    }

    public WrongPhysicsParameterException(Exception e) {
        super(e);
    }
}
