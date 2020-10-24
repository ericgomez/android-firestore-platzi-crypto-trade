package com.platzi.android.firestore.network

import java.lang.Exception

interface  Callback<T> {//La T significa que se recibe un valor de tipo generico
// T nos permite mapear cualquier tipo de valor
    fun onSuccess(result: T?)

    //En caso de que no sea exitosa la operacion
    fun onFailed(exception: Exception)//Nos notificara por medio de una exception
}