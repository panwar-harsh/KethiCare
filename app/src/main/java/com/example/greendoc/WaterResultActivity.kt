package com.example.greendoc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greendoc.databinding.ActivityWaterResultBinding

class WaterResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWaterResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaterResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val amount = intent.getDoubleExtra(EXTRA_AMOUNT, 0.0)
        binding.waterAmountText.text = "Recommended Water: %.2f ml".format(amount)
    }

    companion object {
        private const val EXTRA_AMOUNT = "extra_amount"

        fun newIntent(context: Context, amount: Double): Intent {
            val intent = Intent(context, WaterResultActivity::class.java)
            intent.putExtra(EXTRA_AMOUNT, amount)
            return intent
        }
    }
}
