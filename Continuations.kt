fun main() {
    val numbers = sequence {
        println("one")
        yield(1)

        println("two")
        yield(2)

        println("three")
        yield(3)

        println("...done")
    }

    for (n in numbers) {
        println("number = $n")
    }
}
