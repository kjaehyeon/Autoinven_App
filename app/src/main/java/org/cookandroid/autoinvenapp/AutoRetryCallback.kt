package org.cookandroid.autoinvenapp

import retrofit2.Call
import retrofit2.Callback


abstract class AutoRetryCallback<T> : Callback<T> {
    private val mRetryLimitCount: Int
    private var mRetryCount = 0

    constructor() {
        mRetryLimitCount = 3
    }

    constructor(retryLimitCount: Int) {
        mRetryLimitCount = retryLimitCount
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        mRetryCount++
        if (mRetryCount > mRetryLimitCount) {
            onFinalFailure(call, t)
            return
        }
        retry(call)
    }

    private fun retry(call: Call<T>) {
        call.clone().enqueue(this)
    }

    abstract fun onFinalFailure(call: Call<T>?, t: Throwable?)
}