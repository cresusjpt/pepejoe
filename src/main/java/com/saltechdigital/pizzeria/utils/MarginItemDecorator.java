package com.saltechdigital.pizzeria.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MarginItemDecorator extends RecyclerView.ItemDecoration {

    private int spaceHeiht;

    public MarginItemDecorator(int spaceHeight) {
        this.spaceHeiht = spaceHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = spaceHeiht;
        }
        outRect.left = spaceHeiht;
        outRect.right = spaceHeiht;
        outRect.bottom = spaceHeiht;
    }
}
