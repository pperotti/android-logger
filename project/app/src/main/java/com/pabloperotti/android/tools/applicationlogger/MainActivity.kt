package com.pabloperotti.android.tools.applicationlogger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pabloperotti.android.tools.log.client.ToasterMessage

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ToasterMessage.show(this, "Hola!!!!")
    }
}