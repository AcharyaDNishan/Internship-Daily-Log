1. Create a suspend function and starting it manually using startCoroutine().
2. Create a suspend function that prints "Task started" and "Task finished". Start the function manually using startCoroutine() and provide a Continuation to receive the result.
3. Create a suspend function that prints "Hello" and use createCoroutine() to create its coroutine without starting it. Then start it using the returned continuation.
4. Create a coroutine using createCoroutine() and prove that its code does not execute until resume() is called.
5. Create a suspended coroutine using createCoroutine() and use resume(Unit) to start it.
6. Create a simple suspend function containing a suspension point and manually resume its continuation.
7. Create a continuation whose result type is Int. Resume it successfully using resumeWith(Result.success(...)).
8. Resume a continuation with Result.failure(Exception("Something went wrong")) and print the error.
9. Create a coroutine that repeatedly prints numbers from 1 to 100 and calls ensureActive() on every iteration. Cancel the coroutine after a short delay.
10. Create a long-running loop using ensureActive(). Observe what happens when the coroutine is cancelled.
11. Create a suspend function called waitForInput() using suspendCancellableCoroutine. Resume the continuation after a simulated event.
12. Create a cancellable suspend function that waits for 5 seconds. Cancel the coroutine before the 5 seconds finish and handle the cancellation.
13. Create a Job and retrieve it from a context using context[Job]. Print whether the job is active.
14. Create a CoroutineName and retrieve it from a coroutine context using context[CoroutineName] and print the name.
15. Retrieve a CoroutineName from a context using context.get(CoroutineName).
16. Retrieve a Job using context.get(Job) and print its state.
17. Create two contexts context1 = CoroutineName("First") and context2 = CoroutineName("Second") and combine contexts using + and observe which CoroutineName is stored.
