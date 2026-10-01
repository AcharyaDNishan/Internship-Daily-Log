1. Creating a suspend function that performs two delayed operations sequentially.
2. Comparing Sequential Execution vs Concurrent Execution
3. Use withContext(Dispatchers.Default) to perform a simple calculation. Print the result after switching context.
4. Using withContext(Dispatchers.IO) to simulate a file-reading operation using delay().
5. Sum of 1 to 1000 usinf Dispatcher.Default.
6. Using coroutineScope with 2 child performing differenet task.
7. Creating three child in a coroutineScope and throwing an Exception in one.
8. No. 7 but using supervisorScope.
9. Try-Catch inside Launch.
10. Exception Handling using CoroutineExceptionHandler.
11. Creating a coroutine that has a timeout of 2 seconds using withTimeout. Making the task take 5 seconds and Observing the result.
12. No. 111 with withTimeoutOrNull.
13. Using isActive and Cancel
14. Create a simple program that simulates downloading three files concurrently. Each file should take a different amount of time.
15. Taskmanager that starts three coroutine tasks, give each task a different delay, print when each starts, print when each finishes, allow the user to cancel the tasks and wait for all remaining tasks to finish.