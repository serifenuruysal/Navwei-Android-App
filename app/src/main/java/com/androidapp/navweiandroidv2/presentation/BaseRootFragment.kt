package com.androidapp.navweiandroidv2.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.androidapp.navweiandroidv2.R
import kotlinx.android.synthetic.main.mall_fragment.*

/**
 * Created by S.Nur Uysal on 2019-11-06.
 */
abstract class BaseRootFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (toolbar != null) {

            setToolbarListener()
        }
    }


    open fun setToolbarListener() {
        toolbar.setNavigationOnClickListener {
            activity?.finish()
        }
    }


    fun setToolbarTitle(toolbarTitle: String?) {
        tv_title_toolbar.setText(toolbarTitle)
        tv_title_toolbar.setOnClickListener(onChangedStoreClicked)

    }

    fun setToolbarMenu(menuId: Int) {
        toolbar.inflateMenu(menuId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                activity?.finish()
            }
        }
        return true
    }

    private val onChangedStoreClicked = View.OnClickListener {
        activity?.finish()
    }

}