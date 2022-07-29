package com.rmanzur.ec2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logButton = findViewById<Button>(R.id.btnLocation);
        val regButton = findViewById<Button>(R.id.btnLogoff)

        logButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
        regButton.setOnClickListener {
            Toast.makeText(this, "Registro no implementado", Toast.LENGTH_SHORT).show()
        }
    }
}