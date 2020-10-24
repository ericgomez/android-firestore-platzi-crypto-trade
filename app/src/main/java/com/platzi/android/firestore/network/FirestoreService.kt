package com.platzi.android.firestore.network

import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.android.firestore.model.Crypto
import com.platzi.android.firestore.model.User

//Cramos dos contaste que nos facilitaran el acceso a las colecciones de Firestore
const val CRYPTO_COLLECTION_NAME = "cryptos"
const val USERS_COLLECTION_NAME = "users"

class FirestoreService(val firebaseFirestore: FirebaseFirestore) {
    //Seteamos mismo que nos permite guarda cualquier tipo de documento en Firestore
    fun setDocument(data: Any, collectionName: String, id: String, callback: Callback<Void>) {
        firebaseFirestore.collection(collectionName).document(id).set(data)
            .addOnSuccessListener { callback.onSuccess(null) }//En caso de que la operacion sea exitosa
            .addOnFailureListener { exception -> callback.onFailed(exception) }//En caso de que la operacion tenga un error
    }

    //Actualizar el usuario
    fun updateUser(user:User, callback: Callback<User>) {// el callback es para notificarnos cuando fue exitosa ono la operacion
        firebaseFirestore.collection(USERS_COLLECTION_NAME).document(user.username)
            .update("cryptosList", user.cryptosList)//Actualizamos en Firestore
            .addOnSuccessListener { result ->
                if (callback != null)//Si el callback es diferente de nulo
                    callback.onSuccess(user)
            }
            .addOnFailureListener { exception -> callback.onFailed(exception) }//En caso de que exista un error
    }

    //Actualizar Crypto
    fun updateCrypto(crypto: Crypto) {
        firebaseFirestore.collection(CRYPTO_COLLECTION_NAME).document(crypto.getDocumentId())
            .update("available", crypto.available)//modificamos la cantidad de monedas disponibles
    }

}