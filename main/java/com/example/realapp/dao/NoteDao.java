package com.example.realapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.realapp.entities.Note;

import java.util.List;

@Dao
// Интерфейс для взаимодействия с базой данных
public interface NoteDao {
    @Query("SELECT * FROM note ORDER BY id DESC")
    List<Note> getAllNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(com.example.realapp.entities.Note note);
    @Delete
    void deleteNote(Note note);
}
