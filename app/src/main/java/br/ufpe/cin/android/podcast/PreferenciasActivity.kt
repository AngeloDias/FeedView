package br.ufpe.cin.android.podcast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PreferenciasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferencias)
        //Após criar o fragmento, use o código abaixo para exibir
//        supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.preferencias,PrefsFragment())
//                .commit()
    }
}