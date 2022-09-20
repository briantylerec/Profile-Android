package com.monksoft.profile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.monksoft.profile.databinding.ActivityEditBinding

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding
    private var imgUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        with(binding){
            intent.extras?.let{
                etName.setText(it.getString(getString(R.string.key_name)))
                etWeb.setText(it.getString(getString(R.string.key_web)))
                etPhone.setText(it.getString(getString(R.string.key_phone)))
                etMail.setText(it.getString(getString(R.string.key_mail)))
                etLatitud.setText(it.getString(getString(R.string.key_lat)))
                etLongitud.setText(it.getString(getString(R.string.key_long)))
            }

            etName.setOnFocusChangeListener { view, isFocused -> if (isFocused) etName.text?.let{ etName.setSelection(it.length) }}
            etWeb.setOnFocusChangeListener { view, isFocused -> if (isFocused) etWeb.text?.let{ etWeb.setSelection(it.length) }}
            etPhone.setOnFocusChangeListener { view, isFocused -> if (isFocused) etPhone.text?.let{ etPhone.setSelection(it.length) }}
            etMail.setOnFocusChangeListener { view, isFocused -> if (isFocused) etMail.text?.let{ etMail.setSelection(it.length) }}
            etLatitud.setOnFocusChangeListener { view, isFocused -> if (isFocused) etLatitud.text?.let{ etLatitud.setSelection(it.length) }}
            etLongitud.setOnFocusChangeListener { view, isFocused -> if (isFocused) etLongitud.text?.let{ etLongitud.setSelection(it.length) }}

            btnSelectPhoto.setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "image/jpeg"
                }
                startActivityForResult(intent, RC_GALLERY)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_save -> sendData()
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == RC_GALLERY) {
                imgUri = data?.data

                val contentResolver = applicationContext.contentResolver
                val takeFlags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                imgUri?.let { contentResolver.takePersistableUriPermission(it, takeFlags) }

                binding.imageProfile.setImageURI(imgUri)
            }
        }
    }

    private fun sendData(){
        val intent = Intent()

        with(binding){
            intent.apply {
                putExtra(getString(R.string.key_image), imgUri.toString())
                putExtra(getString(R.string.key_name), etName.text.toString())
                putExtra(getString(R.string.key_mail), etMail.text.toString())
                putExtra(getString(R.string.key_phone), etPhone.text.toString())
                putExtra(getString(R.string.key_web), etWeb.text.toString())
                putExtra(getString(R.string.key_lat), etLatitud.text.toString().toDouble())
                putExtra(getString(R.string.key_long),etLongitud.text.toString().toDouble())
            }
        }

        setResult(RESULT_OK, intent)

        finish()
    }

    companion object {
        private const val RC_GALLERY = 22
    }
}