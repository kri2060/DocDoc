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
public final class RemovePasswordViewModel_Factory implements Factory<RemovePasswordViewModel> {
  private final Provider<DocumentRepository> documentRepositoryProvider;

  public RemovePasswordViewModel_Factory(Provider<DocumentRepository> documentRepositoryProvider) {
    this.documentRepositoryProvider = documentRepositoryProvider;
  }

  @Override
  public RemovePasswordViewModel get() {
    return newInstance(documentRepositoryProvider.get());
  }

  public static RemovePasswordViewModel_Factory create(
      Provider<DocumentRepository> documentRepositoryProvider) {
    return new RemovePasswordViewModel_Factory(documentRepositoryProvider);
  }

  public static RemovePasswordViewModel newInstance(DocumentRepository documentRepository) {
    return new RemovePasswordViewModel(documentRepository);
  }
}
