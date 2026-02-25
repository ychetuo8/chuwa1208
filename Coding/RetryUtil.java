import java.util.concurrent.Callable;

public class RetryUtil {

    /**
     * Executes the given task with exponential backoff retry.
     *
     * Semantics for this homework/test:
     * - maxRetries is treated as the maximum total attempts (including the first attempt).
     * - attempt starts from 0.
     * - after a failed attempt, sleep for baseDelayMs * 2^attempt (capped at maxDelayMs), then retry.
     *
     * @param task        The task to execute
     * @param maxRetries  Maximum number of attempts (must be > 0)
     * @param baseDelayMs Base delay in milliseconds (must be >= 0)
     * @param maxDelayMs  Maximum delay in milliseconds (must be >= 0)
     * @return The result of the task
     * @throws Exception If all attempts fail, throws the last exception
     */
    public static <T> T executeWithRetry(
            Callable<T> task,
            int maxRetries,
            long baseDelayMs,
            long maxDelayMs) throws Exception {

        if (task == null) throw new IllegalArgumentException("task must not be null");
        if (maxRetries <= 0) throw new IllegalArgumentException("maxRetries must be > 0");
        if (baseDelayMs < 0) throw new IllegalArgumentException("baseDelayMs must be >= 0");
        if (maxDelayMs < 0) throw new IllegalArgumentException("maxDelayMs must be >= 0");

        Exception last = null;

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                return task.call();
            } catch (Exception e) {
                last = e;

                // Exhausted all attempts
                if (attempt == maxRetries - 1) {
                    throw last;
                }

                long delay = computeDelay(baseDelayMs, maxDelayMs, attempt);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    // Preserve interrupt status and propagate
                    Thread.currentThread().interrupt();
                    throw ie;
                }
            }
        }

        // Should not reach here
        throw last != null ? last : new IllegalStateException("Retry loop ended unexpectedly");
    }

    private static long computeDelay(long baseDelayMs, long maxDelayMs, int attempt) {
        // If either base or max is 0, delay should be 0
        if (baseDelayMs == 0 || maxDelayMs == 0) return 0;

        // 1L << attempt overflows for large attempt, clamp early
        if (attempt >= 62) return maxDelayMs;

        long factor = 1L << attempt;

        // Overflow-safe multiply with cap
        if (baseDelayMs > Long.MAX_VALUE / factor) {
            return maxDelayMs;
        }

        long raw = baseDelayMs * factor;
        return Math.min(raw, maxDelayMs);
    }
}
