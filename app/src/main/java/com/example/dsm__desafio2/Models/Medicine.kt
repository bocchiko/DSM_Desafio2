package com.example.dsm__desafio2.Models


class Medicine{
    fun key(key: String?) {
    }

    var id: Int? = null
    var name: String? = null
    var price: Double? = 0.0
    var key: String? = null
    var per: MutableMap<String, Boolean> = HashMap()

    constructor() {}
    constructor(id: Int?, name: String?, price: Double?) {
        this.id = id
        this.name = name
        this.price = price
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "price" to price,
            "key" to key,
            "per" to per
        )
    }

    override fun toString(): String {
        return "$name, Precio=$price"
    }
}
