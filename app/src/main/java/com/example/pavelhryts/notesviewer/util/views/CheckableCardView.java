package com.example.pavelhryts.notesviewer.util.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.Checkable;

public class CheckableCardView extends CardView implements Checkable{

    private boolean select = false;

    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked,
    };

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);

        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    public CheckableCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableCardView(Context context) {
        super(context);
    }

    @Override
    public void setChecked(boolean b) {
        select = b;
    }

    @Override
    public boolean isChecked() {
        return select;
    }

    @Override
    public void toggle() {
        select = !select;
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }
}
