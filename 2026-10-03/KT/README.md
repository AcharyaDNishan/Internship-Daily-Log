1. Create a coroutine using CoroutineStart.LAZY and manually start it using job.start().
2. Create a lazy async coroutine and use start() before calling await().
3. Create a coroutine that delays for 2 seconds. Check isCompleted before and after the coroutine finishes.
4. Create a Job, wait for it using join(), and then print job.isCompleted.
5. Create a coroutine, cancel it, and check job.isCancelled.
6. Create a coroutine that throws an exception and check whether its job becomes cancelled.