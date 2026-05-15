package com.example.manekelasa

data class JobRequest(
    val id: String = "",
    val seekerUserId: String = "",
    val seekerName: String = "",
    val skillNeeded: String = "",
    val area: String = "",
    val town: String = "",
    val description: String = "",
    val budgetPerDay: Int = 0,
    val active: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
