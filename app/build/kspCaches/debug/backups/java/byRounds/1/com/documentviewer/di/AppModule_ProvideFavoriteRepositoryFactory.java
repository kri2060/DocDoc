package com.documentviewer.di;

import com.documentviewer.data.local.dao.FavoriteDao;
import com.documentviewer.data.repository.FavoriteRepository;
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
public final class AppModule_ProvideFavoriteRepositoryFactory implements Factory<FavoriteRepository> {
  private final Provider<FavoriteDao> favoriteDaoProvider;

  public AppModule_ProvideFavoriteRepositoryFactory(Provider<FavoriteDao> favoriteDaoProvider) {
    this.favoriteDaoProvider = favoriteDaoProvider;
  }

  @Override
  public FavoriteRepository get() {
    return provideFavoriteRepository(favoriteDaoProvider.get());
  }

  public static AppModule_ProvideFavoriteRepositoryFactory create(
      Provider<FavoriteDao> favoriteDaoProvider) {
    return new AppModule_ProvideFavoriteRepositoryFactory(favoriteDaoProvider);
  }

  public static FavoriteRepository provideFavoriteRepository(FavoriteDao favoriteDao) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideFavoriteRepository(favoriteDao));
  }
}
