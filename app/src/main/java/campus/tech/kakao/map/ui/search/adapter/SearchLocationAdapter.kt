package campus.tech.kakao.map.ui.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import campus.tech.kakao.map.databinding.ItemLocationBinding
import campus.tech.kakao.map.data.local_search.Location
import campus.tech.kakao.map.ui.search.SearchLocationViewModel

class SearchLocationAdapter(
    private val dataList: List<Location>,
    private val context: Context,
    private val viewModel: SearchLocationViewModel
) : RecyclerView.Adapter<SearchLocationAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: ItemLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                viewModel.addHistory(dataList[bindingAdapterPosition].name)
                viewModel.addMarker(dataList[bindingAdapterPosition])
            }
        }

        fun bind(locationData: Location) {
            binding.locationNameTextView.text = locationData.name
            binding.locationAddressTextView.text = locationData.address
            binding.locationTypeTextView.text = locationData.category
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemLocationBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size
}