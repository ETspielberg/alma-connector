package org.unidue.ub.libintel.almaconnector.clients.alma;

import feign.RetryableException;
import feign.Retryer;

/**
 * Retryer to account for problems in connecting to alma api
 */
public class AlmaRetryer implements Retryer {

    private final int maxAttempts;

    private final long backoff;

    public int attempt;

    /**
     * general constructor with default properties (2000ms time delay, maximum of 3 attempts)
     */
    public AlmaRetryer() {
        this(2000, 3);
    }

    /**
     * constructor allowing to set the max attempts and backoff time
     * @param backoff the delay time in ms
     * @param maxAttempts the maximum number of attempts
     */
    public AlmaRetryer(long backoff, int maxAttempts) {
        this.backoff = backoff;
        this.maxAttempts = maxAttempts;
        this.attempt = 1;
    }

    /**
     * decides upon the number of retries to continue or cancel the requests
     * @param e RetryableException triggering the retries
     */
    public void continueOrPropagate(RetryableException e) {
        if (attempt++ >= maxAttempts) {
            throw e;
        }

        try {
            Thread.sleep(backoff);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * @return a duplicated <code>AlmaRetryer</code> object
     */
    @Override
    public Retryer clone() {
        return new AlmaRetryer(backoff, maxAttempts);
    }
}
