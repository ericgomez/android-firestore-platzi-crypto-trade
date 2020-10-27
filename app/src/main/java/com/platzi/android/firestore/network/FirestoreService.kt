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
    fun updateUser(user:User, callback: Callback<User>?) {// el callback es para notificarnos cuando fue exitosa ono la operacion
        firebaseFirestore.collection(USERS_COLLECTION_NAME).document(user.username)
            .update("cryptosList", user.cryptosList)//Actualizamos en Firestore
            .addOnSuccessListener { result ->
                if (callback != null)//Si el callback es diferente de nulo
                    callback.onSuccess(user)
            }
            .addOnFailureListener { exception -> callback!!.onFailed(exception) }//En caso de que exista un error
    }

    //Actualizar Crypto
    fun updateCrypto(crypto: Crypto) {
        firebaseFirestore.collection(CRYPTO_COLLECTION_NAME).document(crypto.getDocumentId())
            .update("available", crypto.available)//modificamos la cantidad de monedas disponibles
    }

    //Funcion que consulte todas las listas de Crypto Monedas
    fun getCryptos(callback: Callback<List<Crypto>>?) {
        firebaseFirestore.collection(CRYPTO_COLLECTION_NAME)
                .get()
                .addOnSuccessListener { result -> //En caso de completar exitosamente
                    for (document in result) {//Iteramos en toda la lista
                        //Convertomos a un objeto de tipo Crypto
                        val cryptoList = result.toObjects(Crypto::class.java)
                        callback!!.onSuccess(cryptoList)//Al terminar el proceso y validamos con !! que el callback no este nulo
                        break//Nos salimos del for
                    }
                }
                .addOnFailureListener { exception -> callback!!.onFailed(exception)

                }
    }

    //Funcion que nos permite encontrar usuarios decuerdo al ID
    fun findUserById(id: String, callback: Callback<User>) {
        firebaseFirestore.collection(USERS_COLLECTION_NAME).document(id)
                .get()
                .addOnSuccessListener { result ->
                    if (result.data != null) {//Validamos que resultado sea diferente de nulo
                        callback.onSuccess(result.toObject(User::class.java))//En caso de ser exitoso Convertimos el resultado a un usuario
                    } else {//en caso de que el resultado sea null
                        callback.onSuccess(null)//Le indicamos que la operacion fue exitosa pero el resultado fue nulo
                    }
                }
                .addOnFailureListener { exception -> callback.onFailed(exception) }
    }

}