/*
 * SPDX-FileCopyrightText: 2026 Lunaris-AOSP
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.contacts.preference;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.contacts.R;

public class LunarisPreferenceRow extends LinearLayout {

    private int mShape = -1;

    public LunarisPreferenceRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!(getParent() instanceof ListView)) {
            return;
        }
        final ListView list = (ListView) getParent();
        final int position = list.getPositionForView(this);
        if (position == ListView.INVALID_POSITION) {
            return;
        }
        final Adapter adapter = list.getAdapter();
        final boolean first = isBoundary(adapter, position - 1);
        final boolean last = isBoundary(adapter, position + 1);
        final int shape = (first ? 1 : 0) | (last ? 2 : 0);
        if (shape != mShape) {
            mShape = shape;
            setBackground(createBackground(first, last));
        }
    }

    private static boolean isBoundary(Adapter adapter, int position) {
        if (position < 0 || position >= adapter.getCount()) {
            return true;
        }
        final Object item = adapter.getItem(position);
        return item == null || item instanceof PreferenceCategory;
    }

    private RippleDrawable createBackground(boolean first, boolean last) {
        final float outer = getResources().getDimension(R.dimen.lunaris_card_radius);
        final float inner = getResources().getDimension(R.dimen.lunaris_card_inner_radius);
        final float top = first ? outer : inner;
        final float bottom = last ? outer : inner;
        final GradientDrawable shape = new GradientDrawable();
        shape.setColor(getContext().getColor(R.color.lunaris_surface_bright));
        shape.setCornerRadii(new float[] {top, top, top, top, bottom, bottom, bottom, bottom});
        final int gap = getResources().getDimensionPixelSize(R.dimen.lunaris_card_gap) / 2;
        final RippleDrawable ripple = new RippleDrawable(ColorStateList.valueOf(
                getContext().getColor(R.color.control_highlight_color)), shape, null);
        ripple.setLayerInset(0, 0, first ? 0 : gap, 0, last ? 0 : gap);
        return ripple;
    }
}
