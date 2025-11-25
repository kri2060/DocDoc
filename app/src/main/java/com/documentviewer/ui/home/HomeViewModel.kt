package com.documentviewer.ui.home

import androidx.lifecycle.ViewModel
import com.documentviewer.core.preferences.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val themePreferences: ThemePreferences
) : ViewModel()
