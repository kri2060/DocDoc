package com.documentviewer.di;

import android.content.Context;
import com.documentviewer.data.local.dao.SearchIndexDao;
import com.documentviewer.data.repository.SearchRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideSearchRepositoryFactory implements Factory<SearchRepository> {
  private final Provider<SearchIndexDao> searchIndexDaoProvider;

  private final Provider<Context> contextProvider;

  public AppModule_ProvideSearchRepositoryFactory(Provider<SearchIndexDao> searchIndexDaoProvider,
      Provider<Context> contextProvider) {
    this.searchIndexDaoProvider = searchIndexDaoProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public SearchRepository get() {
    return provideSearchRepository(searchIndexDaoProvider.get(), contextProvider.get());
  }

  public static AppModule_ProvideSearchRepositoryFactory create(
      Provider<SearchIndexDao> searchIndexDaoProvider, Provider<Context> contextProvider) {
    return new AppModule_ProvideSearchRepositoryFactory(searchIndexDaoProvider, contextProvider);
  }

  public static SearchRepository provideSearchRepository(SearchIndexDao searchIndexDao,
      Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSearchRepository(searchIndexDao, context));
  }
}
