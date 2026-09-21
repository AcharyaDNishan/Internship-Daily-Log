// 1. Define the annotation (our custom label)
annotation class Task(val name: String, val priority: Int = 0)

// 2. Mark some functions with it
class AppTasks {

    @Task(name = "Send Welcome Email", priority = 1)
    fun sendWelcomeEmail() {
        println("📧 Sending welcome email...")
    }

    @Task(name = "Generate Report", priority = 3)
    fun generateReport() {
        println("📊 Generating monthly report...")
    }

    @Task(name = "Clean Temp Files", priority = 2)
    fun cleanTempFiles() {
        println("🧹 Cleaning temp files...")
    }

    // This one is NOT annotated, so it won't be picked up
    fun helperFunction() {
        println("This is just a helper, not a task.")
    }
}

// 3. Use reflection to find all @Task-annotated functions and run them by priority
fun runAllTasks(obj: Any) {
    val tasks = obj::class.members
        .mapNotNull { member ->
            val annotation = member.annotations
                .filterIsInstance<Task>()
                .firstOrNull()
            if (annotation != null) {
                Triple(annotation.name, annotation.priority, member)
            } else null
        }
        .sortedByDescending { it.second } // highest priority first

    println("=== Found ${tasks.size} tasks ===\n")

    for ((name, priority, member) in tasks) {
        println("▶ Running [Priority $priority]: $name")
        member.call(obj)  // execute the function
        println()
    }
}

fun main() {
    runAllTasks(AppTasks())
}