package com.example.realapp.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.realapp.dao.NoteDao;
import com.example.realapp.entities.Note;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NotesDataBase extends RoomDatabase {
    private static NotesDataBase notesDataBase;
    //Возвращает экземпляр DAO
    public static synchronized NotesDataBase getDataBase (Context context){
        if (notesDataBase == null){
            notesDataBase = Room.databaseBuilder(context,
                    NotesDataBase.class,
                    "notes_db").build();
        }
        return notesDataBase;
    }
    public abstract NoteDao noteDao();
}
