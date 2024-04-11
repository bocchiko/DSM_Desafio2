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

                    orderListView.setOnItemClickListener { parent, view, position, id ->
                        val selectedOrderId = snapshot.children.elementAtOrNull(position)?.key
                        selectedOrderId?.let { orderId ->
                            val orderSnapshot = snapshot.child(orderId)
                            val medicinesSnapshot = orderSnapshot.child("medicines")
                            val medicinesList = mutableListOf<String>()
                            var totalOrder = 0.0
                            for (medicineSnapshot in medicinesSnapshot.children) {
                                val name = medicineSnapshot.child("name").getValue(String::class.java)
                                val price = medicineSnapshot.child("price").getValue(Double::class.java)
                                if (name != null && price != null) {
                                    medicinesList.add("$name - Precio: $price")
                                    totalOrder += price
                                }
                            }

                            // Obtener el total de la orden
                            val total = orderSnapshot.child("total").getValue(Double::class.java)
                            if (total != null) {
                                totalOrder = total
                            }

                            // Crear un intent para iniciar la actividad OrderDetailActivity
                            val intent = Intent(this@OrdersActivity, OrderDetailActivity::class.java)

                            // Puedes pasar la lista de medicamentos y el total de la orden a OrderDetailActivity para mostrarlos en la pantalla de detalles de la orden
                            intent.putStringArrayListExtra("MEDICINES", ArrayList(medicinesList))
                            intent.putExtra("TOTAL_ORDER", totalOrder)

                            // Iniciar la actividad OrderDetailActivity
                            startActivity(intent)
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