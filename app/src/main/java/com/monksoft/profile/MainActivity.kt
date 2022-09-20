package com.monksoft.profile

import android.app.SearchManager
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.updateLayoutParams
import androidx.preference.PreferenceManager
import com.monksoft.profile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    private var lat: Double = 0.0
    private var long: Double = 0.0
    private lateinit var imgUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)



        //updateUI()
        readUserData()
        setupIntents()
    }

    override fun onResume() {
        super.onResume()
        refreshSettings()
    }

    private fun refreshSettings() {
        val isEnabled = sharedPreferences.getBoolean(getString(R.string.preference_key_enable_clicks), true)

        with(binding) {
            tvName.isEnabled = isEnabled
            tvEmail.isEnabled = isEnabled
            tvWebSite.isEnabled = isEnabled
            tvPhone.isEnabled = isEnabled
            tvLocation.isEnabled = isEnabled
            tvEnableLocation.isEnabled = isEnabled
        }

        val imgSize = sharedPreferences.getString("pref_ui_img_size","")
        val size = when(imgSize){
            "pref_small" -> resources.getDimensionPixelSize(R.dimen.profile_image_size_small)
            "pref_medium" -> resources.getDimensionPixelSize(R.dimen.profile_image_size_medium)
            else -> resources.getDimensionPixelSize(R.dimen.profile_image_size_large)

        }
        binding.imageProfile.updateLayoutParams {
            width = size
            height = size
        }

        readUserData()
    }

    private fun readUserData() {
        imgUri = Uri.parse((sharedPreferences.getString(getString(R.string.key_image), "")))
        var name = sharedPreferences.getString(getString(R.string.key_name), "")
        var mail = sharedPreferences.getString(getString(R.string.key_mail), "")
        var phone = sharedPreferences.getString(getString(R.string.key_phone), "")
        var website = sharedPreferences.getString(getString(R.string.key_web), "")
        lat = sharedPreferences.getString(getString(R.string.key_lat), "0.0")!!.toDouble()
        long = sharedPreferences.getString(getString(R.string.key_long), "0.0")!!.toDouble()

        updateUI(name, mail, website, phone)
    }

    private fun setupIntents() {
        with(binding) {
            tvName.setOnClickListener {
                val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                    putExtra(SearchManager.QUERY, binding.tvName.text.toString())
                }
                launchIntent(intent)
            }

            tvEmail.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(binding.tvEmail.text.toString()))
                    putExtra(Intent.EXTRA_SUBJECT, "From kotlin course")
                    putExtra(Intent.EXTRA_TEXT, "Hi im here")
                }
                launchIntent(intent)
            }

            tvWebSite.setOnClickListener {
                val webPage = Uri.parse(binding.tvWebSite.text.toString())
                val intent = Intent(Intent.ACTION_VIEW, webPage)
                launchIntent(intent)
            }

            tvPhone.setOnClickListener {
                val intent = Intent(Intent.ACTION_CALL).apply{
                    val phone = (it as TextView).text
                    data = Uri.parse("tel:$phone")
                }
                launchIntent(intent)
            }

            tvLocation.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("geo:0,0?q=$lat,$long(Cursos android)")
                    `package` = "com.google.android.apps.maps"
                }
                launchIntent(intent)
            }

            tvEnableLocation.setOnClickListener {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                launchIntent(intent)
            }
        }
    }

    private fun launchIntent(intent: Intent) {
        if(intent.resolveActivity(packageManager) != null){
            startActivity(intent)
        } else {
            println(intent.resolveActivity(packageManager))
            Toast.makeText(this, "No resueldo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(name:String?, email:String?, website:String?, phone:String?) {
        with(binding) {
            imageProfile.setImageURI(imgUri)
            tvName.text = name ?: "Brian"
            tvEmail.text = email ?: "sjkdfhksj@gmail.com"
            tvWebSite.text = website ?: "www.facebook.com"
            tvPhone.text = phone ?: "+593995456551"
        }
        if (lat.equals("")) lat = -0.1615789
        if (long.equals("")) long = -78.4845747
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_edit -> {
                val intent = Intent(this, EditActivity::class.java)
                intent.putExtra(getString(R.string.key_name),binding.tvName.text.toString())
                intent.putExtra(getString(R.string.key_mail),binding.tvEmail.text.toString())
                intent.putExtra(getString(R.string.key_phone),binding.tvPhone.text.toString())
                intent.putExtra(getString(R.string.key_web),binding.tvWebSite.text.toString())
                intent.putExtra(getString(R.string.key_lat), lat.toString())
                intent.putExtra(getString(R.string.key_long), long.toString())

                startActivityForResult(intent, RC_EDIT)
            }
            R.id.action_settings -> {
                val intent:Intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == RC_EDIT) {
            imgUri = Uri.parse(data?.getStringExtra(getString(R.string.key_image)))
            val name = data?.getStringExtra(getString(R.string.key_name))
            val mail = data?.getStringExtra(getString(R.string.key_mail))
            val web = data?.getStringExtra(getString(R.string.key_web))
            val phone = data?.getStringExtra(getString(R.string.key_phone))
            lat = data?.getDoubleExtra(getString(R.string.key_lat), 0.0) ?: 0.0
            long = data?.getDoubleExtra(getString(R.string.key_long), 0.0) ?: 0.0

            saveUserData(name,mail, web, phone)
        }
    }

    private fun saveUserData(name: String?, mail: String?, web: String?, phone: String?) {
        sharedPreferences.edit {
            putString(getString(R.string.key_image), imgUri.toString())
            putString(getString(R.string.key_name), name)
            putString(getString(R.string.key_mail), mail)
            putString(getString(R.string.key_web), web)
            putString(getString(R.string.key_phone), phone)
            putString(getString(R.string.key_lat), lat.toString())
            putString(getString(R.string.key_long), long.toString())
            apply()
        }
        updateUI(name, mail, web, phone)
    }

    companion object {
        private const val RC_EDIT = 21
    }
}