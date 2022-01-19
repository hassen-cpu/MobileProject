package com.example.myapplication

import android.net.Uri
import com.example.myapplication.PlantRepository.Singleton.databaseRef
import com.example.myapplication.PlantRepository.Singleton.downloadUri
import com.example.myapplication.PlantRepository.Singleton.plantList
import com.example.myapplication.PlantRepository.Singleton.storageReference
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.*

class PlantRepository {

    object Singleton{
        //link to buccket
        private val BUCKET_URL: String = "gs://myapplication-87ba0.appspot.com"
        // se connecter à espce de stockage


        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(BUCKET_URL)

        // se connecter à la reference "plants"
        val databaseRef = FirebaseDatabase.getInstance().getReference("plants")

        //creer liste plant
        val plantList = arrayListOf<PlantModel>()

        //
        var downloadUri : Uri? = null
    }

    fun updateData(callback: () -> Unit){
        //absorber donnes a partir database
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // clear list
                plantList.clear()
                //recolter liste
                for(ds in snapshot.children){
                    //construire un obj plant
                    val plant = ds.getValue(PlantModel::class.java)

                    //verifier not null
                    if(plant != null){
                        //add to list
                        plantList.add(plant)
                    }
                }
                //act callback
                callback()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    //creer func send images to storage
    fun uploadImage(file: Uri , callback: () -> Unit){
        //verify not null
        if(file != null){
            val fileName = UUID.randomUUID().toString() + ".jpg"
            val ref = storageReference.child(fileName)
            val uploadTask = ref.putFile(file)

            //demarrer tache send
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>> {task ->
                if(!task.isSuccessful){
                    task.exception?.let { throw it }
                }
                return@Continuation ref.downloadUrl

            }).addOnCompleteListener{task ->
                if(task.isSuccessful){
                    //rec image
                    downloadUri = task.result
                    callback()
                }
            }
        }
    }

    // update plant sur ddb

    fun updatePlant(plant : PlantModel) = databaseRef.child(plant.id).setValue(plant)


    // insert plant sur ddb

    fun insertPlant(plant : PlantModel) = databaseRef.child(plant.id).setValue(plant)


// delete plant from ddb
    fun deletePlant(plant: PlantModel) = databaseRef.child(plant.id).removeValue()
}