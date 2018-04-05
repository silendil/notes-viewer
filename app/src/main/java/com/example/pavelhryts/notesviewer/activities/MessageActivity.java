package com.example.pavelhryts.notesviewer.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.pavelhryts.notesviewer.R;
import com.example.pavelhryts.notesviewer.model.db.NotesDAO;
import com.example.pavelhryts.notesviewer.model.notes.Note;
import com.example.pavelhryts.notesviewer.model.notes.NoteHolder;
import com.example.pavelhryts.notesviewer.util.handlers.TextChangeHandler;

import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.example.pavelhryts.notesviewer.util.Consts.MESSAGE;

/**
 * Created by Pavel.Hryts on 16.03.2018.
 */

public class MessageActivity extends AppCompatActivity{
    private Note message;
    private boolean newMessage = false;

    private EditText title, body;
    private TextInputLayout til;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_message);
        title = findViewById(R.id.message_title);
        body = findViewById(R.id.message_body);
        Intent intent = getIntent();
        if(intent.hasExtra(MESSAGE))
            message = NoteHolder.getInstance().getNote(intent.getExtras().getInt(MESSAGE));
        if(message == null){
            newMessage = true;
            message = new Note();
        }
        til = findViewById(R.id.title_layout);
        title.setText(message.getTitle());
        if(title.getText().toString().isEmpty())
            til.setError(getString(R.string.required));
        body.setText(message.getBody());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_editor_option,menu);
        if(title.getText().toString().isEmpty())
            menu.getItem(0).setVisible(false);
        title.addTextChangedListener(new TextChangeHandler(til, menu, getString(R.string.required)));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.done){
            message.setTitle(title.getText().toString());
            message.setBody(body.getText().toString());
            if(newMessage)
                NoteHolder.getInstance().addNote(message);
            else
                NotesDAO.getInstance().edit(message);
            setResult(RESULT_OK);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
