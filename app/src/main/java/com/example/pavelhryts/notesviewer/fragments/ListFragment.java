package com.example.pavelhryts.notesviewer.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pavelhryts.notesviewer.R;
import com.example.pavelhryts.notesviewer.activities.MessageActivity;
import com.example.pavelhryts.notesviewer.adapters.ListAdapter;
import com.example.pavelhryts.notesviewer.model.notes.Note;
import com.example.pavelhryts.notesviewer.model.notes.NoteHolder;

import static com.example.pavelhryts.notesviewer.util.Consts.MESSAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment implements View.OnClickListener {

    private static final int CREATE_MESSAGE = 1;
    private RecyclerView list;
    private NoteHolder noteHolder = NoteHolder.getInstance();
    private TextView emptyMessage;
    private int pushedItemId;
    private Menu optionMenu;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment,container,false);
        init(view);
        return view;
    }

    private void init(View view){
        list = view.findViewById(R.id.list_view);
        emptyMessage = view.findViewById(R.id.empty_message);
        list.setAdapter(new ListAdapter(getContext()) {
            @Override
            public void onSelect(Note note) {
                updateMenuView();
            }

            @Override
            public void getPushedItem(Note note) {
                pushedItemId = noteHolder.indexOf(note);
            }
        });
        list.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        registerForContextMenu(list);
        checkListVisibility();
    }

    private void checkListVisibility() {
        if(noteHolder.isEmty()){
            list.setVisibility(View.INVISIBLE);
            emptyMessage.setVisibility(View.VISIBLE);
        }else{
            list.setVisibility(View.VISIBLE);
            emptyMessage.setVisibility(View.INVISIBLE);
        }
    }

    private void showPopupMenu(View view){
        PopupMenu menu = new PopupMenu(getContext(), view);
        menu.inflate(R.menu.popup_menu);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add_message_popup:
                        showMessageEditor(-1);
                        break;
                    case R.id.clear_notes_popup:
                        noteHolder.clearNotes();
                        updateView();
                        break;
                    case R.id.clear_selection_popup:
                        noteHolder.selectNone();
                        updateView();
                        break;
                }
                return false;
            }
        });
        menu.show();
    }

    public void updateView(){
        list.getAdapter().notifyDataSetChanged();
        updateMenuView();
        checkListVisibility();
    }

    private void showMessageEditor(int index){
        Intent messageIntent = new Intent(getActivity(), MessageActivity.class);
        messageIntent.putExtra(MESSAGE, index);
        startActivityForResult(messageIntent, CREATE_MESSAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        noteHolder.selectNone();
        updateView();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.edit_message_context:
                showMessageEditor(pushedItemId);
                break;
            case R.id.delete_message_context:
                deletePushedNote(pushedItemId);
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        showPopupMenu(view);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        optionMenu = menu;
        updateMenuView();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateMenuView(){
        if(optionMenu != null){
            if (noteHolder.getSelectedNotesCount() > 0) {
                optionMenu.getItem(2).setVisible(true);
                if (noteHolder.getSelectedNotesCount() == 1) {
                    optionMenu.getItem(1).setVisible(true);
                }
                else {
                    optionMenu.getItem(1).setVisible(false);
                }
            }
            if (noteHolder.getSelectedNotesCount() == 0) {
                optionMenu.getItem(1).setVisible(false);
                optionMenu.getItem(2).setVisible(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.add_message:
                showMessageEditor(-1);
                break;
            case R.id.clear_notes:
                clearNotes();
                break;
            case R.id.delete_message:
                deleteNotes();
                break;
            case R.id.edit_message:
                showMessageEditor(noteHolder.indexOf(noteHolder.getSelectedNotes().get(0)));
                break;
            case R.id.clear_selection:
                clearSelection();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearNotes() {
        noteHolder.clearNotes();
        updateView();
    }

    private void deleteNotes(){
        noteHolder.removeNotes(noteHolder.getSelectedNotes());
        noteHolder.selectNone();
        updateView();
    }

    private void clearSelection(){
        noteHolder.selectNone();
        updateView();
    }

    private void deletePushedNote(int id){
        noteHolder.removeNote(id);
        noteHolder.selectNone();
        updateView();
    }
}
