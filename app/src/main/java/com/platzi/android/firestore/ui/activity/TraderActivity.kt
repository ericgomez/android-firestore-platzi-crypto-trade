package com.platzi.android.firestore.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.android.firestore.R
import com.platzi.android.firestore.adapter.CryptosAdapter
import com.platzi.android.firestore.adapter.CryptosAdapterListener
import com.platzi.android.firestore.model.Crypto
import com.platzi.android.firestore.network.Callback
import com.platzi.android.firestore.network.FirestoreService
import kotlinx.android.synthetic.main.activity_trader.*
import java.lang.Exception


/**
 * @author Santiago Carrillo
 * 2/14/19.
 */
class TraderActivity : AppCompatActivity(), CryptosAdapterListener {

    //Creamos una instancia a nuestro Firestore Service
    lateinit var firestoreService: FirestoreService
    //Creamos una instancia del CryptosAdapter y le asignamos una nueva instancia
    private val cryptosAdapter: CryptosAdapter = CryptosAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trader)
        //inicializamos nuestra instacia de FirebaseFirestore en firestoreService
        firestoreService = FirestoreService(FirebaseFirestore.getInstance())

        configureRecyclerView()

        //Realizamos la carga de las crypto monedas
        loadCryptos()

        fab.setOnClickListener { view ->
            Snackbar.make(view, getString(R.string.generating_new_cryptos), Snackbar.LENGTH_SHORT)
                .setAction("Info", null).show()
        }

    }

    private fun loadCryptos() {
        firestoreService.getCryptos(object: Callback<List<Crypto>>{
            override fun onSuccess(result: List<Crypto>?) {
                //Realiza el cambio de contexto para actualizar la interfas grafica
                this@TraderActivity.runOnUiThread{//Actualizamos la interfaz de manera auntomatica con runOnUiThread
                    //Actualizamos la lista de crypto monedas del CryptosAdapter
                    cryptosAdapter.cryptoList = result!!
                    //Llamamos a un metodo del adaptador que forza la pintada de los valores de las crypto monedas en la vista
                    cryptosAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailed(exception: Exception) {

            }

        })
    }

    private fun configureRecyclerView() {
        //Accedemos al recyclerView directamente del XML
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager //Asignamos el layoutManager que acabamos de crear al recyclerView
        recyclerView.adapter = cryptosAdapter //Asignamos el cryptosAdapter al recyclerView
    }

    fun showGeneralServerErrorMessage() {
        Snackbar.make(fab, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
            .setAction("Info", null).show()
    }
    //Esta funciona va a entrar una vez le demos click a la compra de Crypto monedas
    override fun onBuyCryptoClicked(crypto: Crypto) {
        TODO("Not yet implemented")
    }
}