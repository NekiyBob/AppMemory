package com.example.realapp.listeners;

import com.example.realapp.entities.Note;

public interface NotesListener {
     void onNoteClicked(Note note, int position);
}
