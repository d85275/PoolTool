package com.e.pooltool

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.e.pooltool.database.Repository

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MyViewModel
    private lateinit var navController: NavController  // we'll initialise it later
    private val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
        // react on change
        // you can check destination.id or destination.label and act based on that
        viewModel.setDesId(destination.id)
        invalidateOptionsMenu()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_main)
        getViewModel()
        getNavController()
        setRepository()
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(listener)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(listener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // hide all the items if its not in the main fragment
        if (!viewModel.isMainFragment()) return false

        menuInflater.inflate(R.menu.main_menu, menu)
        viewModel.setResetButtonVisibility(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btAdd -> {
                viewModel.addPlayer()
                invalidateOptionsMenu()
            }
            R.id.btReset -> DialogHelper(this, viewModel).resetPlayer()
            R.id.btSavedPlayers -> {
                navController.navigate(R.id.action_mainFragment_to_savedPlayerFragment)
                invalidateOptionsMenu() // call the onCreateOptionsMenu function again
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getViewModel() {
        viewModel = this.run {
            ViewModelProviders.of(
                this,
                SavedStateViewModelFactory(application, this)
            )[MyViewModel::class.java]
        }
    }

    private fun getNavController() {
        navController = findNavController(R.id.host_fragment)
    }

    private fun setRepository() {
        viewModel.setRepository(Repository(this))
    }
}
