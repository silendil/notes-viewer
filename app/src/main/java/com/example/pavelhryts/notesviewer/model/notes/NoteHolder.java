package com.example.pavelhryts.notesviewer.model.notes;

import com.example.pavelhryts.notesviewer.model.db.NotesTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Pavel.Hryts on 17.03.2018.
 */

public class NoteHolder {

    private NoteHolder() {
    }

    private NotesTable notesTable = NotesTable.getInstance();

    private static volatile NoteHolder instance;

    public synchronized static NoteHolder getInstance(){
        if(instance == null)
            instance = new NoteHolder();
        return instance;
    }

    public void initNotesFromDB(){
        notes = notesTable.getAll();
    }

    public void initDB(){
        for(Note note : notes)
            notesTable.edit(note);
    }

    public void setNotes(List<Note> notes){
        this.notes = new ArrayList<>(notes);
    }

    private List<Note> notes = new ArrayList<>();

    public List<Note> getNotes() {
        return Collections.unmodifiableList(notes);
    }

    public void addNote(Note note) {
        notesTable.add(note);
        notes.add(note);
    }

    public void removeNote(int index) {
        Note note = getNote(index);
        notesTable.delete(note);
        notes.remove(index);
    }

    public void removeNotes(List<Note> forDelete) {
        for(Note note : forDelete){
            notesTable.delete(note);
        }
        notes.removeAll(forDelete);
    }

    public int getSize(){
        return notes.size();
    }

    public Note getNote(int i){
        if(i>=0 && i < notes.size())
            return notes.get(i);
        return null;
    }

    public boolean isEmpty(){
        return notes.isEmpty();
    }

    public void clearNotes(){
        notesTable.deleteAll();
        notes.clear();
    }

    public List<Note> getSelectedNotes(){
        List<Note> result = new ArrayList<>();
        for(Note note: notes)
            if(note.isSelected())
                result.add(note);
        return result;
    }

    public int getSelectedNotesCount(){
        return getSelectedNotes().size();
    }

    public void selectNone(){
        for(Note note : notes)
            note.setSelected(false);
    }

    public int indexOf(Note note) {
        return notes.indexOf(note);
    }
}
