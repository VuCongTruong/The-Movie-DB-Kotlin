package com.example.moviedb.ui.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviedb.R
import kotlinx.android.synthetic.main.fragment_paging.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BasePagingFragment<ViewBinding : ViewDataBinding, ViewModel : BasePagingViewModel<Item>, Item : Any> :
    BaseFragment<ViewBinding, ViewModel>() {

    override val layoutId: Int = R.layout.fragment_paging

    abstract val pagingAdapter: BasePagingAdapter<Item, out ViewDataBinding>

    open fun getLayoutManager(): RecyclerView.LayoutManager = GridLayoutManager(context, 2)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupPaging()
    }

    /**
     * setup paging
     */
    private fun setupPaging() {
        refresh_layout?.setOnRefreshListener {
            viewModel.doRefresh()
        }
        recycler_view?.layoutManager = getLayoutManager()
        recycler_view?.adapter = pagingAdapter
        recycler_view?.setHasFixedSize(true)
        lifecycleScope.launch {
            viewModel.items.collectLatest {
                pagingAdapter.submitData(it)
            }
        }
        pagingAdapter.addLoadStateListener {
            viewModel.handleLoadStates(
                combinedLoadStates = it,
                itemCount = pagingAdapter.itemCount
            )
        }
    }

    override fun handleLoading(isLoading: Boolean) {
        // use progress bar
    }
}