package com.example.newsappmvvm.ui.util

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject


fun Fragment.handleApiError(
    failure: NetworkResult.Failure,
    retry: (() -> Unit)? = null
) {
    when{
        failure.isNetworkError -> requireView().snackBar("Please check your internet connection", retry)
        else -> {
            val responseString = failure.errorBody?.string().toString()
            val jsonObject = JSONObject(responseString)
            val error = if(jsonObject.has("message")) jsonObject.get("message") else responseString
            requireView().snackBar(error.toString())
        }
    }
}

fun View.snackBar(message: String, action: (() -> Unit)? = null) {
    val snack = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let {
        snack.setAction("Retry"){
            it()
        }
    }
    snack.show()
}