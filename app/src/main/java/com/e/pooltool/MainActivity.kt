package com.e.pooltool

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.e.pooltool.database.Repository

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_main)
        getViewModel()
        setRepository()
    }

    private fun getViewModel() {
        viewModel = this.run {
            ViewModelProviders.of(
                this,
                SavedStateViewModelFactory(application, this)
            )[MyViewModel::class.java]
        }
    }

    private fun setRepository() {
        viewModel.setRepository(Repository(this))
    }
}
