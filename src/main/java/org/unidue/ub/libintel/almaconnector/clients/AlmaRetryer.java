package org.unidue.ub.libintel.almaconnector.clients;

import feign.RetryableException;
import feign.Retryer;

public class AlmaRetryer implements Retryer {

    private final int maxAttempts;

    private final long backoff;

    public int attempt;

    public AlmaRetryer() {
        this(2000, 3);
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public long getBackoff() {
        return backoff;
    }

    public AlmaRetryer(long backoff, int maxAttempts) {
        this.backoff = backoff;
        this.maxAttempts = maxAttempts;
        this.attempt = 1;
    }

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

    @Override
    public Retryer clone() {
        return new AlmaRetryer(backoff, maxAttempts);
    }
}
