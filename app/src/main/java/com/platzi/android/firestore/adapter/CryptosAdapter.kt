package com.platzi.android.firestore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.platzi.android.firestore.R
import com.platzi.android.firestore.model.Crypto
import com.squareup.picasso.Picasso

class CryptosAdapter(val cryptosAdapterListener: CryptosAdapterListener): RecyclerView.Adapter<CryptosAdapter.ViewHolder>() {

    //Creamos una lista de Cryptomonedas
    var cryptoList: List<Crypto> = ArrayList()

    //Nos permitira realizar las actualizaciones de los componentes
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        //Mapeamos todos los componentes que estan en la lista de cryto_row.xml
        var image = view.findViewById<ImageView>(R.id.image)
        var name = view.findViewById<TextView>(R.id.nameTextView)
        var available = view.findViewById<TextView>(R.id.availableTextView)
        var buyButton = view.findViewById<Button>(R.id.buyButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Inflamos la lista con LayoutInflater
        val view = LayoutInflater.from(parent.context).inflate(R.layout.crypto_row, parent, false)
        return ViewHolder(view)
    }
    //Realiza la acualizacion de cada uno de los valores de la lista de cryptomonedas
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val crypto = cryptoList[position]
        //Actualizamos los valores de la imagen de la clase ViewHolder
        Picasso.get().load(crypto.imageUrl).into(holder.image)
        holder.name.text = crypto.name //Actualizamos el valor del name
        holder.available.text = holder.itemView.context.getString(R.string.available_message, crypto.available.toString()) //Actualizamos la cantidad disponible de available

        holder.buyButton.setOnClickListener {
            //Enviamos este metodo al cual se le dio click atravez del OnClickListener
            cryptosAdapterListener.onBuyCryptoClicked(crypto)
        }

    }

    override fun getItemCount(): Int {
        //Retornamos el tamaño de la lista
        return cryptoList.size
    }
}