package uk.ac.imperial.pipe.visitor;

import uk.ac.imperial.pipe.models.petrinet.DiscreteTransition;
import uk.ac.imperial.pipe.models.petrinet.DiscreteTransitionVisitor;
import uk.ac.imperial.pipe.models.petrinet.Transition;

/**
 * Class used to clone all concrete implementations of {@link uk.ac.imperial.pipe.models.petrinet.Transition}
 */
public final class TransitionCloner implements DiscreteTransitionVisitor {
    /**
     * Cloned transition, null before visit is called
     */
    public Transition cloned;

    /**
     * Clones a discrete transition
     * @param transition to be visited
     */
    @Override
    public void visit(DiscreteTransition transition) {
        cloned = new DiscreteTransition(transition);
    }
}
