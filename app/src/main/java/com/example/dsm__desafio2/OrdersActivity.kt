package com.example.dsm__desafio2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.util.*
import java.text.SimpleDateFormat

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

        loadUserOrders()

    }

    private fun loadUserOrders() {
        val orderListView: ListView = findViewById(R.id.OrdersListView)
        val ordersAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        orderListView.adapter = ordersAdapter

        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val ordersRef: DatabaseReference = database.getReference("orders").child(user.uid)

            ordersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    ordersAdapter.clear()
                    for (orderSnapshot in snapshot.children) {
                        val dateMap = orderSnapshot.child("date").getValue() as? Map<*, *>
                        val total = orderSnapshot.child("total").getValue(Double::class.java)

                        if (dateMap != null && total != null) {
                            val calendar = Calendar.getInstance()
                            calendar.set(
                                dateMap["year"].toString().toInt(),
                                dateMap["month"].toString().toInt(),
                                dateMap["day"].toString().toInt(),
                                dateMap["hours"].toString().toInt(),
                                dateMap["minutes"].toString().toInt(),
                                dateMap["seconds"].toString().toInt()
                            )
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                            val dateString = dateFormat.format(calendar.time)
                            ordersAdapter.add("Fecha: $dateString - Total: $total")
                        } else {
                            ordersAdapter.add("Orden sin fecha o total")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "Error al leer la base de datos: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}