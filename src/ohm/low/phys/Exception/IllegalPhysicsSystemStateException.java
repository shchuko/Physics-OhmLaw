package ohm.low.phys.Exception;

public class IllegalPhysicsSystemStateException extends Exception {
    public IllegalPhysicsSystemStateException() {

    }

    public IllegalPhysicsSystemStateException(String message) {
        super(message);
    }

    public IllegalPhysicsSystemStateException(Exception e) {
        super(e);
    }
}
