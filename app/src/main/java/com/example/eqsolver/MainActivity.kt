package com.example.eqsolver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onSolveClick(view: View) {
        if (coefANumber.text.isEmpty() || coefBNumber.text.isEmpty() || coefCNumber.text.isEmpty() || coefDNumber.text.isEmpty())
            return
        if (resultNumber.text.isEmpty())
            return
        if (iterationNumber.text.isEmpty() || mutationNumber.text.isEmpty() || populationNumber.text.isEmpty())
            return

        val coefs = listOf(
            coefANumber.text.toString().toInt(),
            coefBNumber.text.toString().toInt(),
            coefCNumber.text.toString().toInt(),
            coefDNumber.text.toString().toInt()
        )
        val y = resultNumber.text.toString().toInt()
        val populationSize = populationNumber.text.toString().toInt()
        val mutationChance = mutationNumber.text.toString().toDouble()
        val maxIterations = iterationNumber.text.toString().toInt()

        if (mutationChance > 1 || mutationChance < 0)
            return

        val (results, iterations) = solve(y, coefs, mutationChance, populationSize, maxIterations)
        if (iterations >= maxIterations) {
            resultView.text = "The equation wasn't solved"
            iterationsView.text = ""
        } else {
            resultView.text = results.toString()
            iterationsView.text = iterations.toString()
        }
    }
}
