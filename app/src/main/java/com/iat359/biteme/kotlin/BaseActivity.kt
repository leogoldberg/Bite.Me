package com.iat359.biteme.kotlin

import android.content.Intent
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.iat359.biteme.R


open class BaseActivity : AppCompatActivity() {
    private val drawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var fullLayout: DrawerLayout
    lateinit var baseContent: FrameLayout

    override fun setContentView(layoutResID: Int) {
        // Your base layout here
        fullLayout = layoutInflater.inflate(R.layout.activity_base, null) as DrawerLayout
        baseContent = fullLayout.findViewById(R.id.base_content) as FrameLayout

        // Setting the content of layout your provided to the act_content frame
        layoutInflater.inflate(layoutResID, baseContent, true)
        super.setContentView(fullLayout)

        // here you can get your drawer buttons and define how they
        // should behave and what must they do, so you won't be
        // needing to repeat it in every activity class
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navView = findViewById<NavigationView>(R.id.navView)
        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.find_recipes -> {
                    val intent = Intent(this, SwipeActivity::class.java)
                    startActivity(intent)
                }
                R.id.bookmarked_recipes -> {
                    val intent = Intent(this, RecipeListActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}