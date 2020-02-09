package com.androidapp.domain

import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer

interface UseCase<A , R > : ObservableTransformer<A, R>

interface FlowableUseCase<A , R > : FlowableTransformer<A, R>

interface BlockingUseCase<A , out R > {

    fun execute(action: A): R
}