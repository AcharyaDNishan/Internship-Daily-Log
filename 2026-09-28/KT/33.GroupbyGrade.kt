data class Student(val name: String, val grade: String)

fun main() {
    val list = listOf(
        Student("Nishan", "A"),
        Student("Ram", "B"),
        Student("Hari", "A")
    )
    val result = list.groupBy { it.grade }
    println(result)
}