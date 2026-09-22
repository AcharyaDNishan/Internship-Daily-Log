import java.io.File
data class Repetitions(val firstRound: Int,val secondRound: Int)
interface NextRepetitionsRepository {
    fun read(): Repetitions?
    fun write(repetitions: Repetitions)
}
class FileNextRepetitionsRepository : NextRepetitionsRepository {
    private val file = File("next_repetitions.txt")
    override fun read(): Repetitions? {
        if (!file.exists()) {
            return null
        }
        val lines = file.readLines()
        val firstRound = lines.getOrNull(0)?.toIntOrNull()
            ?: return null
        val secondRound = lines.getOrNull(1)?.toIntOrNull()
            ?: return null
        return Repetitions(
            firstRound = firstRound,
            secondRound = secondRound
        )
    }
    override fun write(repetitions: Repetitions) {
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(
            "${repetitions.firstRound}\n" +
            "${repetitions.secondRound}"
        )
    }
}
class WorkoutManager {
    fun nextRepetitions(
        firstRoundDone: Int,
        secondRoundDone: Int,
        thirdRoundDone: Int,
        repetitions: Repetitions
    ): Repetitions {
        return when {
            firstRoundDone < repetitions.firstRound -> {
                repetitions.copy(
                    firstRound = (repetitions.firstRound - 1)
                        .coerceAtLeast(0),
                    secondRound = (repetitions.secondRound - 1)
                        .coerceAtLeast(0)
                )
            }
            secondRoundDone < repetitions.secondRound -> {
                repetitions.copy(
                    secondRound = (repetitions.secondRound - 1)
                        .coerceAtLeast(0)
                )
            }
            thirdRoundDone > repetitions.firstRound -> {
                repetitions.copy(
                    firstRound = repetitions.firstRound + 1,
                    secondRound = repetitions.secondRound + 1
                )
            }
            thirdRoundDone > repetitions.secondRound -> {
                repetitions.copy(
                    secondRound = repetitions.secondRound + 1
                )
            }
            else -> repetitions
        }
    }
}
class WorkoutInteractor(
    private val manager: WorkoutManager,
    private val nextRepetitionsRepository: NextRepetitionsRepository
) {
    fun start() {
        while (true) {
            println("Hello, I am your push-ups workout assistant!")
            val repetitions =
                nextRepetitionsRepository.read()
                    ?: Repetitions(5, 5)
            println("Now do ${repetitions.firstRound} push-ups")
            val firstRoundDone = getValidIntInput(
                "How many push-ups did you do?"
            )
            println("Now rest for 1 minute")
            println("Now do ${repetitions.secondRound} push-ups")
            val secondRoundDone = getValidIntInput(
                "How many push-ups did you do?"
            )
            println("Now rest for 1 minute")
            println("Now do as many push-ups as you can!")
            val thirdRoundDone = getValidIntInput(
                "How many push-ups did you do?"
            )
            val next = manager.nextRepetitions(
                firstRoundDone = firstRoundDone,
                secondRoundDone = secondRoundDone,
                thirdRoundDone = thirdRoundDone,
                repetitions = repetitions
            )
            nextRepetitionsRepository.write(next)
            println("Your next repetitions will be: ${next.firstRound} and ${next.secondRound}")
            println("Do you want another workout? (y/n)")
            val answer = readlnOrNull()?.trim()?.lowercase()
            if (answer != "y" && answer != "yes") {
                break
            }
        }
    }
    private fun getValidIntInput(prompt: String): Int {
        while (true) {
            println(prompt)
            val input = readlnOrNull()?.toIntOrNull()
            if (input != null && input >= 0) {
                return input
            }
            println("Please enter a valid number.")
        }
    }
}
fun main() {
    val interactor = WorkoutInteractor(
        manager = WorkoutManager(),
        nextRepetitionsRepository =
            FileNextRepetitionsRepository()
    )
    interactor.start()
}