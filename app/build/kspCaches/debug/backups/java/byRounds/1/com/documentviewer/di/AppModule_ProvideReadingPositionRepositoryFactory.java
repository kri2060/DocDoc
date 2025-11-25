package com.documentviewer.di;

import com.documentviewer.data.local.dao.ReadingPositionDao;
import com.documentviewer.data.repository.ReadingPositionRepository;
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
public final class AppModule_ProvideReadingPositionRepositoryFactory implements Factory<ReadingPositionRepository> {
  private final Provider<ReadingPositionDao> readingPositionDaoProvider;

  public AppModule_ProvideReadingPositionRepositoryFactory(
      Provider<ReadingPositionDao> readingPositionDaoProvider) {
    this.readingPositionDaoProvider = readingPositionDaoProvider;
  }

  @Override
  public ReadingPositionRepository get() {
    return provideReadingPositionRepository(readingPositionDaoProvider.get());
  }

  public static AppModule_ProvideReadingPositionRepositoryFactory create(
      Provider<ReadingPositionDao> readingPositionDaoProvider) {
    return new AppModule_ProvideReadingPositionRepositoryFactory(readingPositionDaoProvider);
  }

  public static ReadingPositionRepository provideReadingPositionRepository(
      ReadingPositionDao readingPositionDao) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideReadingPositionRepository(readingPositionDao));
  }
}
