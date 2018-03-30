package com.example.pavelhryts.notesviewer.model;

import java.io.Serializable;

/**
 * Created by Pavel.Hryts on 30.03.2018.
 */

public abstract class BaseModel implements Serializable {
    private long id;

    public BaseModel(long id) {
        this.id = id;
    }

    public BaseModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
