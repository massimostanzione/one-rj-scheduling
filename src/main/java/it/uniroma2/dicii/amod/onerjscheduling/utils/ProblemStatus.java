package it.uniroma2.dicii.amod.onerjscheduling.utils;

/**
 * An enumeration of all the possible states of the branch-and-bound tree nodes.
 */
public enum ProblemStatus {

    /**
     * Node generated but not visited yet.
     * At the end of the execution no one of the nodes should be in this state.
     */
    NOT_VISITED,

    /**
     * Node being pre-processed.
     * At the end of the execution no one of the nodes should be in this state.
     */
    PROCESSING,

    /**
     * Node pre-processed, waiting for further processing (i.e. subproblems generation).
     * At the end of the execution no one of the nodes should be in this state.
     */
    EXPANDABLE,

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
    OPTIMAL_REACHED
}
