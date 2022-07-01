package it.uniroma2.dicii.amod.onerjscheduling.utils;

/**
 * An enumeration of all the possible states of the branch-and-bound tree nodes.
 */
public enum ProblemStatus {

    /**
     * Node generated but not visited yet.
     */
    NOT_VISITED,

    /**
     * Node being processed. At the end of the execution no one of the nodes should be in this state.
     */
    PROCESSING,

    /**
     * Node visited and able to be expanded (lower bound better than the incumbent).
     */
    EXPANDED,

    /**
     * Node visited and fathomed by dominance rule.
     */
    FATHOMED_DOMINANCE,

    /**
     * Node visited and fathomed by bounding.
     */
    FATHOMED_BOUNDING,

    /**
     * Node visited and closed because guaranteed optimum was reached.
     */
    OPTIMUM_REACHED
}
