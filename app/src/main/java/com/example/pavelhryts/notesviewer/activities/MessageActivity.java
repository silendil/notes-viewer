package com.example.pavelhryts.notesviewer.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.pavelhryts.notesviewer.R;
import com.example.pavelhryts.notesviewer.model.db.NotesTable;
import com.example.pavelhryts.notesviewer.model.notes.Note;
import com.example.pavelhryts.notesviewer.model.notes.NoteHolder;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.example.pavelhryts.notesviewer.util.Consts.MESSAGE;

/**
 * Created by Pavel.Hryts on 16.03.2018.
 */

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    private Note message;
    private boolean newMessage = false;

    private EditText title, body;

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
        title.setText(message.getTitle());
        body.setText(message.getBody());
        Button submit = findViewById(R.id.submit_message);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submit_message:
                message.setTitle(title.getText().toString());
                message.setBody(body.getText().toString());
                if(newMessage)
                    NoteHolder.getInstance().addNote(message);
                else
                    NotesTable.getInstance().edit(message);
                setResult(RESULT_OK);
                finish();
                break;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }
}
