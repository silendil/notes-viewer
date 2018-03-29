package com.example.pavelhryts.notesviewer.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.pavelhryts.notesviewer.R;
import com.example.pavelhryts.notesviewer.model.notes.Note;
import com.example.pavelhryts.notesviewer.model.notes.NoteHolder;

/**
 * Created by Pavel.Hryts on 17.03.2018.
 */

public abstract class ListViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
    private TextView textViewTitle;
    private TextView textViewBody;
    private ImageButton show;
    private boolean expanded = false;
    private Note note;
    private View root;

    ListViewHolder(LayoutInflater inflater, ViewGroup parent, final SelectListener listener) {
        super(inflater.inflate(R.layout.list_item, parent, false));
        textViewTitle = itemView.findViewById(R.id.list_item_text);
        textViewBody = itemView.findViewById(R.id.list_item_message);
        show = itemView.findViewById(R.id.list_text_button);
        root = itemView.findViewById(R.id.list_parent_root);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(expanded){
                    collapse();
                }else{
                    expand();
                }

            }
        });
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                note.setSelected(!note.isSelected());
                setSelected(note.isSelected());
                listener.onSelect(note);
            }
        };
        textViewTitle.setOnClickListener(clickListener);
        textViewBody.setOnClickListener(clickListener);
        textViewTitle.setOnLongClickListener(this);
        textViewBody.setOnLongClickListener(this);
    }


    void bind(int position) {
        note = NoteHolder.getInstance().getNote(position);
        textViewTitle.setText(note.getTitle());
        setSelected(note.isSelected());
        collapse();
    }

    private void collapse(){
        expanded = false;
        textViewBody.setVisibility(View.GONE);
        show.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
    }

    private void expand(){
        expanded = true;
        textViewBody.setVisibility(View.VISIBLE);
        textViewBody.setText(note.getBody());
        show.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
    }

    private void setSelected(boolean value) {
        if (value)
            root.setBackgroundColor(Color.MAGENTA);
        else
            root.setBackgroundColor(Color.CYAN);
        note.setSelected(value);
        textViewTitle.setSelected(value);

    }

    public interface SelectListener {
        void onSelect(Note note);
    }

    @Override
    public boolean onLongClick(View view) {
        getPushedItem(note);
        setSelected(note.isSelected());
        return false;
    }

    public abstract void getPushedItem(Note note);
}
