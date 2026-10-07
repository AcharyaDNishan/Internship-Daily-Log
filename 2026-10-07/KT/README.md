1. Create a coroutine producer that sends the numbers 1 through 5 to a ReceiveChannel, then consume and print them.
2. Write a producer that sends the squares of 1 through 10 and closes its channel when finished.
3. Create two producers for two different data sources and consume both channels, keeping their results separate.
4. Build a producer that emits values every 500 ms and stop the producer safely when the consumer cancels the channel.