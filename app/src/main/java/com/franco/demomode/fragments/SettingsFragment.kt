package com.franco.demomode.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.franco.demomode.BuildConfig
import com.franco.demomode.R
import com.franco.demomode.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@ExperimentalTime
class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var dumpPermissionPref: Preference
    private lateinit var writeSecureSettingsPref: Preference
    private lateinit var applyWithRootPref: Preference
    private lateinit var showAdbInstructionsPref: Preference
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {}
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        addPreferencesFromResource(R.xml.settings)
        
        dumpPermissionPref = findPreference(KEY_DUMP)!!
        writeSecureSettingsPref = findPreference(KEY_WRITE_SECURE_SETTINGS)!!
        showAdbInstructionsPref = findPreference(KEY_SHOW_ADB_INSTRUCTIONS)!!
        applyWithRootPref = findPreference(KEY_APPLY_WITH_ROOT)!!
        
        showAdbInstructionsPref.setOnPreferenceClickListener { showMessage(R.string.adb_instructions) }
        applyWithRootPref.setOnPreferenceClickListener { grantPermissionsWithRoot(); false }
    }
    
    private fun grantPermissionsWithRoot() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val exec = Runtime.getRuntime().exec(arrayOf(
                    "su", "-c",
                    "pm grant ${BuildConfig.APPLICATION_ID} android.permission.DUMP && pm grant ${BuildConfig.APPLICATION_ID} android.permission.WRITE_SECURE_SETTINGS",
                ))
                val returnCode = exec.waitFor()
                
                if (returnCode == 0) {
                    showMessage(R.string.permissions_have_been_granted)
                } else {
                    showMessage(R.string.permissions_could_not_be_granted)
                    Log.e(KEY_APPLY_WITH_ROOT, String(exec.errorStream.readBytes()))
                }
            } catch (e: Exception) {
                showMessage(R.string.permissions_could_not_be_granted)
                Log.e(KEY_APPLY_WITH_ROOT, e.message ?: "no message")
            }
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        lifecycleScope.launch {
            while (isActive) {
                renderDump(Utils.isDumpPermissionGranted(requireContext()))
                renderWriteSecureSettings(Utils.isWriteSecureSettingsPermissionGranted(requireContext()))
            }
        }
    }
    
    private fun renderDump(isGranted: Boolean) {
        dumpPermissionPref.summary = when (isGranted) {
            true -> getString(R.string.granted)
            false -> getString(R.string.not_granted)
        }
    }
    
    private fun renderWriteSecureSettings(isGranted: Boolean) {
        writeSecureSettingsPref.summary = when (isGranted) {
            true -> getString(R.string.granted)
            false -> getString(R.string.not_granted)
        }
    }
    
    private fun showMessage(messageId: Int): Boolean {
        lifecycleScope.launchWhenStarted {
            activity?.let { activity ->
                MaterialAlertDialogBuilder(activity)
                    .setTitle(R.string.permission_request_title)
                    .setMessage(messageId)
                    .setNeutralButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }
        return false
    }
    
    companion object {
        private const val KEY_DUMP = "dump_permission"
        private const val KEY_WRITE_SECURE_SETTINGS = "write_secure_settings"
        private const val KEY_APPLY_WITH_ROOT = "apply_with_root"
        private const val KEY_SHOW_ADB_INSTRUCTIONS = "show_adb_instructions"
    }
}
