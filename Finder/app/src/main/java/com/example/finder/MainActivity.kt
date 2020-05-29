package com.example.finder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.example.finder.Remote.CustomAdapter

class MainActivity : AppCompatActivity() {

    val elements = arrayOf("Restaurants","Bars","Musées","SuperMarché","Hopital","Magasins")
    val img = arrayOf(R.drawable.restaurant, R.drawable.bar, R.drawable.musee, R.drawable.supermarche, R.drawable.hopital, R.drawable.magasin)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = CustomAdapter(this, elements, img)
        val listView: ListView = findViewById(R.id.listView_1)
        listView.setAdapter(adapter)

        listView.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long){
                val itemValue = listView.getItemAtPosition(position) as String //ERROR IL FAUT DIRE SI ON CLIQUE SUR LIMAGE OU SUR LE TEXT CE QUIL SE PASSE
                var choix: String?= null
                when(itemValue)
                {
                    "Restaurants" -> choix = "restaurant"
                    "Bars" -> choix = "bar"
                    "Musées" -> choix = "museum"
                    "SuperMarché" -> choix = "supermarket"
                    "Hopital" -> choix = "hospital"
                    "Magasins" -> choix = "shopping_mall"
                }
                //Toast.makeText(applicationContext, "Position: $position \n Item Value : $itemValue", Toast.LENGTH_LONG).show()
                val intent = Intent(this@MainActivity, MapActivity::class.java)
                intent.putExtra("CHOIX", choix)
                startActivity(intent)
            }
        }

    }
}
