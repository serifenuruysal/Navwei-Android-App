package com.androidapp.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import javax.inject.Inject

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

abstract class BaseActivity<P : BasePresenter<Any>> : AppCompatActivity() {

  @Inject
  lateinit var presenter: P

  protected abstract fun getLayout(): Int
  protected abstract fun initInjector()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(getLayout())
    initInjector()
    initPresenter()
  }

  private fun initPresenter() {
    presenter.attachView(this)
    presenter.initialise()
  }

  override fun onDestroy() {
    presenter.disposeSubscriptions()
    presenter.detachView()
    super.onDestroy()
  }
}
