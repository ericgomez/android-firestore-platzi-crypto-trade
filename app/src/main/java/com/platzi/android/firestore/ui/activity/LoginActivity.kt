package com.platzi.android.firestore.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.platzi.android.firestore.R
import kotlinx.android.synthetic.main.activity_trader.*

/**
 * @author Santiago Carrillo
 * github sancarbar
 * 1/29/19.
 */


const val USERNAME_KEY = "username_key"

class LoginActivity : AppCompatActivity() {


    private val TAG = "LoginActivity"

    //Creamos la instancia del modulo de autenticacion de firebase FirebaseAuth
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }


    fun onStartClicked(view: View) {
        auth.signInAnonymously()
            //NOTA: Habilitar en FIREBASE la Authentication - Anónimo
            //Agregamos un listener(addOnCompleteListener) que nos estara notificando si esta operacion fue exitosa o no
            .addOnCompleteListener {task ->//Retorna una tarea si es exitosa o no
                if (task.isSuccessful) {
                   val username = usernameTextView.text.toString()//obtenemos el valor del username
                    //Lo enviamos al mainActivity el username
                    startMainActivity(username)
                } else {//En caso de que no sea exitosa la conexion
                    showErrorMessage(view)//Mostramos un mensaje de error en caso de que caiga en este supuesto
                }
            }
        startMainActivity("Santiago")

    }

    private fun showErrorMessage(view: View) {
        Snackbar.make(view, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
            .setAction("Info", null).show()
    }

    private fun startMainActivity(username: String) {
        val intent = Intent(this@LoginActivity, TraderActivity::class.java)
        intent.putExtra(USERNAME_KEY, username)
        startActivity(intent)
        finish()
    }

}
