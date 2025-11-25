package com.documentviewer.ui.tools;

import com.documentviewer.data.repository.DocumentRepository;
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
public final class MergePdfViewModel_Factory implements Factory<MergePdfViewModel> {
  private final Provider<DocumentRepository> documentRepositoryProvider;

  public MergePdfViewModel_Factory(Provider<DocumentRepository> documentRepositoryProvider) {
    this.documentRepositoryProvider = documentRepositoryProvider;
  }

  @Override
  public MergePdfViewModel get() {
    return newInstance(documentRepositoryProvider.get());
  }

  public static MergePdfViewModel_Factory create(
      Provider<DocumentRepository> documentRepositoryProvider) {
    return new MergePdfViewModel_Factory(documentRepositoryProvider);
  }

  public static MergePdfViewModel newInstance(DocumentRepository documentRepository) {
    return new MergePdfViewModel(documentRepository);
  }
}
