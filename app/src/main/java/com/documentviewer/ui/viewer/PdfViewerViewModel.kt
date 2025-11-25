package com.documentviewer.ui.viewer

import androidx.lifecycle.ViewModel
import com.documentviewer.core.preferences.PdfPositionPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PdfViewerViewModel @Inject constructor(
    val pdfPositionPreferences: PdfPositionPreferences
) : ViewModel()
