package com.isu.apitracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.isu.apitracker.domain.Repository

class ViewModelFactory(private val repository:Repository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ApiTrackerViewModel(repository) as T
    }
}