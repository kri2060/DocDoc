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
public final class NoteEditorViewModel_Factory implements Factory<NoteEditorViewModel> {
  private final Provider<NoteRepository> noteRepositoryProvider;

  public NoteEditorViewModel_Factory(Provider<NoteRepository> noteRepositoryProvider) {
    this.noteRepositoryProvider = noteRepositoryProvider;
  }

  @Override
  public NoteEditorViewModel get() {
    return newInstance(noteRepositoryProvider.get());
  }

  public static NoteEditorViewModel_Factory create(
      Provider<NoteRepository> noteRepositoryProvider) {
    return new NoteEditorViewModel_Factory(noteRepositoryProvider);
  }

  public static NoteEditorViewModel newInstance(NoteRepository noteRepository) {
    return new NoteEditorViewModel(noteRepository);
  }
}
