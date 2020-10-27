package com.platzi.android.firestore.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.android.firestore.R
import com.platzi.android.firestore.adapter.CryptosAdapter
import com.platzi.android.firestore.adapter.CryptosAdapterListener
import com.platzi.android.firestore.model.Crypto
import com.platzi.android.firestore.model.User
import com.platzi.android.firestore.network.Callback
import com.platzi.android.firestore.network.FirestoreService
import com.platzi.android.firestore.network.RealtimeDataListener
import com.squareup.picasso.Picasso
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
    //Creamos una variable username que toma el valor de nulo
    private var username: String? = null
    //Creamos una variable gobal para asignar a ese usuario
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trader)
        //inicializamos nuestra instacia de FirebaseFirestore en firestoreService
        firestoreService = FirestoreService(FirebaseFirestore.getInstance())

        username = intent.extras!![USERNAME_KEY]!!.toString()
        //Cuando tengamos el nombre de usuario lo podemos asignar
        usernameTextView.text = username

        configureRecyclerView()

        //Realizamos la carga de las crypto monedas
        loadCryptos()

        fab.setOnClickListener { view -> //Funcion el boton flotante
            Snackbar.make(view, getString(R.string.generating_new_cryptos), Snackbar.LENGTH_SHORT)
                .setAction("Info", null).show()
            //Funcion para generar crypto monedas aleatoreamente
            generateCryptoCurrenciesRandom()
        }

    }

    //Funcion para generar crypto monedas aleatoreamente
    private fun generateCryptoCurrenciesRandom() {
        for (crypto in cryptosAdapter.cryptoList) {
            val amount = (1..10).random()//Generamos un valor random propia de Kotlin
            crypto.available += amount//Actualizamos la cantidad de monedas disponibles de esta crypto moneda
            //Actualizamos en nuestro modelo de datos
            firestoreService.updateCrypto(crypto)
        }
    }

    private fun loadCryptos() {
        firestoreService.getCryptos(object: Callback<List<Crypto>>{
            override fun onSuccess(cryptoList: List<Crypto>?) {
                //Una vez es exitosa la carga de crypto monedas Buscamos el usaurio si existe
                firestoreService.findUserById(username!!, object: Callback<User>{
                    override fun onSuccess(result: User?) {
                        user = result//le asignamos este resultado a la varible user
                        //validamos que este usuario tenga una lista de crypto monedas
                        if (user!!.cryptosList == null) {//si el usuario el igual a nulo es No tiene lista de cryptomoneda
                            //Iteramos la lista del servidor
                            val userCryptoList = mutableListOf<Crypto>()//Creamos una lista mutable de tipo Crypto

                            for (crypto in cryptoList!!){
                                val cryptoUser = Crypto()
                                //Seteamos cada valor deacuerdo a lo que viene en esta lista
                                cryptoUser.name = crypto.name
                                cryptoUser.available = crypto.available
                                cryptoUser.imageUrl = crypto.imageUrl
                                userCryptoList.add(cryptoUser)//Agregamos el elemento a la lista
                            }
                            user!!.cryptosList = userCryptoList
                            //Enviamos nuestra data al servidor utilizando el metodo que creamos anteriormente
                            firestoreService.updateUser(user!!, null)
                        }
                        //Funcion que carga la lista de crypto monedas en el panel de informacion
                        loadUserCryptos()

                        addRealtimeDatabaseListeners(user!!, cryptoList!!)

                    }

                    override fun onFailed(exception: Exception) {
                        showGeneralServerErrorMessage()
                    }

                })

                //Realiza el cambio de contexto para actualizar la interfas grafica
                this@TraderActivity.runOnUiThread{//Actualizamos la interfaz de manera auntomatica con runOnUiThread
                    //Actualizamos la lista de crypto monedas del CryptosAdapter
                    cryptosAdapter.cryptoList = cryptoList!!
                    //Llamamos a un metodo del adaptador que forza la pintada de los valores de las crypto monedas en la vista
                    cryptosAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailed(exception: Exception) {
                //En caso de existir un error mostramos un Log
                Log.e("TraderActivity", "Error loading cryptos", exception)
                showGeneralServerErrorMessage()
            }

        })
    }

    private fun addRealtimeDatabaseListeners(user: User, cryptosList: List<Crypto>) {
        firestoreService.listenForUpdates(user, object : RealtimeDataListener<User>{
            override fun onDataChange(updatedData: User) {
                this@TraderActivity.user = updatedData
                loadUserCryptos()
            }

            override fun onError(exception: Exception) {
                showGeneralServerErrorMessage()
            }
        })

        firestoreService.listenForUpdates(cryptosList, object : RealtimeDataListener<Crypto>{
            override fun onDataChange(updateData: Crypto) {
                var pos = 0 //Variable para ver la poscion que tiene la crypto moneda en el arreglo
                for (crypto in cryptosAdapter.cryptoList) {//Realizamos el casteo de la lista
                    if (crypto.name.equals(updateData.name)) {//Buscamos la moneda que estamos actualizando
                        crypto.available = updateData.available
                        //Le notificamos al adaptador que actualize el item para la posicion especifica
                        cryptosAdapter.notifyItemChanged(pos)//Actualiza unicamente el item que cambia por medio del id
                    }
                    pos++
                }
            }

            override fun onError(exception: Exception) {
                showGeneralServerErrorMessage()
            }

        })
    }

    private fun loadUserCryptos() {
        //Realiza el cambio de contexto para actualizar la interfas grafica
        runOnUiThread {//Actualizamos la interfaz de manera auntomatica con runOnUiThread
            if (user != null && user!!.cryptosList != null) {
                infoPanel.removeAllViews()//Borramos todas las vistas que tenga este contenedor con removeAllViews
                //Iteramos las lista de crypto monedas
                for (crypto in user!!.cryptosList!!) {
                    addUserCryptoInfoRow(crypto)
                }
            }
        }
    }

    private fun addUserCryptoInfoRow(crypto: Crypto) {
        //Inflamos el layoud
        //Cargamos nuestra vista
        val view = LayoutInflater.from(this).inflate(R.layout.coin_info, infoPanel, false)
        view.findViewById<TextView>(R.id.coinLabel).text =
            getString(R.string.coin_info, crypto.name, crypto.available.toString())
        Picasso.get().load(crypto.imageUrl).into(view.findViewById<ImageView>(R.id.coinIcon))
        //La agregamos al contenedor para poder visualizarla
        infoPanel.addView(view)
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
        //Verificamos que la crypto moneda de la cual vamos arealizar la compra tenga un saldo suficiente
        if (crypto.available > 0) {
            for (userCrypto in user!!.cryptosList!!) {
                if (userCrypto.name == crypto.name) {//buscamos en la lista si la crypto moneda existe
                    userCrypto.available += 1//si la encuentra incrementa 1
                    break//nos salimos del ciclo
                }
            }
            //Disminuimos la cantidad de Crypto monedas disponibles de esta Crypto moneda que estamo comprando
            crypto.available--

            firestoreService.updateUser(user!!, null)//Actauzlizamos el usuario directamente en Firestore
            firestoreService.updateCrypto(crypto)//Actauzlizamos la crypto moneda directamente en Firestore
        }
    }
}