package com.hajmola.up

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hajmola.up.utils.SessionManager
import com.hajmola.up.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController : NavController
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    private val viewModel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
                val granted = perms.values.all { it }
                if (!granted) {
                    finish()
                }
            }

        checkPermissionsAndPick()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        navController = navHostFragment.navController

        val navInflater = navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.navigation)

        if (SessionManager.isLoggedIn(this@MainActivity)) {
            if (SessionManager.isAdminLoggedIn(this)){
                navGraph.setStartDestination(R.id.adminHomeFragment)
            }else{
                navGraph.setStartDestination(R.id.mainMenuFragment)
            }
        } else {
            navGraph.setStartDestination(R.id.loginFragment)
        }

        navController.graph = navGraph

        viewModel.getCityList()

        lifecycleScope.launch {
            viewModel.getCityListState.collect { state ->
                when(state){
                    is UiState.Success -> {
                        state.data?.let { cities ->
                            viewModel.updateFetchedCityList{cities}
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun checkPermissionsAndPick() {
        val neededPermissions = mutableListOf<String>()

        // Camera permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            neededPermissions.add(Manifest.permission.CAMERA)
        }

        // Storage / Media permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                neededPermissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                neededPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (neededPermissions.isNotEmpty()) {
            permissionLauncher.launch(neededPermissions.toTypedArray())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SessionManager.setFetchHandled(this, false)
    }
}