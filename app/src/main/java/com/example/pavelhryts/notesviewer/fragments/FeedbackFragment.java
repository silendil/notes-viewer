package com.example.pavelhryts.notesviewer.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.pavelhryts.notesviewer.R;
import com.example.pavelhryts.notesviewer.util.handlers.TextChangeHandler;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment{

    private EditText mailText;

    private TextInputLayout til;

    public FeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        mailText = view.findViewById(R.id.mail_text);
        til = view.findViewById(R.id.feedback_layout);
        if(mailText.getText().toString().isEmpty())
            til.setError(getString(R.string.required));
        getActivity().setTitle(getString(R.string.feedback));
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.done){
            Intent sendMail = new Intent(Intent.ACTION_SEND);
            sendMail.setType("text/plain");
            sendMail.putExtra(Intent.EXTRA_EMAIL,getContext().getResources().getString(R.string.owner_email));
            sendMail.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            sendMail.putExtra(Intent.EXTRA_TEXT, mailText.getText());
            startActivity(Intent.createChooser(sendMail,"Send email..."));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.message_editor_option,menu);
        if(mailText.getText().toString().isEmpty())
            menu.getItem(0).setVisible(false);
        mailText.addTextChangedListener(new TextChangeHandler(til, menu, getString(R.string.required)));
        super.onCreateOptionsMenu(menu, inflater);
    }
}
