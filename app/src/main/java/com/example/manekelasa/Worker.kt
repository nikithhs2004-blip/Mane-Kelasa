package com.example.manekelasa

data class Worker(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val skill: String = "",
    val phone: String = "",
    val area: String = "",
    val town: String = "",
    val dailyRate: Int = 0,
    val available: Boolean = false,
    val thumbsUpCount: Int = 0,
    val role: String = "worker"
)
