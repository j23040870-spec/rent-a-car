package com.brianteng.rentacar.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.brianteng.rentacar.databinding.ActivityDetailBinding
import com.brianteng.rentacar.model.Car

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var car: Car
    private var creditBalance = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Safe extraction of Parcelable extra across all API levels
        car = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("car", Car::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("car")
        } ?: return finish()

        creditBalance = intent.getIntExtra("creditBalance", 0)

        displayCarDetails()
        setupSlider()
        setupButtons()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setResult(Activity.RESULT_CANCELED)
                Toast.makeText(this@DetailActivity, "Booking cancelled", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun displayCarDetails() {
        binding.ivCarDetail.setImageResource(car.imageResId)
        binding.tvDetailNameModel.text = "${car.name} ${car.model}"
        binding.tvDetailYear.text = "Year: ${car.year}"
        binding.rbDetail.rating = car.rating
        binding.tvDetailDailyCost.text = "Daily cost: ${car.dailyCost} credits"
        updateTotalCost(1)
    }

    private fun setupSlider() {
        binding.sliderDays.addOnChangeListener { _, value, _ ->
            updateTotalCost(value.toInt())
        }
    }

    private fun updateTotalCost(days: Int) {
        val total = car.dailyCost * days
        binding.tvTotalCost.text = "Total: $total credits"
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            val days = binding.sliderDays.value.toInt()
            val totalCost = car.dailyCost * days

            if (totalCost > 400) {
                Toast.makeText(this, "Maximum rental cost is 400 credits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (totalCost > creditBalance) {
                Toast.makeText(this, "Insufficient credit balance", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultIntent = Intent().apply {
                putExtra("carId", car.id)
                putExtra("totalCost", totalCost)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            Toast.makeText(this, "Booking confirmed", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}