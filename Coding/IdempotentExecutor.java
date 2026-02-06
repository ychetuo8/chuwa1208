import java.util.*;
import java.util.function.Supplier;

/**
 * Programming Question 11
 * Implement execute() so each requestId runs at most once, and subsequent calls
 * return the cached result (including null).
 */
public class IdempotentExecutor<T> {

    private final Map<String, T> cache = new HashMap<>();

    /**
     * Execute the operation only if requestId has not been seen before.
     * If it has been seen, return the cached result.
     *
     * @param requestId a unique identifier for this request
     * @param operation the operation to execute
     * @return the result (from first execution, or cached)
     */
    public T execute(String requestId, Supplier<T> operation) {
        if (cache.containsKey(requestId)) {
            return cache.get(requestId); // may be null, but must be cached
        }

        T result = operation.get();
        cache.put(requestId, result); // cache even if result is null
        return result;
    }

    // ==================== TEST CODE (DO NOT MODIFY) ====================
    public static void main(String[] args) {
        IdempotentExecutor<String> executor = new IdempotentExecutor<>();

        int[] callCount = {0};
        Supplier<String> placeOrder = () -> {
            callCount[0]++;
            return "order-placed";
        };

        // Test 1: first call should execute the operation
        String r1 = executor.execute("req-001", placeOrder);
        if (!"order-placed".equals(r1)) {
            System.out.println("FAIL Test 1: expected 'order-placed', got '" + r1 + "'");
            return;
        }
        if (callCount[0] != 1) {
            System.out.println("FAIL Test 1: operation should have been called once, was called " + callCount[0] + " times");
            return;
        }
        System.out.println("PASS Test 1: first call executes operation and returns correct result");

        // Test 2: duplicate call with same requestId should return cached result
        String r2 = executor.execute("req-001", placeOrder);
        if (r1 != r2) {
            System.out.println("FAIL Test 2: duplicate call should return the same cached reference");
            return;
        }
        if (callCount[0] != 1) {
            System.out.println("FAIL Test 2: operation should still have been called only once, was called " + callCount[0] + " times");
            return;
        }
        System.out.println("PASS Test 2: duplicate call returns cached result, operation not called again");

        // Test 3: different requestId should execute the operation again
        String r3 = executor.execute("req-002", placeOrder);
        if (callCount[0] != 2) {
            System.out.println("FAIL Test 3: new requestId should trigger a new execution, call count = " + callCount[0]);
            return;
        }
        System.out.println("PASS Test 3: different requestId triggers new execution");

        // Test 4: operation that returns null should still be cached (at-most-once)
        int[] nullCallCount = {0};
        Supplier<String> returnsNull = () -> {
            nullCallCount[0]++;
            return null;
        };

        String n1 = executor.execute("req-null", returnsNull);
        String n2 = executor.execute("req-null", returnsNull);
        if (n1 != null || n2 != null) {
            System.out.println("FAIL Test 4: expected null result, got '" + n1 + "' and '" + n2 + "'");
            return;
        }
        if (nullCallCount[0] != 1) {
            System.out.println("FAIL Test 4: operation returning null should still only be called once, was called " + nullCallCount[0] + " times");
            return;
        }
        System.out.println("PASS Test 4: null result is cached correctly (at-most-once guaranteed)");

        System.out.println("\nAll tests passed!");
    }
}
