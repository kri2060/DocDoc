package com.documentviewer.di;

import com.documentviewer.data.local.AppDatabase;
import com.documentviewer.data.local.dao.SearchIndexDao;
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
public final class AppModule_ProvideSearchIndexDaoFactory implements Factory<SearchIndexDao> {
  private final Provider<AppDatabase> databaseProvider;

  public AppModule_ProvideSearchIndexDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public SearchIndexDao get() {
    return provideSearchIndexDao(databaseProvider.get());
  }

  public static AppModule_ProvideSearchIndexDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new AppModule_ProvideSearchIndexDaoFactory(databaseProvider);
  }

  public static SearchIndexDao provideSearchIndexDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSearchIndexDao(database));
  }
}
