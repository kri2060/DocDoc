package com.documentviewer.di;

import com.documentviewer.data.local.AppDatabase;
import com.documentviewer.data.local.dao.ReadingPositionDao;
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
public final class AppModule_ProvideReadingPositionDaoFactory implements Factory<ReadingPositionDao> {
  private final Provider<AppDatabase> databaseProvider;

  public AppModule_ProvideReadingPositionDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public ReadingPositionDao get() {
    return provideReadingPositionDao(databaseProvider.get());
  }

  public static AppModule_ProvideReadingPositionDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new AppModule_ProvideReadingPositionDaoFactory(databaseProvider);
  }

  public static ReadingPositionDao provideReadingPositionDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideReadingPositionDao(database));
  }
}
