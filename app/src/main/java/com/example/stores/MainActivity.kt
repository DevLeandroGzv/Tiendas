package com.example.stores

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener , MainAux{

    private lateinit var mbinding : ActivityMainBinding


    private  lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager
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
        mbinding.fab.setOnClickListener{
            launchEditFragment()
        }

        setupRecyclerView()
    }

    private fun launchEditFragment(args :Bundle? = null) {
        val fragment = EditStoreFragment()
        if(args!= null) fragment.arguments = args
        val manager =supportFragmentManager
        val fragmentTransaction = manager.beginTransaction()

        fragmentTransaction.add(R.id.containerMAin,fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        hideFab()
    }

    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this,2)
        getStores()
        mbinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun getStores(){

        doAsync {val stores = StoreApplication.database.storeDao().getAllstore()
        uiThread {
            mAdapter.setStores(stores)
            }
        }
    }


    /*
    * OnClickListener
    * */
    override fun onClick(id:Long) {
        val args = Bundle()
        args.putLong(getString(R.string.arr_id),id)

        launchEditFragment(args)
    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavoirte=!storeEntity.isFavoirte
        doAsync {
            StoreApplication.database.storeDao().UpdateStore(storeEntity)
            uiThread {
                updateStorage(storeEntity)
            }
        }
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
    private fun confirmDialog(storeEntity: StoreEntity){
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_title_delete)
            .setPositiveButton(R.string.yesdialog) { dialog, which ->
                doAsync {
                    StoreApplication.database.storeDao().deleteStore(storeEntity)
                    uiThread {

                        mAdapter.delete(storeEntity)
                    }
                }
            }
            .setNegativeButton(R.string.dialogo_delete_cacel, null)
            .show()
    }

    private fun dial(phone :String ){
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }
        startIntent(callIntent)
    }

    private fun goToWebSite(website : String){
        if (website.isEmpty()){
            Toast.makeText(this,"error ingresando a la web",Toast.LENGTH_LONG).show()
        }else {
            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }

            startIntent(websiteIntent)
        }
    }
    private fun startIntent(intent: Intent){
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "eno hay ninguna aplicacion para esto", Toast.LENGTH_LONG)
                .show()

        }

    }

    override fun hideFab(isVisible: Boolean) {
        if(isVisible) mbinding.fab.show() else mbinding.fab.hide()
    }

    override fun addStorage(storeEntity: StoreEntity) {
       mAdapter.add(storeEntity)
    }

    override fun updateStorage(storeEntity: StoreEntity) {
            mAdapter.update(storeEntity)
    }
}