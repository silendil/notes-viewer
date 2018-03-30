package com.example.pavelhryts.notesviewer.model.notes;

import com.example.pavelhryts.notesviewer.model.BaseModel;


/**
 * Created by Pavel.Hryts on 16.03.2018.
 */

public class Note extends BaseModel {
    private String title;
    private String body;


    private boolean selected = false;

    public Note(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public Note(String title, String body, long id) {
        super(id);
        this.title = title;
        this.body = body;
    }

    public Note() {
        title = "";
        body = "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Note note = (Note) o;

        if (!title.equals(note.title)) return false;
        return body.equals(note.body);
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
