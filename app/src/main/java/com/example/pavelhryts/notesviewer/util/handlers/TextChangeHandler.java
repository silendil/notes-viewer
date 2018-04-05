package com.example.pavelhryts.notesviewer.util.handlers;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;

import com.example.pavelhryts.notesviewer.R;

public class TextChangeHandler implements TextWatcher {

    private TextInputLayout til;
    private Menu optionMenu;
    private String errorMessage;

    public TextChangeHandler(TextInputLayout til, Menu optionMenu, String errorMessage) {
        this.til = til;
        this.optionMenu = optionMenu;
        this.errorMessage = errorMessage;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(charSequence.toString().isEmpty()){
            til.setError(errorMessage);
            optionMenu.getItem(0).setVisible(false);
        }else{
            til.setError(null);
            optionMenu.getItem(0).setVisible(true);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
