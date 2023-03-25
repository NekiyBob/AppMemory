
package com.example.realapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.realapp.R;
import com.example.realapp.adapters.NotesAdapter;
import com.example.realapp.database.NotesDataBase;
import com.example.realapp.entities.Note;
import com.example.realapp.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener {
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_NOTE = 3;
    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;
    public static int codeForNotificationChannel = 1;
    private int noteClickedPosition = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //При нажатии открывается активность для создания заметки
        ImageView imageAddNote = findViewById(R.id.addNote);
        imageAddNote.setOnClickListener((v) -> {
            //noinspection deprecation
            startActivityForResult(new Intent(getApplicationContext(), CreateNoteActivity.class), REQUEST_CODE_ADD_NOTE);
        });

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList, this);
        notesRecyclerView.setAdapter(notesAdapter);


    }
    //Отображение заметок при запуске
    @Override
    protected void onResume() {
        super.onResume();
        getNotes(REQUEST_CODE_SHOW_NOTE, false);
    }
    // переходим к заметке при клике на нее
    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        //noinspection deprecation
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
    }

    //Получаем заметки
    private void getNotes(final int requestCode, final boolean isNoteDeleted) {
        @SuppressWarnings("deprecation")
        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {
            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDataBase.getDataBase(getApplicationContext()).noteDao().getAllNotes();
            }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                //Показыаем все заметки на экране
                if (requestCode == REQUEST_CODE_SHOW_NOTE) {
                    noteList.clear();
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                    //Добaвляем в начало заметку
                    noteList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                    //Заменяем заметку
                    noteList.remove(noteClickedPosition);
                    //Проверка удален ли
                    if (isNoteDeleted) {
                        notesAdapter.notifyItemRemoved(noteClickedPosition);
                    } else {
                        noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }
            }
        }
        new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            getNotes(REQUEST_CODE_ADD_NOTE, false);
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK) {
            if (data != null) {
                getNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false));
            }
        }
    }
}
