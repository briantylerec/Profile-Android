package com.monksoft.profile

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.core.content.edit
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val deleteUserDataPreference = findPreference<Preference>("pref_delete_data")
        deleteUserDataPreference?.setOnPreferenceClickListener {
            val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreference.edit {
                putString(getString(R.string.key_image), null)
                putString(getString(R.string.key_name), null)
                putString(getString(R.string.key_mail), null)
                putString(getString(R.string.key_web), null)
                putString(getString(R.string.key_phone), null)
                putString(getString(R.string.key_lat), null)
                putString(getString(R.string.key_long), null)
            }
            true
        }

        val switchPreferenceCompact = findPreference<SwitchPreference>("pref_en-clic")
        val listPreference = findPreference<ListPreference>("pref_ui_img_size")

        val restoreAllPreference = findPreference<Preference>("pref_restore_data")
        restoreAllPreference?.setOnPreferenceClickListener {
            val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreference.edit().clear().apply()

            switchPreferenceCompact?.isChecked = true
            listPreference?.value = "pref_large"
            true
        }
    }
}