package com.example.pavelhryts.notesviewer.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pavelhryts.notesviewer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment implements View.OnClickListener {


    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.about_info,container,false);
    }

    @Override
    public void onClick(View view) {

    }
}
