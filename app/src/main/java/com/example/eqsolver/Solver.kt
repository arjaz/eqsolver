package com.example.eqsolver

import kotlin.random.Random
import kotlin.math.abs

fun result(coefs: List<Int>, xs: List<Int>) = coefs.zip(xs) {a, x -> a * x}.sum()

fun delta(expected: Int, actual: Int) = abs(expected - actual)

fun possibility(curDelta: Int, sumDelta: Double): Double = (1.0 / curDelta.toDouble()) / sumDelta

fun crossover(d1: List<Int>, d2: List<Int>, threshold: Int): List<Int> =
    d1.mapIndexed{ index, it -> if (index <= threshold) it else d2[index] }

fun invertedSum(data: List<Int>): Double = data.map{ 1.0 / it.toDouble() }.sum()

// return index of the first element which returns true on given predicate or index of the last element
fun<T> firstTrue(d: List<T>, p: (T) -> Boolean): Int {
    d.forEachIndexed{ index, it -> if (p(it)) return index }
    return d.size - 1
}

// returns new List with mutation by offset on the given position
fun mutate(d: List<Int>, pos: Int, offset: Int) =
    d.mapIndexed{ index, it -> if (index == pos) it + offset else it }.toList()

fun mutatedChildren(mutationChance: Double, populationSize: Int, sectors: List<Double>, data: List<List<Int>>): List<List<Int>> {
    var newData = Array(data.size){ listOf(0) }
    data.forEachIndexed{ index, it -> newData[index] = it }
    for (i in 0 until populationSize) {
        val fatherChance = Random.nextDouble()
        val motherChance = Random.nextDouble()
        val fatherIndex = firstTrue(sectors) { x -> x >= fatherChance } - 1
        val motherIndex = firstTrue(sectors) { x -> x >= motherChance } - 1
        val father = data[fatherIndex]
        val mother = data[motherIndex]
        val threshold = Random.nextInt(0, populationSize - 1)
        val child = crossover(father, mother, threshold)
        if (Random.nextDouble() <= mutationChance) {
            val mutatedChild = mutate(child, Random.nextInt(0, populationSize - 1), Random.nextInt(-1, 1))
            newData[i] = mutatedChild
        } else {
            newData[i] = child
        }
    }
    return newData.toList()
}

fun iterativeAnswer(data: List<List<Int>>, coefs: List<Int>, answer: Int, mutationChance: Double, populationSize: Int, iteration: Int, maxIterations: Int): Pair<List<Int>, Int> {
    if (iteration >= maxIterations)
        return List(coefs.size){ 0 } to iteration
    val results = data.map{ result(coefs, it) }
    val deltas = results.map{ delta(answer, it) }
    deltas.forEachIndexed{ index, it -> if (it == 0) return data[index] to iteration }
    val possibilities = deltas.map{ possibility(it, invertedSum(deltas)) }
    val sectors = possibilities.mapIndexed{ index, _ -> possibilities.take(index).sum() }
    val newData = mutatedChildren(mutationChance, populationSize, sectors, data)
    return iterativeAnswer(newData, coefs, answer, mutationChance, populationSize, iteration + 1, maxIterations)
}

fun solve(answer: Int, coefs: List<Int>, mutationChance: Double, populationSize: Int, maxIterations: Int): Pair<List<Int>, Int> {
    val data = List(populationSize) { List(coefs.size) { Random.nextInt(0, answer / 2) } }
    return iterativeAnswer(data, coefs, answer, mutationChance, populationSize, 0, maxIterations)
}
