package com.example.stores.mainModule

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.editModule.EditStoreFragment
import com.example.stores.common.utils.MainAux
import com.example.stores.R
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.databinding.ActivityMainBinding
import com.example.stores.editModule.viewModel.EditStoreViewModel
import com.example.stores.mainModule.adapters.OnClickListener
import com.example.stores.mainModule.adapters.StoreAdapter
import com.example.stores.mainModule.viewModel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mbinding: ActivityMainBinding


    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager

    //MVVM
    private lateinit var mMainViewModel: MainViewModel
    private lateinit var mEditStoreViewModel: EditStoreViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mbinding.root)

//        mbinding.btnSave.setOnClickListener{
//            val store= StoreEntity(name = mbinding.etName.text.toString().trim())
//            Thread{
//            StoreApplication.database.storeDao().addStore(store)}.start()
//            mAdapter.add(store)
//        }
        mbinding.fab.setOnClickListener {
            launchEditFragment()
        }
        setupViewModel()

        setupRecyclerView()
    }

    private fun setupViewModel() {
        mMainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mMainViewModel.getstores().observe(this, { stores ->
            mAdapter.setStores(stores)
        })

        mEditStoreViewModel = ViewModelProvider(this).get(EditStoreViewModel::class.java)
        mEditStoreViewModel.getShowFab().observe(this,{isVisible ->
            if (isVisible) mbinding.fab.show() else mbinding.fab.hide()
        })

        mEditStoreViewModel.getStoreSelected().observe(this,{storeEntity->
            mAdapter.add(storeEntity)
        })

    }

    private fun launchEditFragment(storeEntity: StoreEntity = StoreEntity()) {
        mEditStoreViewModel.setShowFab(false)
        mEditStoreViewModel.setStoreSelected(storeEntity)
        val fragment = EditStoreFragment()
        val manager = supportFragmentManager
        val fragmentTransaction = manager.beginTransaction()

        fragmentTransaction.add(R.id.containerMAin, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }

    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, 2)
        // getStores()
        mbinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    /*
    * OnClickListener
    * */
    override fun onClick(storeEntity: StoreEntity) {
        launchEditFragment(storeEntity)
    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        mMainViewModel.updateStore(storeEntity)

    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        val items = resources.getStringArray(R.array.array_option_item)
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.opcion_dialog)
            .setItems(items) { diaolg, i ->
                when (i) {
                    0 -> confirmDialog(storeEntity)
                    1 -> dial(storeEntity.telefono)
                    2 -> goToWebSite(storeEntity.website)
                }
            }
            .show()

    }

    private fun confirmDialog(storeEntity: StoreEntity) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_title_delete)
            .setPositiveButton(R.string.yesdialog) { dialog, which ->
        mMainViewModel.deleteStore(storeEntity)
            }
            .setNegativeButton(R.string.dialogo_delete_cacel, null)
            .show()
    }

    private fun dial(phone: String) {
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }
        startIntent(callIntent)
    }

    private fun goToWebSite(website: String) {
        if (website.isEmpty()) {
            Toast.makeText(this, "error ingresando a la web", Toast.LENGTH_LONG).show()
        } else {
            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }

            startIntent(websiteIntent)
        }
    }

    private fun startIntent(intent: Intent) {
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "eno hay ninguna aplicacion para esto", Toast.LENGTH_LONG)
                .show()

        }

    }

}