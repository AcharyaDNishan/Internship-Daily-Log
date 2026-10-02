1. Create a suspend function and starting it manually using startCoroutine().
2. Create a suspend function that prints "Task started" and "Task finished". Start the function manually using startCoroutine() and provide a Continuation to receive the result.
3. Create a suspend function that prints "Hello" and use createCoroutine() to create its coroutine without starting it. Then start it using the returned continuation.
4. Create a coroutine using createCoroutine() and prove that its code does not execute until resume() is called.
5. Create a suspended coroutine using createCoroutine() and use resume(Unit) to start it.
6. Create a simple suspend function containing a suspension point and manually resume its continuation.
