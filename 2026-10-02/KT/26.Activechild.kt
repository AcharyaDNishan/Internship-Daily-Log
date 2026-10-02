import kotlinx.coroutines.*
fun main() = runBlocking {
    val children = listOf(
        launch(CoroutineName("Child1")) {
            delay(3000)
            println("This is Child1")
        },
        launch(CoroutineName("Child2")) {
            delay(1000)
            println("This is Child2")
        },
        launch(CoroutineName("Child3")) {
            delay(2000)
            println("This is Child3")
        }
    )
    yield()
    println("Children active status:")
    children.forEach { child ->
        println("${child} => ${child.isActive}")
    }
    println("Any child still active? ${children.any { it.isActive }}")
    children.forEach { it.join() }
}