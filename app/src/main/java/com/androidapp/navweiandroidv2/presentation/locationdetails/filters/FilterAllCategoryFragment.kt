package com.androidapp.navweiandroidv2.presentation.locationdetails.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.androidapp.entity.models.Category
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.adapters.CategoryAllListAdapter
import com.androidapp.navweiandroidv2.presentation.locationdetails.filters.events.OnClickCategoryEvent
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.all_category_filters_fragment.*


/**
 * Created by S.Nur Uysal on 2019-11-07.
 */
class FilterAllCategoryFragment : Fragment() {


    private var selectedCategoryList: MutableList<Category> = mutableListOf()
    private var categoryList: ArrayList<Category>? = arrayListOf()
    lateinit var subscribeOnClickCategoryEvent: Disposable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.all_category_filters_fragment, container, false)

    }

    companion object {
        fun newInstance(
            selectedCategoryList: ArrayList<Category>?,
            categoryList: ArrayList<Category>
        ): FilterAllCategoryFragment {
            val f = FilterAllCategoryFragment()

            val args = Bundle()
            args.putParcelableArrayList("categoryList", categoryList)
            args.putParcelableArrayList("selectedCategoryList", selectedCategoryList)
            f.arguments = args

            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        selectedCategoryList = arguments?.getParcelableArrayList<Category>("selectedCategoryList")!!
        categoryList = arguments?.getParcelableArrayList<Category>("categoryList")
        initToolbar()
        subscribe()

    }

    private fun subscribe() {
        subscribeOnClickCategoryEvent =
            RxBus.listen(OnClickCategoryEvent::class.java).subscribe {
                selectedCategoryList = it.selectedCategoryList.toMutableList()

            }
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

    }

    private fun setToolbar() {
        tv_title_toolbar.text = getString(R.string.title_store_category)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAllCategoryRecyclerView()
        setToolbar()

    }

    private fun initializeAllCategoryRecyclerView() {

        rv_filter_all_category_list.apply {
            val adapter = CategoryAllListAdapter(categoryList!!, selectedCategoryList)
            rv_filter_all_category_list.adapter = adapter

        }

    }


}