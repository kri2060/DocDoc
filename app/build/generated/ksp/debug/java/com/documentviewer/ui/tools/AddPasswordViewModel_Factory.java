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
public final class AddPasswordViewModel_Factory implements Factory<AddPasswordViewModel> {
  private final Provider<DocumentRepository> documentRepositoryProvider;

  public AddPasswordViewModel_Factory(Provider<DocumentRepository> documentRepositoryProvider) {
    this.documentRepositoryProvider = documentRepositoryProvider;
  }

  @Override
  public AddPasswordViewModel get() {
    return newInstance(documentRepositoryProvider.get());
  }

  public static AddPasswordViewModel_Factory create(
      Provider<DocumentRepository> documentRepositoryProvider) {
    return new AddPasswordViewModel_Factory(documentRepositoryProvider);
  }

  public static AddPasswordViewModel newInstance(DocumentRepository documentRepository) {
    return new AddPasswordViewModel(documentRepository);
  }
}
