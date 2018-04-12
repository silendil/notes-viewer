package com.example.pavelhryts.notesviewer.adapters;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.pavelhryts.notesviewer.R;
import com.example.pavelhryts.notesviewer.activities.MessageActivity;
import com.example.pavelhryts.notesviewer.model.notes.Note;
import com.example.pavelhryts.notesviewer.model.notes.NoteHolder;

import java.util.ArrayList;
import java.util.List;

import static com.example.pavelhryts.notesviewer.util.Consts.MESSAGE;

public class WidgetNotesFactory implements RemoteViewsService.RemoteViewsFactory {

    private NoteHolder noteHolder = NoteHolder.getInstance();
    private Context context;
    private List<Note> notes = new ArrayList<>();

    public WidgetNotesFactory(Context context){
        this.context = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        notes.clear();
        notes = new ArrayList<>(noteHolder.getNotes());
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
        remoteViews.setTextViewText(R.id.widget_item_title,noteHolder.getNote(position).getTitle());
        remoteViews.setTextViewText(R.id.widget_item_body,noteHolder.getNote(position).getBody());

        Intent clickIntent = new Intent();
        clickIntent.putExtra(MESSAGE,position);
        remoteViews.setOnClickFillInIntent(R.id.widget_item_title, clickIntent);
        remoteViews.setOnClickFillInIntent(R.id.widget_item_body, clickIntent);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
