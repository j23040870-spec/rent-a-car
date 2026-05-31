package com.brianteng.rentacar.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import com.brianteng.rentacar.R
import com.brianteng.rentacar.data.CarRepository
import com.brianteng.rentacar.databinding.ActivityMainBinding
import com.brianteng.rentacar.model.Car

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val repo = CarRepository

    private var currentIndex = 0
    private var displayedCars: MutableList<Car> = repo.availableCars
    private var searchQuery = ""
    private var isRestoringState = false   // <-- NEW

    private val rentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val carId = result.data?.getIntExtra("carId", -1) ?: -1
            val totalCost = result.data?.getIntExtra("totalCost", 0) ?: 0
            if (carId != -1) {
                repo.rentCar(carId, totalCost)
                updateBalance()
                updateDisplayedCars()
                Toast.makeText(this, "Car booked successfully!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupDarkModeToggle()
        setupSearch()
        setupListeners()

        // Restore saved state
        if (savedInstanceState != null) {
            isRestoringState = true
            searchQuery = savedInstanceState.getString("searchQuery", "")
            currentIndex = savedInstanceState.getInt("currentIndex", 0)
        }

        // Do NOT reset index when restoring
        updateDisplayedCars(searchQuery, resetIndex = false)
        updateBalance()

        // Now restore the search text (listeners will ignore because isRestoringState is true)
        if (isRestoringState) {
            binding.searchView.setQuery(searchQuery, false)
            isRestoringState = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentIndex", currentIndex)
        outState.putString("searchQuery", binding.searchView.query?.toString() ?: searchQuery)
    }

    private fun setupDarkModeToggle() {
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    private fun updateBalance() {
        binding.tvBalance.text = "Credit: ${repo.creditBalance}"
    }

    private fun updateDisplayedCars(query: String = "", resetIndex: Boolean = true) {
        displayedCars = repo.availableCars.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.model.contains(query, ignoreCase = true)
        }.toMutableList()
        if (resetIndex) currentIndex = 0
        if (displayedCars.isEmpty()) {
            clearCarDisplay()
            Toast.makeText(this, "No cars match", Toast.LENGTH_SHORT).show()
        } else {
            if (currentIndex >= displayedCars.size) currentIndex = 0
            showCar(displayedCars[currentIndex])
        }
        updateFavorites()
    }

    private fun showCar(car: Car) {
        binding.ivCar.setImageResource(car.imageResId)
        binding.tvNameModel.text = "${car.name} ${car.model}"
        binding.tvYear.text = "Year: ${car.year}"
        binding.ratingBar.rating = car.rating
        binding.tvKilometers.text = "Km: ${car.kilometers}"
        binding.tvDailyCost.text = "${car.dailyCost} credits/day"
        updateHeartIcon(car)
    }

    private fun clearCarDisplay() {
        binding.ivCar.setImageResource(0)
        binding.tvNameModel.text = ""
        binding.tvYear.text = ""
        binding.ratingBar.rating = 0f
        binding.tvKilometers.text = ""
        binding.tvDailyCost.text = ""
    }

    private fun updateHeartIcon(car: Car) {
        binding.btnFavorite.setImageResource(
            if (repo.favoriteIds.contains(car.id)) R.drawable.ic_heart_filled
            else R.drawable.ic_heart_outline
        )
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (isRestoringState) return true
                updateDisplayedCars(query ?: "", resetIndex = true)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (isRestoringState) return true
                updateDisplayedCars(newText ?: "", resetIndex = true)
                return true
            }
        })
    }

    private fun setupListeners() {
        binding.btnNext.setOnClickListener {
            if (displayedCars.isNotEmpty()) {
                currentIndex = (currentIndex + 1) % displayedCars.size
                showCar(displayedCars[currentIndex])
            }
        }
        binding.btnPrevious.setOnClickListener {
            if (displayedCars.isNotEmpty()) {
                currentIndex = if (currentIndex == 0) displayedCars.size - 1 else currentIndex - 1
                showCar(displayedCars[currentIndex])
            }
        }
        binding.btnRent.setOnClickListener {
            if (displayedCars.isEmpty()) return@setOnClickListener
            val car = displayedCars[currentIndex]
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("car", car)
                putExtra("creditBalance", repo.creditBalance)
            }
            rentLauncher.launch(intent)
        }
        binding.btnFavorite.setOnClickListener { toggleFavorite() }
        binding.ivCar.setOnLongClickListener { toggleFavorite(); true }
        binding.btnSort.setOnClickListener { showSortMenu() }
    }

    private fun showSortMenu() {
        val popup = PopupMenu(this, binding.btnSort)
        popup.menuInflater.inflate(R.menu.sort_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.sort_rating -> {
                    repo.sortByRating()
                    updateDisplayedCars(binding.searchView.query?.toString() ?: "")
                    true
                }
                R.id.sort_year -> {
                    repo.sortByYear()
                    updateDisplayedCars(binding.searchView.query?.toString() ?: "")
                    true
                }
                R.id.sort_cost -> {
                    repo.sortByCost()
                    updateDisplayedCars(binding.searchView.query?.toString() ?: "")
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun toggleFavorite() {
        if (displayedCars.isEmpty()) return
        val car = displayedCars[currentIndex]
        if (repo.favoriteIds.contains(car.id)) {
            repo.favoriteIds.remove(car.id)
        } else {
            repo.favoriteIds.add(car.id)
        }
        updateHeartIcon(car)
        updateFavorites()
    }

    private fun updateFavorites() {
        val favContainer = binding.favoritesContainer
        val favList = binding.favoritesList
        favList.removeAllViews()
        val favCars = repo.availableCars.filter { repo.favoriteIds.contains(it.id) }
        if (favCars.isEmpty()) {
            favContainer.visibility = android.view.View.GONE
        } else {
            favContainer.visibility = android.view.View.VISIBLE
            favCars.forEach { car ->
                val img = ImageView(this).apply {
                    setImageResource(car.imageResId)
                    layoutParams = LinearLayout.LayoutParams(120, 120).apply { marginEnd = 8 }
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setOnClickListener {
                        val pos = displayedCars.indexOfFirst { it.id == car.id }
                        if (pos != -1) {
                            currentIndex = pos
                            showCar(displayedCars[currentIndex])
                        }
                    }
                    setOnLongClickListener {
                        repo.favoriteIds.remove(car.id)
                        updateFavorites()
                        true
                    }
                }
                favList.addView(img)
            }
        }
    }
}