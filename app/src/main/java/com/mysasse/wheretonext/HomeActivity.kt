package com.mysasse.wheretonext

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.mysasse.wheretonext.ui.auth.LoginActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Init fire-base vars
        mAuth = FirebaseAuth.getInstance()

        //Register and set the support action bar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.notificationFragment,
                R.id.ridesFragment,
                R.id.reviewFragment,
                R.id.historyFragment,
                R.id.carProfileFragment,
                R.id.userProfileFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.logout_option -> {

                val logoutDialog = AlertDialog.Builder(this)
                logoutDialog.setTitle("Sign Out")
                logoutDialog.setMessage("Are you sure you want to logout")
                logoutDialog.setPositiveButton("Sure") { _, _ ->
                    mAuth.signOut()
                    sendToLogin()
                }
                logoutDialog.setNegativeButton("Cancel") { _, _ ->

                }

                logoutDialog.show()

                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onStart() {
        super.onStart()

        /**
         * Get the foreground level permission status for ACCESS_FINE_LOCATION
         */
        val permissionAccessFineLocationApproved = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        /**
         * Start an activity to request the permission for ACCESS_FINE_LOCATION
         */
        if (!permissionAccessFineLocationApproved) {
            Intent(this, RequestPermissionsActivity::class.java).also { permissionIntent ->
                startActivity(permissionIntent)
            }
        }

    }


    private fun sendToLogin() {
        Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }.also { startActivity(it) }
    }
}
