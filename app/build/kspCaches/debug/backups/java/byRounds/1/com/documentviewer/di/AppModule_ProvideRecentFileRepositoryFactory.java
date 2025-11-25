package com.documentviewer.di;

import com.documentviewer.data.local.dao.RecentFileDao;
import com.documentviewer.data.repository.RecentFileRepository;
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
public final class AppModule_ProvideRecentFileRepositoryFactory implements Factory<RecentFileRepository> {
  private final Provider<RecentFileDao> recentFileDaoProvider;

  public AppModule_ProvideRecentFileRepositoryFactory(
      Provider<RecentFileDao> recentFileDaoProvider) {
    this.recentFileDaoProvider = recentFileDaoProvider;
  }

  @Override
  public RecentFileRepository get() {
    return provideRecentFileRepository(recentFileDaoProvider.get());
  }

  public static AppModule_ProvideRecentFileRepositoryFactory create(
      Provider<RecentFileDao> recentFileDaoProvider) {
    return new AppModule_ProvideRecentFileRepositoryFactory(recentFileDaoProvider);
  }

  public static RecentFileRepository provideRecentFileRepository(RecentFileDao recentFileDao) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideRecentFileRepository(recentFileDao));
  }
}
