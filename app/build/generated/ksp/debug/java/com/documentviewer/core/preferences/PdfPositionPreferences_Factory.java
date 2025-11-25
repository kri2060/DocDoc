package com.documentviewer.core.preferences;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class PdfPositionPreferences_Factory implements Factory<PdfPositionPreferences> {
  private final Provider<Context> contextProvider;

  public PdfPositionPreferences_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PdfPositionPreferences get() {
    return newInstance(contextProvider.get());
  }

  public static PdfPositionPreferences_Factory create(Provider<Context> contextProvider) {
    return new PdfPositionPreferences_Factory(contextProvider);
  }

  public static PdfPositionPreferences newInstance(Context context) {
    return new PdfPositionPreferences(context);
  }
}
