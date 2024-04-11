package com.example.dsm__desafio2.Models

import java.util.Date

open class Order {
    fun key(key: String?) {
    }

    var medicines: MutableList<Medicine>? = null
    var total: Double? = 0.0
    var date: Date? =  java.util.Date()
    var key: String? = null
    var per: MutableMap<String, Boolean> = HashMap()

    constructor() {}
    constructor(medicines: MutableList<Medicine>, total: Double?) {
        this.medicines = medicines
        this.total = total
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "medicines" to medicines,
            "total" to total,
            "date" to date,
            "key" to key,
            "per" to per
        )
    }
}