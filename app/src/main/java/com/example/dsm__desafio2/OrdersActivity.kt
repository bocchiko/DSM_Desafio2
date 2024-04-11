package com.example.dsm__desafio2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton

class OrdersActivity : AppCompatActivity() {

    private lateinit var btnCreateOrder: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        btnCreateOrder = findViewById(R.id.btnCreateOrder)
        btnCreateOrder.setOnClickListener {
            val intent = Intent(this, AddOrderActivity::class.java)
            startActivity(intent)
        }
    }
}