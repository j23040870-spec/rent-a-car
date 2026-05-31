package com.brianteng.rentacar.data

import com.brianteng.rentacar.model.Car
import com.brianteng.rentacar.R

object CarRepository {

    // ---------------------------------------------------
    // Base data (never modified directly by sort/search)
    // ---------------------------------------------------
    private val allCars = listOf(
        Car(1, "Toyota", "Camry", 2022, 4.5f, 35000, 80, R.drawable.car_camry),
        Car(2, "Honda", "Civic", 2021, 4.2f, 28000, 70, R.drawable.car_civic),
        Car(3, "Ford", "Focus", 2023, 4.7f, 12000, 90, R.drawable.car_focus),
        Car(4, "BMW", "X5", 2020, 4.0f, 60000, 110, R.drawable.car_x5),
        Car(5, "Audi", "A4", 2022, 4.8f, 22000, 100, R.drawable.car_a4)
    )

    // ---------------------------------------------------
    // Mutable state (available cars, balance, favorites)
    // ---------------------------------------------------
    val availableCars: MutableList<Car> = allCars.toMutableList()

    var creditBalance: Int = 500
        private set

    val favoriteIds: MutableSet<Int> = mutableSetOf()

    // ---------------------------------------------------
    // Rental logic
    // ---------------------------------------------------
    fun rentCar(carId: Int, totalCost: Int): Boolean {
        if (totalCost > creditBalance) return false
        creditBalance -= totalCost
        availableCars.removeAll { it.id == carId }
        return true
    }

    fun restoreCar(car: Car) {
        if (availableCars.none { it.id == car.id }) {
            availableCars.add(car)
        }
    }

    // ---------------------------------------------------
    // Sorting (in‑place on availableCars without changing allCars)
    // ---------------------------------------------------
    fun sortByRating() {
        availableCars.sortByDescending { it.rating }
    }


    fun sortByYear() {
        availableCars.sortByDescending { it.year }
    }

    fun sortByCost() {
        availableCars.sortBy { it.dailyCost }
    }

    // For testing only – resets all state to initial values
    fun resetForTest() {
        availableCars.clear()
        availableCars.addAll(allCars)
        creditBalance = 500
        favoriteIds.clear()
    }
}