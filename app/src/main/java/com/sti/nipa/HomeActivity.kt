package com.sti.nipa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sessionManager: SessionManager
    private lateinit var usernameEditText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        drawerLayout = findViewById(R.id.drawerLayout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_View)

        usernameEditText = navigationView.getHeaderView(0).findViewById(R.id.usernameText)



        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if(savedInstanceState == null){
           replaceFragment(HomeFragment())
            navigationView.setCheckedItem(R.id.nv_home)
        }
        val sessionManager = SessionManager(this)
        val roleIDCheck = sessionManager.getRoleID()
        val loggedInUsernameKey = sessionManager.getUsernameKey()
        val userDetails = sessionManager.getUserDetails()
        val loggedInUsernameText = userDetails[loggedInUsernameKey] ?: ""
        usernameEditText.text = loggedInUsernameText.toString()


    }

    //Opening each fragments
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nv_home -> replaceFragment(HomeFragment())
//            R.id.nv_info -> replaceFragment(InfoFragment())
            R.id.nv_location -> replaceFragment(Location())
            R.id.nv_logout -> logoutAndRedirectToLogin()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    fun logoutAndRedirectToLogin() {
        val sessionManager = SessionManager(this)
        sessionManager.logoutUser()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    //Change fragments
    private fun replaceFragment(fragment: Fragment){
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        } else{
            onBackPressedDispatcher.onBackPressed()
        }
    }
}