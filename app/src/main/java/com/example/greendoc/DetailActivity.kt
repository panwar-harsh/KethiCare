package com.example.greendoc

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val predictedIndex = intent.getIntExtra("predictedIndex", -1)
        val confidence = intent.getFloatExtra("confidence", 0.0f)

        val diseaseNames = loadDiseaseNamesFromFile()

        val diseaseTitle = findViewById<TextView>(R.id.diseaseTitle)
        val confidenceText = findViewById<TextView>(R.id.confidenceText)

        diseaseTitle.text = if (predictedIndex in diseaseNames.indices) diseaseNames[predictedIndex] else "Unknown Disease"
        confidenceText.text = "Confidence: %.2f%%".format(confidence)
    }

    private fun loadDiseaseNamesFromFile(): List<String> {
        val inputStream = assets.open("disease_labels.txt")
        val reader = BufferedReader(InputStreamReader(inputStream))
        return reader.readLines()
    }
}
