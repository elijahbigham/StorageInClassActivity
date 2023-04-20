package com.example.networkapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.*
import android.net.Uri
import android.provider.Settings


class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView

    private val internalFilename = "my_file"
    private lateinit var file: File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        file = File(filesDir, internalFilename)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        showButton.setOnClickListener {
            downloadComic(numberEditText.text.toString())
        }
        if (file.exists()){
            loadComic()
        }
        if(intent.action == Intent.ACTION_VIEW) {
            intent.data?.path?.run {
                downloadComic(replace("/",""))
            }
        }

        findViewById<Button>(R.id.button).setOnClickListener {
            try{
                val intent = Intent(
                    Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                    Uri.parse("package:${packageName}")
                )
                startActivity(intent)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }

    }

    private fun downloadComic (comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        requestQueue.add (
            JsonObjectRequest(url, {
                saveComic(it)
                showComic(it)
                                   }, {})
        )

    }

    private fun showComic (comicObject: JSONObject) {
        var myComicObject = comicObject


        titleTextView.text = myComicObject.getString("title")
        descriptionTextView.text = myComicObject.getString("alt")
        Picasso.get().load(myComicObject.getString("img")).into(comicImageView)
    }
    private fun saveComic(comicObject: JSONObject) {
        try {
            val outputStream = FileOutputStream(file)
            outputStream.write(comicObject.toString().toByteArray())
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun loadComic(){
        lateinit var comicObject: JSONObject
        try {
            val br = BufferedReader(FileReader(file))
            val text = StringBuilder()
            var line: String?
            while (br.readLine().also { line = it } != null) {
                text.append(line)
                text.append('\n')
            }
            br.close()
            comicObject = JSONObject(text.toString())

        } catch (e: IOException) {
            e.printStackTrace()
        }
        showComic(comicObject)
    }


}