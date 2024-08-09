package com.isu.apitracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.isu.apitracker.domain.Repository
import com.isu.apitracker.presentation.ApiTrackerViewModel

class ViewModelFactory(private val repository:Repository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ApiTrackerViewModel(repository) as T
    }
}