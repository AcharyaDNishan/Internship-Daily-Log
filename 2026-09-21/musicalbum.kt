class Song(
    val title: String,
    val artist: String,
    val year: Int,
    val playcount: Int,
    val popularity: Boolean = playcount > 1000
)

fun main() {
    val m = Song("Hello", "Adele", 2006, 110000)
    println("${m.title}, performed by ${m.artist}, was released in ${m.year}.")
    when (m.popularity) {
        true -> println("${m.title} is popular.")
        false -> println("${m.title} is not popular.")
    }
}