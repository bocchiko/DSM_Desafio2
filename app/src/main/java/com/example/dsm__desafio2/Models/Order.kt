package com.example.dsm__desafio2.Models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

open class Order {
    fun key(key: String?) {
    }

    var medicines: MutableList<Medicine>? = null
    var total: Double? = 0.0
    var date: String? = getCurrentDate()
    var key: String? = null
    var userId: String? = null
    var per: MutableMap<String, Boolean> = HashMap()

    constructor() {}
    constructor(medicines: MutableList<Medicine>, total: Double?, userId: String?) {
        this.medicines = medicines
        this.total = total
        this.userId = userId
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "medicines" to medicines,
            "total" to total,
            "date" to date,
            "key" to key,
            "per" to per,
            "userId" to userId
        )
    }

    open fun getCurrentDate(): String? {
        val sdf = SimpleDateFormat("yyyy MMM dd hh:mm a")
        val date = Date()
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-6:00"))
        return sdf.format(date)
    }

}