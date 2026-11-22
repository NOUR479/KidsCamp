package com.example.kidscamp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {    super.onCreate(savedInstanceState)

        com.example.kidscamp.data.KidsCampData.load(this)

        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnWorkshops)
            .setOnClickListener {
                startActivity(
                    Intent(this, WorkshopsActivity::class.java)
                )
            }

        findViewById<Button>(R.id.btnChildren)
            .setOnClickListener {
                startActivity(
                    Intent(this, ChildrenActivity::class.java)
                )
            }

        findViewById<Button>(R.id.btnInvoices)
            .setOnClickListener {
                startActivity(
                    Intent(this, InvoiceActivity::class.java)
                )
            }
    }
}