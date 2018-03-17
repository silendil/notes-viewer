package com.example.pavelhryts.notesviewer.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pavelhryts.notesviewer.R;
import com.example.pavelhryts.notesviewer.model.Note;
import com.example.pavelhryts.notesviewer.model.NoteHolder;

/**
 * Created by Pavel.Hryts on 17.03.2018.
 */

public abstract class ListViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
    private TextView textView;
    private Note note;

    public ListViewHolder(LayoutInflater inflater, ViewGroup parent, final SelectListener listener) {
        super(inflater.inflate(R.layout.list_item, parent, false));
        textView = itemView.findViewById(R.id.list_item_text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                note.setSelected(!note.isSelected());
                setSelected(note.isSelected());
                listener.onSelect(note);
            }
        });
        textView.setOnLongClickListener(this);
    }


    public void bind(int position) {
        note = NoteHolder.getInstance().getNote(position);
        textView.setText(note.getTitle());
        setSelected(note.isSelected());
    }

    private void setSelected(boolean value) {
        if (value)
            textView.setBackgroundColor(Color.MAGENTA);
        else
            textView.setBackgroundColor(Color.CYAN);
        note.setSelected(value);
        textView.setSelected(value);

    }

    public interface SelectListener {
        void onSelect(Note note);
    }

    @Override
    public boolean onLongClick(View view) {
        getPushedItem(note);
        return false;
    }

    public abstract void getPushedItem(Note note);
}
