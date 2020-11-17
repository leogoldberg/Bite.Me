package com.iat359.biteme

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.google.android.material.navigation.NavigationView
import com.yuyakaido.android.cardstackview.*
import java.util.*

class SwipeActivity : BaseActivity(), CardStackListener {
    private val db by lazy { RecipeDatabase(this) }
    private val drawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }
    private val cardStackView by lazy { findViewById<CardStackView>(R.id.card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val recipesCached by lazy { db.getAllData(RecipeReaderContract.RECIPE_TABLE_NAME)}
    private val adapter by lazy { CardStackAdapter(recipesCached, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe)
        Log.d("data", "test call")

        setupNavigation()
        setupCardStackView()
        setupButton()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
        if (manager.topPosition == adapter.itemCount - 5) {
            paginate()
        }
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.recipe_name)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.recipe_name)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")
    }

    private fun setupNavigation() {
//        // Toolbar
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//
//        // DrawerLayout
//        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
//        actionBarDrawerToggle.syncState()
//        drawerLayout.addDrawerListener(actionBarDrawerToggle)
//
//        // NavigationView
//        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
//        navigationView.setNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.reload -> reload()
//                R.id.add_spot_to_first -> addFirst(1)
//                R.id.add_spot_to_last -> addLast(1)
//                R.id.remove_spot_from_first -> removeFirst(1)
//                R.id.remove_spot_from_last -> removeLast(1)
//                R.id.replace_first_spot -> replace()
//                R.id.swap_first_for_last -> swap()
//            }
//            drawerLayout.closeDrawers()
//            true
//        }
    }

    private fun setupCardStackView() {
        initialize()
    }

    private fun setupButton() {
        val skip = findViewById<View>(R.id.skip_button)
        skip.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }

        val rewind = findViewById<View>(R.id.rewind_button)
        rewind.setOnClickListener {
            val setting = RewindAnimationSetting.Builder()
                    .setDirection(Direction.Bottom)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(DecelerateInterpolator())
                    .build()
            manager.setRewindAnimationSetting(setting)
            cardStackView.rewind()
        }

        val like = findViewById<View>(R.id.like_button)
        like.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Right)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }
    }

    private fun initialize() {
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    private fun paginate() {
        val old = adapter.getRecipes()
        val new = old.plus(createRecipes())
        val callback = RecipeDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setRecipes(new)
        result.dispatchUpdatesTo(adapter)
    }

//    private fun reload() {
//        val old = adapter.getRecipes()
//        val new = createRecipes()
//        val callback = RecipeDiffCallback(old, new)
//        val result = DiffUtil.calculateDiff(callback)
//        adapter.setRecipes(new)
//        result.dispatchUpdatesTo(adapter)
//    }
//
//    private fun addFirst(size: Int) {
//        val old = adapter.getRecipes()
//        val new = mutableListOf<Recipe>().apply {
//            addAll(old)
//            for (i in 0 until size) {
//                add(manager.topPosition, createRecipe())
//            }
//        }
//        val callback = RecipeDiffCallback(old, new)
//        val result = DiffUtil.calculateDiff(callback)
//        adapter.setRecipes(new)
//        result.dispatchUpdatesTo(adapter)
//    }
//
//    private fun addLast(size: Int) {
//        val old = adapter.getRecipes()
//        val new = mutableListOf<Recipe>().apply {
//            addAll(old)
//            addAll(List(size) { createRecipe() })
//        }
//        val callback = RecipeDiffCallback(old, new)
//        val result = DiffUtil.calculateDiff(callback)
//        adapter.setRecipes(new)
//        result.dispatchUpdatesTo(adapter)
//    }
//
//    private fun removeFirst(size: Int) {
//        if (adapter.getRecipes().isEmpty()) {
//            return
//        }
//
//        val old = adapter.getRecipes()
//        val new = mutableListOf<Recipe>().apply {
//            addAll(old)
//            for (i in 0 until size) {
//                removeAt(manager.topPosition)
//            }
//        }
//        val callback = RecipeDiffCallback(old, new)
//        val result = DiffUtil.calculateDiff(callback)
//        adapter.setRecipes(new)
//        result.dispatchUpdatesTo(adapter)
//    }
//
//    private fun removeLast(size: Int) {
//        if (adapter.getRecipes().isEmpty()) {
//            returng
//        }
//
//        val old = adapter.getRecipes()
//        val new = mutableListOf<Recipe>().apply {
//            addAll(old)
//            for (i in 0 until size) {
//                removeAt(this.size - 1)
//            }
//        }
//        val callback = RecipeDiffCallback(old, new)
//        val result = DiffUtil.calculateDiff(callback)
//        adapter.setRecipes(new)
//        result.dispatchUpdatesTo(adapter)
//    }
//
//    private fun replace() {
//        val old = adapter.getRecipes()
//        val new = mutableListOf<Recipe>().apply {
//            addAll(old)
//            removeAt(manager.topPosition)
//            add(manager.topPosition, createRecipe())
//        }
//        adapter.setRecipes(new)
//        adapter.notifyItemChanged(manager.topPosition)
//    }
//
//    private fun swap() {
//        val old = adapter.getRecipes()
//        val new = mutableListOf<Recipe>().apply {
//            addAll(old)
//            val first = removeAt(manager.topPosition)
//            val last = removeAt(this.size - 1)
//            add(manager.topPosition, last)
//            add(first)
//        }
//        val callback = RecipeDiffCallback(old, new)
//        val result = DiffUtil.calculateDiff(callback)
//        adapter.setRecipes(new)
//        result.dispatchUpdatesTo(adapter)
//    }

//    private fun createRecipe(): Spot {
//        return Spot(
//                name = "Yasaka Shrine",
//                city = "Kyoto",
//                url = "https://source.unsplash.com/Xq1ntWruZQI/600x800"
//        )
//    }
    private fun createRecipes(): List<Recipe> {
        return recipesCached;
    }
}
