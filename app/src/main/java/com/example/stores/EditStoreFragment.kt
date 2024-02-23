package com.example.stores

import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.UiMode
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private var mStoreEntity: StoreEntity? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    private fun getStore(id: Long) {
        doAsync {
            mStoreEntity = StoreApplication.database.storeDao().getStoreByID(id)
            uiThread {
                if (mStoreEntity != null) setUiStore(mStoreEntity!!)
            }

        }

    }

    private fun setUiStore(storeEntity: StoreEntity) {

        with(mBinding) {
            etName.text = storeEntity.name.editable()
            tiPhone.text = storeEntity.telefono.editable()
            itWeb.text = storeEntity.website.editable()
            tiImage.text = storeEntity.photoUrl.editable()
            Glide.with(requireActivity())
                .load(storeEntity.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mBinding.imagePhoto)
        }

    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getLong(getString(R.string.arr_id), 0)
        if (id != null && id != 0L) {
            mIsEditMode = true
            getStore(id)
        } else {
            mIsEditMode = false
            mStoreEntity = StoreEntity(name = "", telefono = "", photoUrl = "")
        }
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = if(mIsEditMode) getString(R.string.mod_store_title)
        else getString(R.string.edt_store_title)

        setHasOptionsMenu(true)
        setupTextFields()
    }

    private fun setupTextFields() {
        with(mBinding) {
            etName.addTextChangedListener {
                validateFields(TiLName)
            }
            tiImage.addTextChangedListener {
                validateFields( TiLimage)
                loadImage(it.toString().trim())

            }
            tiPhone.addTextChangedListener {
                validateFields(TilPhone)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun loadImage(url :String){
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(mBinding.imagePhoto)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }

            R.id.action_save -> {
                if (mStoreEntity != null && validateFields(mBinding.TiLimage,mBinding.TilPhone,mBinding.TiLName)) {
                    with(mStoreEntity!!) {
                        name = mBinding.etName.text.toString().trim()
                        telefono = mBinding.tiPhone.text.toString().trim()
                        website = mBinding.itWeb.text.toString().trim()
                        photoUrl = mBinding.tiImage.text.toString().trim()
                    }
                    doAsync {
                        if (mIsEditMode) StoreApplication.database.storeDao()
                            .UpdateStore(mStoreEntity!!)
                        else mStoreEntity!!.id =
                            StoreApplication.database.storeDao().addStore(mStoreEntity!!)
                        uiThread {
                            hideKeyboard()
                            if(mIsEditMode){
                                mActivity?.updateStorage(mStoreEntity!!)
                                Snackbar.make(mBinding.root, R.string.succes_val, Snackbar.LENGTH_SHORT).show()
                            }else {
                                mActivity?.addStorage(mStoreEntity!!)

                                Toast.makeText(
                                    mActivity,
                                    "Tienda insertada correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                mActivity?.onBackPressed()
                            }
                        }
                    }
                }
                true
            }

            else -> return super.onOptionsItemSelected(item)

        }
        //return super.onOptionsItemSelected(item)
    }

    private fun validateFields(vararg textFields:TextInputLayout) : Boolean{
        var isValid = true
        for(texfield in textFields){
            if(texfield.editText?.text.toString().trim().isEmpty()){
                texfield.error =getString(R.string.helper_text)

                isValid=false
            }else texfield.error =null

        }

        if(!isValid)Snackbar.make(mBinding.root,R.string.message_isVAlid,Snackbar.LENGTH_SHORT).show()
        return isValid

    }

//    private fun validateFields(): Boolean {
//        var isValid = true
//
//        if(mBinding.tiImage.text.toString().trim().isEmpty()){
//            mBinding.TiLimage.error = getString(R.string.helper_text)
//            mBinding.TiLimage.requestFocus()
//            isValid=false
//        }
//        if(mBinding.tiPhone.text.toString().trim().isEmpty()){
//            mBinding.TilPhone.error = getString(R.string.helper_text)
//            mBinding.TilPhone.requestFocus()
//            isValid=false
//        }
//        if(mBinding.etName.text.toString().trim().isEmpty()){
//            mBinding.TiLName.error = getString(R.string.helper_text)
//            mBinding.TiLName.requestFocus()
//            isValid=false
//        }
//        return isValid
//    }

    private fun hideKeyboard() {
        val imn = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null) {
            imn.hideSoftInputFromWindow(requireView().windowToken, 0)
        }

    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(true)
        setHasOptionsMenu(false)
        super.onDestroy()
    }
}