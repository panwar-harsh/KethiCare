package com.example.greendoc

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.greendoc.databinding.ActivityMainBinding
import com.example.greendoc.utils.migratePlantData

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("migration_prefs", MODE_PRIVATE)
        val migrated = prefs.getBoolean("plant_data_migrated", false)

        if (!migrated) {
            migratePlantData()
            prefs.edit().putBoolean("plant_data_migrated", true).apply()
        }


        // Load HomeFragment by default
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeSection -> replaceFragment(HomeFragment())
                R.id.diagnoseSection -> {
                    // 🔥 Start the Diagnose Activity instead of a Fragment
                    startActivity(Intent(this, CameraActivityDiagnose::class.java))
                    true
                }
                R.id.myPlantSection -> replaceFragment(MyPlantFragment())
                R.id.settingSection -> replaceFragment(ProfileFragment())
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }
    override fun onResume() {
        super.onResume()
        // Refresh garden fragment when returning from other activities
        (supportFragmentManager.findFragmentByTag("GardenFragment") as? GardenFragment)?. setupFirebaseListener()
    }
}
