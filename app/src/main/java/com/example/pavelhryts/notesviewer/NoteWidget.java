package com.example.pavelhryts.notesviewer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.pavelhryts.notesviewer.activities.MessageActivity;
import com.example.pavelhryts.notesviewer.services.NotesWidgetService;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static com.example.pavelhryts.notesviewer.util.Consts.MESSAGE;

/**
 * Implementation of App Widget functionality.
 */
public class NoteWidget extends AppWidgetProvider {

    public static final String UPDATE_WIDGET_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";
    public static final String ITEM_ON_CLICK_ACTION = "android.appwidget.action.ITEM_ON_CLICK";
    public static final String BUTTON_ON_CLICK_ACTION = "android.appwidget.action.WIDGET_BUTTON";

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.note_widget_info);

        initList(views, context, appWidgetId);
        initOnClick(views, context, appWidgetId, appWidgetManager);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        if(UPDATE_WIDGET_ACTION.equalsIgnoreCase(intent.getAction())){
            int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(context, NoteWidget.class));
            manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        } else if (ITEM_ON_CLICK_ACTION.equalsIgnoreCase(intent.getAction()) ){
            Intent messageIntent = new Intent(context, MessageActivity.class);
            if(intent.hasExtra(MESSAGE)){
                messageIntent.putExtra(MESSAGE,intent.getIntExtra(MESSAGE,-1));
            } else{
                messageIntent.putExtra(MESSAGE, -1);
            }
            Log.d("widget","message intent message = " + messageIntent.getExtras().getInt(MESSAGE));
            PendingIntent starter = PendingIntent.getActivity(context, 0, messageIntent, 0);
            context.startActivity(messageIntent);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private void initList(RemoteViews views, Context context, int appWidgetId){
        Intent adapter = new Intent(context, NotesWidgetService.class);
        adapter.putExtra(EXTRA_APPWIDGET_ID, appWidgetId);
        views.setRemoteAdapter(R.id.widget_list, adapter);
        views.setEmptyView(R.id.widget_list, R.id.empty_text);
    }

    private void initOnClick(RemoteViews views, Context context, int appWidgetId, AppWidgetManager appWidgetManager){
        Intent listClickIntent = new Intent(context, NoteWidget.class);
        listClickIntent.setAction(ITEM_ON_CLICK_ACTION);
        PendingIntent listClickPendingIntent = PendingIntent.getBroadcast(context, 0, listClickIntent,0);
        views.setPendingIntentTemplate(R.id.widget_list, listClickPendingIntent);

        Intent clickIntent = new Intent(context, MessageActivity.class);
        clickIntent.putExtra(MESSAGE, -1);
        clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        Log.d("widget",clickIntent.getExtras().toString());
        PendingIntent clickPendingIntent = PendingIntent.getActivity(context, 0, clickIntent,0);
        views.setOnClickPendingIntent(R.id.new_note_button, clickPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

