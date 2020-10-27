package com.platzi.android.firestore.network

import java.lang.Exception


//Esta interfas nos estara notificando cada vez que exista un cambio
interface RealtimeDataListener<T> {

    //Nos enviara la data que a sido cambiada
    fun onDataChange(updateData: T)

    fun onError(exception: Exception)//Funcion que notifica cuando existe un error en el servidor
}