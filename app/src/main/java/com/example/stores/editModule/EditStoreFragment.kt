package com.example.stores.editModule

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
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.R
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.databinding.FragmentEditStoreBinding
import com.example.stores.editModule.viewModel.EditStoreViewModel
import com.example.stores.mainModule.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class EditStoreFragment : Fragment() {

    private lateinit var mEdidStoreViewModel: EditStoreViewModel
    private lateinit var mBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private lateinit var  mStoreEntity: StoreEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEdidStoreViewModel = ViewModelProvider(requireActivity()).get(EditStoreViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)
        return mBinding.root
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



        //mmvm

        setupViewModel()

        setHasOptionsMenu(true)
        setupTextFields()
    }

    private fun setupViewModel() {
            mEdidStoreViewModel.getStoreSelected().observe(viewLifecycleOwner) {
                mStoreEntity = it
                if (it.id != 0L) {
                    mIsEditMode = true
                    setUiStore(it)
                } else {
                    mIsEditMode = false
                }
                setupActionBar()
            }
        mEdidStoreViewModel.getResult().observe(viewLifecycleOwner) {result ->
            hideKeyboard()

            when(result){

                is Long -> {
                    mStoreEntity.id =result
                    mEdidStoreViewModel.setStoreSelected(mStoreEntity)
                    Toast.makeText(
                        mActivity,
                        "Tienda insertada correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    mActivity?.onBackPressed()}
                is StoreEntity -> {
                    mEdidStoreViewModel.setStoreSelected(mStoreEntity)
                    Snackbar.make(mBinding.root, R.string.succes_val, Snackbar.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun setupActionBar() {
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = if(mIsEditMode) getString(R.string.mod_store_title)
        else getString(R.string.edt_store_title)
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
                if (validateFields(mBinding.TiLimage,mBinding.TilPhone,mBinding.TiLName)) {
                    with(mStoreEntity) {
                        name = mBinding.etName.text.toString().trim()
                        telefono = mBinding.tiPhone.text.toString().trim()
                        website = mBinding.itWeb.text.toString().trim()
                        photoUrl = mBinding.tiImage.text.toString().trim()
                    }
                    if (mIsEditMode) mEdidStoreViewModel.updateStore(mStoreEntity)
                    else mEdidStoreViewModel.saveStore(mStoreEntity)
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

        if(!isValid)Snackbar.make(mBinding.root, R.string.message_isVAlid,Snackbar.LENGTH_SHORT).show()
        return isValid

    }


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
        mEdidStoreViewModel.setShowFab(true)
        mEdidStoreViewModel.setResult(Any())
        setHasOptionsMenu(false)
        super.onDestroy()
    }
}