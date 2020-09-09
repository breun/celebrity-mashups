import java.io.File

// Models

data class Mashup(
    val mashupName: String,
    val overlap: Int,
    val originalNames: Pair<String, String>
)

// Functions

fun readNamesFromTxtFiles(path: String): List<String> =
    File(path)
        .listFiles { _, name -> name.endsWith(".txt") }
        .flatMap { it.readLines() }

fun calculateMashups(names: List<String>, minimumOverlap: Int): List<Mashup> =
    cartesianProduct(names, names)
        .filterNot { it.first == it.second }
        .mapNotNull { findBestMashup(it, minimumOverlap) }

fun <T> cartesianProduct(list1: Iterable<T>, list2: Iterable<T>): Iterable<Pair<T, T>> =
    list1.flatMap { first -> list2.map { second -> first to second } }

fun findBestMashup(originalNames: Pair<String, String>, minimumOverlap: Int): Mashup? {
    val first = originalNames.first.toLowerCase()
    val second = originalNames.second.toLowerCase()

    // Maximum overlap cannot be more than the minimum length of the strings.
    // Subtract one from that minimum to avoid mashups where one name completely contains the other, because those aren't funny.
    val maxOverlap = minOf(first.length, second.length) - 1

    var overlap = maxOverlap
    while (overlap >= minimumOverlap) {
        val overlapString = second.take(overlap)
        if (first.endsWith(overlapString)) {
            return Mashup(
                mashupName = originalNames.first + originalNames.second.drop(overlap),
                originalNames = originalNames,
                overlap = overlap
            )
        }
        overlap--
    }

    return null
}

fun printMashups(mashups: List<Mashup>) {
    mashups.forEach { println("${it.mashupName} ${it.originalNames} [${it.overlap}]") }
    println("")
    println("Found ${mashups.size} celebrity mashups")
}

// Action!

val names = readNamesFromTxtFiles(path = ".")
val mashups = calculateMashups(names, minimumOverlap = 3) // Really short overlaps don't make for good mashups

printMashups(mashups.sortedBy { it.mashupName })