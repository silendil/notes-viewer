package com.example.pavelhryts.notesviewer.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.pavelhryts.notesviewer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment implements View.OnClickListener{

    private EditText mailText;

    public FeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        mailText = view.findViewById(R.id.mail_text);
        Button send = view.findViewById(R.id.send_mail);
        send.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View view) {
        Intent sendMail = new Intent(Intent.ACTION_SEND);
        sendMail.setType("text/plain");
        sendMail.putExtra(Intent.EXTRA_EMAIL,getContext().getResources().getString(R.string.owner_email));
        sendMail.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
        sendMail.putExtra(Intent.EXTRA_TEXT, mailText.getText());
        startActivity(Intent.createChooser(sendMail,"Send email..."));
    }
}
