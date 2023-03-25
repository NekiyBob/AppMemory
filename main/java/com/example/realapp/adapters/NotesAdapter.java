package com.example.realapp.adapters;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realapp.R;
import com.example.realapp.entities.Note;
import com.example.realapp.listeners.NotesListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder>{
    private final List<Note> notes;
    private final NotesListener notesListener;


    public NotesAdapter(List<Note> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener = notesListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_container_note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setNote(notes.get(position));
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesListener.onNoteClicked(notes.get(position), position);
            }
        });
    }
    @Override
    public int getItemCount() {
        return notes.size();
    }
    @Override
    public int getItemViewType(int position){
        return position;
    }
    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textPart;
        LinearLayout layoutNote;
        RoundedImageView imageNote;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textPart = itemView.findViewById(R.id.textPart);
            layoutNote = itemView.findViewById(R.id.layoutNote);
            imageNote = itemView.findViewById(R.id.imageNote);
        }

        void setNote(Note note){
            textTitle.setText(note.getTitle());
            textPart.setText(note.getNoteText());
            if (note.getImagePath()!= null){
                imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
                imageNote.setVisibility(View.VISIBLE);
            } else{
                imageNote.setVisibility(View.GONE);
            }
        }
    }
}
