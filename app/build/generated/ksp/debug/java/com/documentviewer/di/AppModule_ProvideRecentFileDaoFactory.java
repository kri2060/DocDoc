package com.documentviewer.di;

import com.documentviewer.data.local.AppDatabase;
import com.documentviewer.data.local.dao.RecentFileDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideRecentFileDaoFactory implements Factory<RecentFileDao> {
  private final Provider<AppDatabase> databaseProvider;

  public AppModule_ProvideRecentFileDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public RecentFileDao get() {
    return provideRecentFileDao(databaseProvider.get());
  }

  public static AppModule_ProvideRecentFileDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new AppModule_ProvideRecentFileDaoFactory(databaseProvider);
  }

  public static RecentFileDao provideRecentFileDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideRecentFileDao(database));
  }
}
