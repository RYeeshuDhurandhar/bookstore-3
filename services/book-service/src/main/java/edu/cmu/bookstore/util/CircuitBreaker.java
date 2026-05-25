package edu.cmu.bookstore.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Custom implementation of a Circuit Breaker state machine.
 *
 * This component tracks the health of an external interaction and
 * transitions between CLOSED, OPEN, and HALF_OPEN states according
 * to the following rules:
 * - CLOSED: Calls are allowed. A failure transitions the state to OPEN.
 * - OPEN: Calls are rejected immediately. After 60 seconds, the state
 *   moves to HALF_OPEN.
 * - HALF_OPEN: One trial call is allowed. Success transitions to CLOSED,
 *   while failure transitions back to OPEN.
 */
@Component
public class CircuitBreaker {

    private static final Logger logger = LoggerFactory.getLogger(CircuitBreaker.class);

    /**
     * Enum defining the possible states of the circuit breaker.
     */
    public enum State {
        CLOSED, OPEN, HALF_OPEN
    }

    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
    private Instant lastFailureTime = null;
    private static final long OPEN_DURATION_SECONDS = 60;

    /**
     * Checks if a call is permitted based on the current circuit state.
     *
     * @return true if the call is allowed, false if it should be rejected
     */
    public synchronized boolean isCallAllowed() {
        State currentState = state.get();

        if (currentState == State.OPEN) {
            // If the circuit is open, check if enough time has passed to enter HALF_OPEN.
            if (lastFailureTime != null && 
                Instant.now().isAfter(lastFailureTime.plusSeconds(OPEN_DURATION_SECONDS))) {
                logger.info("Circuit transition from OPEN to HALF_OPEN");
                state.set(State.HALF_OPEN);
                return true;
            }
            logger.warn("Circuit is OPEN. Call rejected.");
            return false;
        }

        return true;
    }

    /**
     * Records a successful interaction.
     *
     * Transitions the circuit from HALF_OPEN to CLOSED.
     */
    public synchronized void recordSuccess() {
        if (state.get() == State.HALF_OPEN) {
            logger.info("Circuit transition from HALF_OPEN to CLOSED");
            state.set(State.CLOSED);
        }
    }

    /**
     * Records a failed interaction.
     *
     * Transitions the circuit to OPEN and records the failure time.
     */
    public synchronized void recordFailure() {
        logger.error("Circuit transition to OPEN due to failure");
        state.set(State.OPEN);
        lastFailureTime = Instant.now();
    }

    /**
     * Returns the current state of the circuit breaker.
     *
     * @return current state
     */
    public State getState() {
        return state.get();
    }
}
