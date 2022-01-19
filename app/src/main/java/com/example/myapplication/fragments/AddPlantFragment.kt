package com.example.myapplication.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.myapplication.MainActivity
import com.example.myapplication.PlantModel
import com.example.myapplication.PlantRepository
import com.example.myapplication.PlantRepository.Singleton.downloadUri
import com.example.myapplication.R
import java.util.*

class AddPlantFragment(
    private val context: MainActivity
) : Fragment() {

    private var file:Uri? = null
private var uploadedImage: ImageView?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater?.inflate(R.layout.fragment_add_plant,container,false)
        //rec uploaded image
        uploadedImage = view.findViewById(R.id.preview_image)


        //  rec button
        val pickupImageButton = view.findViewById<Button>(R.id.upload_button)
        // click open images telephone
        pickupImageButton.setOnClickListener{pickupImage()}
        //
        val confirmButton = view.findViewById<Button>(R.id.confirm_button)
        confirmButton.setOnClickListener{sendForm(view)}


        return view
    }

    private fun sendForm(view : View) {
        val repo = PlantRepository()
        repo.uploadImage(file!!){

            val plantName = view.findViewById<EditText>(R.id.name_input).text.toString()
            val plantDescription = view.findViewById<EditText>(R.id.description_input).text.toString()

            val grow = view.findViewById<Spinner>(R.id.grow_spinner).selectedItem.toString()
            val water = view.findViewById<Spinner>(R.id.water_spinner).selectedItem.toString()

            val downloadImageUrl = downloadUri

            // creaet new object
            val plant =  PlantModel(
                UUID.randomUUID().toString(),
                plantName,
                plantDescription,
                downloadImageUrl.toString(),
                grow,
                water
            )

            //send to ddb
            repo.insertPlant(plant)
        }





    }

    private fun pickupImage() {
        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),47)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 47 && resultCode == Activity.RESULT_OK){
            //VERIF DONNE RECU NULLE
            if(data == null || data.data == null) return

            //rec image selected
            file = data.data
            //update apercu
            uploadedImage?.setImageURI(file)

        }
    }

}