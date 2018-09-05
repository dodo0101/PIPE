package uk.ac.imperial.pipe.models.petrinet;

import uk.ac.imperial.state.State;

/**
 * A transition is a Petri net component that is responsible for firing and thus moving
 * tokens from inbound places to outbound places
 */
public interface Transition extends Connectable {
    /**
     * Message fired when the Transitions priority changes
     */
    String PRIORITY_CHANGE_MESSAGE = "priority";
    /**
     * Message fired when the rate changes
     */
    String RATE_CHANGE_MESSAGE = "rate";
    /**
     * Message fired when the angle changes
     */
    String ANGLE_CHANGE_MESSAGE = "angle";
    /**
     * Message fired when the transition becomes timed/becomes immediate
     */
    String TIMED_CHANGE_MESSAGE = "timed";
    /**
     * Message fired when the transition becomes an infinite/single server
     */
    String INFINITE_SEVER_CHANGE_MESSAGE = "infiniteServer";
    /**
     * Message fired when the transition is enabled
     */
    String ENABLED_CHANGE_MESSAGE = "enabled";
    /**
     * Message fired when the transition is enabled
     */
    String DISABLED_CHANGE_MESSAGE = "disabled";
    /**
     * Height of the transition
     */
    int TRANSITION_HEIGHT = 30;
    /**
     * Width of the transition
     */
    int TRANSITION_WIDTH = TRANSITION_HEIGHT / 3;

    /**
     *
     * @return priority of the transition, transitions with the highest priority that are enabled will only be allowed to fire
     */
    int getPriority();

    /**
     *
     * @param priority the new priority of the transition
     */
    void setPriority(int priority);

    /**
     *
     * @return the rate that this transition references
     */
    Rate getRate();

    /**
     *
     * @param rate new rate of this transition
     */
    void setRate(Rate rate);

    /**
     * Evaluate the transitions rate against the given state
     * <p>
     * If an infinite server the transition will return its rate * enabling degree
     * </p>
     * @param state given state of a petri net to evaluate the functional rate of
     * @param petriNet to be evaluated 
     * @return actual evaluated rate of the Petri net
     */
    Double getActualRate(PetriNet petriNet, State state);

    /**
     *
     * @return string representation of the rate
     */
    String getRateExpr();

    /**
     *
     * @return true if infinite server, false if single
     */
    boolean isInfiniteServer();


    /**
     *
     * @param infiniteServer true if infinite server, false if single
     */
    void setInfiniteServer(boolean infiniteServer);

    /**
     *
     * @return angle from (0,0) facing NORTH that this transition should be displayed at
     */
    int getAngle();

    /**
     *
     * @param angle set angle from (0,0) facing NORTH that this transition should be displayed at
     */
    void setAngle(int angle);

    /**
     *
     * @return true
     */
    boolean isTimed();

    /**
     *
     * @param timed true if this transition supports timed transition semantics, false for immediate.
     */
    void setTimed(boolean timed);

    /**
     * Enable the transition for firing
     */
    void enable();

    /**
     * Disable the transition for firing
     */
    void disable();

    /**
     *
     * These methods probably shouldnt be used anymore since its better to calcualte properly if the
     * transition is enabled in terms of Petri net semantics
     * @return true if enabled
     */
    boolean isEnabled();
}
