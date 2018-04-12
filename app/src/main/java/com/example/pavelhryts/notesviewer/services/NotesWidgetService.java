package com.example.pavelhryts.notesviewer.services;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.example.pavelhryts.notesviewer.adapters.WidgetNotesFactory;

public class NotesWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetNotesFactory(getApplicationContext());
    }
}
