package com.documentviewer.ui.viewer;

import com.documentviewer.core.preferences.PdfPositionPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class PdfViewerViewModel_Factory implements Factory<PdfViewerViewModel> {
  private final Provider<PdfPositionPreferences> pdfPositionPreferencesProvider;

  public PdfViewerViewModel_Factory(
      Provider<PdfPositionPreferences> pdfPositionPreferencesProvider) {
    this.pdfPositionPreferencesProvider = pdfPositionPreferencesProvider;
  }

  @Override
  public PdfViewerViewModel get() {
    return newInstance(pdfPositionPreferencesProvider.get());
  }

  public static PdfViewerViewModel_Factory create(
      Provider<PdfPositionPreferences> pdfPositionPreferencesProvider) {
    return new PdfViewerViewModel_Factory(pdfPositionPreferencesProvider);
  }

  public static PdfViewerViewModel newInstance(PdfPositionPreferences pdfPositionPreferences) {
    return new PdfViewerViewModel(pdfPositionPreferences);
  }
}
