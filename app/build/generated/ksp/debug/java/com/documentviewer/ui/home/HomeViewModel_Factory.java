package com.documentviewer.ui.home;

import com.documentviewer.core.preferences.ThemePreferences;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<ThemePreferences> themePreferencesProvider;

  public HomeViewModel_Factory(Provider<ThemePreferences> themePreferencesProvider) {
    this.themePreferencesProvider = themePreferencesProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(themePreferencesProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<ThemePreferences> themePreferencesProvider) {
    return new HomeViewModel_Factory(themePreferencesProvider);
  }

  public static HomeViewModel newInstance(ThemePreferences themePreferences) {
    return new HomeViewModel(themePreferences);
  }
}
