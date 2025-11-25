package com.documentviewer.ui.tools;

import com.documentviewer.data.repository.NoteRepository;
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
public final class NotepadViewModel_Factory implements Factory<NotepadViewModel> {
  private final Provider<NoteRepository> noteRepositoryProvider;

  public NotepadViewModel_Factory(Provider<NoteRepository> noteRepositoryProvider) {
    this.noteRepositoryProvider = noteRepositoryProvider;
  }

  @Override
  public NotepadViewModel get() {
    return newInstance(noteRepositoryProvider.get());
  }

  public static NotepadViewModel_Factory create(Provider<NoteRepository> noteRepositoryProvider) {
    return new NotepadViewModel_Factory(noteRepositoryProvider);
  }

  public static NotepadViewModel newInstance(NoteRepository noteRepository) {
    return new NotepadViewModel(noteRepository);
  }
}
