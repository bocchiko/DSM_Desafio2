package com.example.dsm__desafio2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.dsm__desafio2.Models.Medicine
import com.example.dsm__desafio2.Models.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.util.Date

class AddOrderActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    companion object {
        var database: FirebaseDatabase = FirebaseDatabase.getInstance()
        var refMedicine: DatabaseReference = database.getReference("medicines")
    }
    private var queryOrderBy: Query = refMedicine.orderByChild("name")
    private var medicines: MutableList<Medicine>? = null
    val checkMedicines: MutableList<Medicine> = ArrayList()
    private var medicineListView: ListView? = null

    private lateinit var btnSaveOrder: AppCompatButton
    private lateinit var btnCancelOrder: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_order)

        fetchMedicines()

        btnSaveOrder = findViewById(R.id.btnSaveOrder)
        btnCancelOrder = findViewById(R.id.btnCancelOrder)

        btnSaveOrder.setOnClickListener {
            saveOrder(FirebaseAuth.getInstance().currentUser)
        }

        btnCancelOrder.setOnClickListener {
            finish()
        }
    }

    private fun fetchMedicines() {
        queryOrderBy.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    medicines = ArrayList()
                    for (medSnapshot in dataSnapshot.children) {
                        val medicine: Medicine = medSnapshot.getValue(Medicine::class.java)!!
                        medicine?.key(medSnapshot.key)
                        if (medicine != null) {
                            medicines?.add(medicine)
                        }
                    }
                    listConfig(medicines as ArrayList<Medicine>)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@AddOrderActivity, "Error fetching medicines", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun listConfig(medicines: ArrayList<Medicine>) {
        medicineListView = findViewById(R.id.medicinesListView)
        val arrayAdapter: ArrayAdapter<Medicine> = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_multiple_choice, medicines)
        medicineListView?.adapter = arrayAdapter
        medicineListView?.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        medicineListView?.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val options: String = parent?.getItemAtPosition(position).toString()
        val medicine: Medicine? = parent?.getItemAtPosition(position) as? Medicine
        if (checkMedicines.contains(medicine)) {
            checkMedicines.remove(medicine)
        } else {
            if (medicine != null) {
                checkMedicines.add(medicine)
            }
        }
        Log.d("CheckMedicines", checkMedicines.toString())
        Toast.makeText(this, "Click $options", Toast.LENGTH_SHORT).show()
    }

    private fun saveOrder(currentUser: FirebaseUser?) {
        currentUser?.let { user ->
            var total: Double = 0.0
            for (medicine in checkMedicines) {
                total += medicine.price ?: 0.0
            }

            val order = Order(checkMedicines, total, user.uid)
            val refOrder = database.getReference("orders").child(user.uid)
            val key = refOrder.push().key

            order.key(key)
            refOrder.child(key!!).setValue(order.toMap()).addOnSuccessListener {
                Toast.makeText(this, "Orden guardada", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, OrdersActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Error al guardar la orden", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show()
        }
    }
}