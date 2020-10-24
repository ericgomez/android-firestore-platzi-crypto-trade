package com.platzi.android.firestore.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.android.firestore.R
import com.platzi.android.firestore.model.User
import com.platzi.android.firestore.network.Callback
import com.platzi.android.firestore.network.FirestoreService
import com.platzi.android.firestore.network.USERS_COLLECTION_NAME
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception

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

    //Creamos una referencia al servicio creado en network
    lateinit var firestoreService: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Intaciamos la intacia de FirebaseFirestore en FirestoreService mismo que lo pasamos como constructor
        firestoreService = FirestoreService(FirebaseFirestore.getInstance())
    }


    fun onStartClicked(view: View) {
        view.isEnabled = false //Desabilitamos el boton OnStart con isEnabled
        auth.signInAnonymously()
            //NOTA: Habilitar en FIREBASE la Authentication - Anónimo
            //NOTA: Habilitar en FIREBASE en Rules los permios de escritura allow read, write: if true;
            //Agregamos un listener(addOnCompleteListener) que nos estara notificando si esta operacion fue exitosa o no
            .addOnCompleteListener {task ->//Retorna una tarea si es exitosa o no
                if (task.isSuccessful) {
                   val username = username.text.toString()//obtenemos el valor del username

                   val user = User()
                    user.username = username

                    //Lo enviamos al mainActivity el username
                    saveUserAndStartMainActivity(user, view)//La vista es para utilizarla en el showErrorMessage que es lo que recibe
                } else {//En caso de que no sea exitosa la conexion
                    showErrorMessage(view)//Mostramos un mensaje de error en caso de que caiga en este supuesto
                    //En caso de que ocurra un error habilitamos el boton
                    view.isEnabled = true
                }
            }

    }

    private fun saveUserAndStartMainActivity(user: User, view: View) {
        firestoreService.setDocument(user, USERS_COLLECTION_NAME, user.username,
            object : Callback<Void> {//Nos muestra dos ecenarios
                override fun onSuccess(result: Void?) {//En caso de que se a exitoso
                    startMainActivity(user.username)//comenzamos la activadad principal
                }

                override fun onFailed(exception: Exception) {//En caso de que exista un error
                    showErrorMessage(view)//Mostramos un mensaje de error
                    Log.e(TAG, "error", exception)
                    view.isEnabled = true //Habilitamos el boton OnStart con isEnabled
                }

            })
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
