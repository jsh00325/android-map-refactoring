package campus.tech.kakao.map.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import campus.tech.kakao.map.databinding.ActivitySearchLocationBinding
import campus.tech.kakao.map.ui.search.adapter.HistoryAdapter
import campus.tech.kakao.map.ui.search.adapter.SearchLocationAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchLocationActivity : AppCompatActivity() {
    @Inject lateinit var viewModel: SearchLocationViewModel
    private lateinit var binding: ActivitySearchLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.removeSearchInputButton.setOnClickListener {
            binding.searchInputEditText.text.clear()
        }

        binding.searchInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchLocation(s.toString())
            }
        })

        applyObserver()
    }

    fun applyObserver() {
        viewModel.searchInput.observe(this) {
            it?.let { searchInput ->
                binding.searchInputEditText.setText(searchInput)
                binding.searchInputEditText.setSelection(searchInput.length)
            }
        }

        viewModel.location.observe(this) {
            it?.let { locationData ->
                binding.searchResultRecyclerView.adapter =
                    SearchLocationAdapter(locationData, this, viewModel)
                binding.emptyResultTextView.isVisible = locationData.isEmpty()
            }
        }

        viewModel.history.observe(this) {
            it?.let { historyData ->
                var adapter = binding.searchHistoryRecyclerView.adapter as? HistoryAdapter

                if (adapter == null) {
                    adapter = HistoryAdapter(historyData, this, viewModel)
                    binding.searchHistoryRecyclerView.adapter = adapter
                } else {
                    adapter.updateDataList(historyData)
                }

                binding.searchHistoryRecyclerView.isVisible = historyData.isNotEmpty()
                binding.executePendingBindings()
            }
        }

        viewModel.markerLocation.observe(this) {
            it?.let { location ->
                val intent = intent
                intent.putExtra("markerLocation", location)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}