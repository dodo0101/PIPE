package uk.ac.imperial.pipe.exceptions;

/**
 * Exception thrown from the {@link uk.ac.imperial.pipe.models.petrinet.PetriNet} when
 * searching for a component that does not exist
 */
public class PetriNetComponentNotFoundException extends PetriNetComponentException {
    /**
     *
     * @param message message of the exception
     */
    public PetriNetComponentNotFoundException(String message) {
        super(message);
    }
}

