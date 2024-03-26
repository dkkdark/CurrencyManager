package com.kseniabl.currencymanager.presentation.exchange_rates.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kseniabl.currencymanager.databinding.CurrencyItemBinding
import com.kseniabl.currencymanager.domain.model.CurrencyModel

class CurrencyRecycleViewAdapter :
    RecyclerView.Adapter<CurrencyRecycleViewAdapter.CurrencyRecycleViewHolder>() {

    private var listener: Listener? = null

    private val diffCallback = object : DiffUtil.ItemCallback<CurrencyModel>() {
        override fun areItemsTheSame(oldItem: CurrencyModel, newItem: CurrencyModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CurrencyModel, newItem: CurrencyModel): Boolean =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<CurrencyModel>) = differ.submitList(list)

    inner class CurrencyRecycleViewHolder(val binding: CurrencyItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyRecycleViewHolder {
        val binding =
            CurrencyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrencyRecycleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyRecycleViewHolder, position: Int) {
        val item = differ.currentList[position]

        holder.binding.apply {
            currencyCode.text = item.charCode
            currencyName.text = item.name
            value.text = item.value.toString()
            nominal.text = "Nominal: ${item.nominal}"
        }
        holder.itemView.setOnClickListener {
            listener?.onAddItemClick(item)
        }
    }

    fun setOnClickListener(onClickListener: Listener) {
        listener = onClickListener
    }

    override fun getItemCount(): Int = differ.currentList.size

    interface Listener {
        fun onAddItemClick(item: CurrencyModel)
    }
}