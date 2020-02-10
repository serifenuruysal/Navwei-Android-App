package com.androidapp.ui.base

/**
 * Created by S.Nur Uysal on 2019-10-23.
 */

abstract class BasePresenter<out T> {
  private var view: T? = null

  abstract fun initialise()
  abstract fun disposeSubscriptions()

  fun getView(): T? = view

  @Suppress("UNCHECKED_CAST")
  fun attachView(view: Any?) {
    this.view = view as T?
  }

  fun detachView() {
    view = null
  }
}
