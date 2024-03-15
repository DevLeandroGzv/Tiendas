package com.example.stores.mainModule.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.R
import com.example.stores.common.entities.StoreEntity
import com.example.stores.databinding.ItemStoreBinding

class StoreAdapter(
    private var stores: MutableList<StoreEntity>,
    private var listener: OnClickListener
) : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = stores.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val store = stores.get(position)
        with(holder) {
            setListener(store)
            binding.tvname.text = store.name
            binding.checkboxFavorite.isChecked = store.isFavoirte

            Glide.with(mContext)
                .load(store.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.imagePhoto)


        }
    }

    fun add(storeEntity: StoreEntity) {

        if (storeEntity.id != 0L) {
            if (!stores.contains(storeEntity)) {
                stores.add(storeEntity)
                notifyItemInserted(stores.size - 1)
            }else{
                update(storeEntity)
            }
        }
    }

    fun setStores(stores: List<StoreEntity>) {
        this.stores = stores as MutableList<StoreEntity>
        notifyDataSetChanged()

    }

    private fun update(storeEntity: StoreEntity) {
        val index = stores.indexOf(storeEntity)
        if (index != -1) {
            stores.set(index, storeEntity)
            notifyItemChanged(index)
        }
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemStoreBinding.bind(view)
        fun setListener(storeEntity: StoreEntity) {
            with(binding.root) {
                setOnClickListener {
                    listener.onClick(storeEntity)
                }
                setOnLongClickListener {
                    listener.onDeleteStore(storeEntity)
                    true
                }
            }
            binding.checkboxFavorite.setOnClickListener {
                listener.onFavoriteStore(storeEntity)
            }
        }
    }
}