package com.example.dsm__desafio2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView

class OrderDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        val medicines = intent.getStringArrayListExtra("MEDICINES")
        val totalOrder = intent.getDoubleExtra("TOTAL_ORDER", 0.0)

        val medicinesWithTotal = ArrayList<String>()
        medicines?.let { medicinesList ->
            medicinesWithTotal.addAll(medicinesList)
        }
        medicinesWithTotal.add("Total: $totalOrder")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, medicinesWithTotal)
        val listViewMedicines = findViewById<ListView>(R.id.listViewMedicines)
        listViewMedicines.adapter = adapter

        val btnBackToOrder = findViewById<Button>(R.id.btnBackToOrder)
        btnBackToOrder.setOnClickListener {
            finish()
        }
    }
}