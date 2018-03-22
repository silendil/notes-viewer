package com.example.pavelhryts.notesviewer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.pavelhryts.notesviewer.model.notes.Note;
import com.example.pavelhryts.notesviewer.model.notes.NoteHolder;

/**
 * Created by Pavel.Hryts on 17.03.2018.
 */

public abstract class ListAdapter extends RecyclerView.Adapter<ListViewHolder> implements ListViewHolder.SelectListener {

    private NoteHolder holder = NoteHolder.getInstance();

    private Context appContext;

    private ListViewHolder viewHolder;

    protected ListAdapter(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        viewHolder = new ListViewHolder(LayoutInflater.from(appContext),parent, this){
            @Override
            public void getPushedItem(Note note) {
                ListAdapter.this.getPushedItem(note);
            }
        };
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return holder.getSize();
    }

    public abstract void getPushedItem(Note note);

}
