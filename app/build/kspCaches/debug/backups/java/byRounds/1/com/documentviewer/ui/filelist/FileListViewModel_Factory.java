package com.documentviewer.ui.filelist;

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
public final class FileListViewModel_Factory implements Factory<FileListViewModel> {
  private final Provider<DocumentRepository> documentRepositoryProvider;

  public FileListViewModel_Factory(Provider<DocumentRepository> documentRepositoryProvider) {
    this.documentRepositoryProvider = documentRepositoryProvider;
  }

  @Override
  public FileListViewModel get() {
    return newInstance(documentRepositoryProvider.get());
  }

  public static FileListViewModel_Factory create(
      Provider<DocumentRepository> documentRepositoryProvider) {
    return new FileListViewModel_Factory(documentRepositoryProvider);
  }

  public static FileListViewModel newInstance(DocumentRepository documentRepository) {
    return new FileListViewModel(documentRepository);
  }
}
