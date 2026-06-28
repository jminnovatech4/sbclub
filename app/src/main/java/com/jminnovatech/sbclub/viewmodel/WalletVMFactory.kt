package com.jminnovatech.sbclub.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jminnovatech.sbclub.repository.AppRepository

class WalletVMFactory(private val repo: AppRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WalletVM(repo) as T
    }
    class WalletVMFactory(private val context: Context) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            val repo = AppRepository(context)

            return WalletVM(repo) as T
        }
    }
}