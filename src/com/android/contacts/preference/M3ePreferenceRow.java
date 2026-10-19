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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.contacts.R;

public class M3ePreferenceRow extends LinearLayout {

    // Icon emblems cycle through the container roles, like the theme store's categories.
    private static final int[][] EMBLEM_COLORS = {
            {R.color.m3e_primary_container, R.color.m3e_on_primary_container},
            {R.color.m3e_secondary_container, R.color.m3e_on_secondary_container},
            {R.color.m3e_tertiary_container, R.color.m3e_on_tertiary_container},
    };

    private int mShape = -1;

    public M3ePreferenceRow(Context context, AttributeSet attrs) {
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
        updateEmblem();
        final int shape = (first ? 1 : 0) | (last ? 2 : 0);
        if (shape != mShape) {
            mShape = shape;
            setBackground(createBackground(first, last));
        }
    }

    private void updateEmblem() {
        final ImageView icon = findViewById(android.R.id.icon);
        final TextView title = findViewById(android.R.id.title);
        if (icon == null || title == null) {
            return;
        }
        final int[] colors = EMBLEM_COLORS[(title.getText().toString().hashCode()
                & Integer.MAX_VALUE) % EMBLEM_COLORS.length];
        icon.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(colors[0])));
        icon.setImageTintList(ColorStateList.valueOf(getContext().getColor(colors[1])));
    }

    private static boolean isBoundary(Adapter adapter, int position) {
        if (position < 0 || position >= adapter.getCount()) {
            return true;
        }
        final Object item = adapter.getItem(position);
        return item == null || item instanceof PreferenceCategory;
    }

    private RippleDrawable createBackground(boolean first, boolean last) {
        final float outer = getResources().getDimension(R.dimen.m3e_card_radius);
        final float inner = getResources().getDimension(R.dimen.m3e_card_inner_radius);
        final float top = first ? outer : inner;
        final float bottom = last ? outer : inner;
        final GradientDrawable shape = new GradientDrawable();
        shape.setColor(getContext().getColor(R.color.m3e_surface_container_low));
        shape.setCornerRadii(new float[] {top, top, top, top, bottom, bottom, bottom, bottom});
        final int gap = getResources().getDimensionPixelSize(R.dimen.m3e_segment_gap) / 2;
        final RippleDrawable ripple = new RippleDrawable(ColorStateList.valueOf(
                getContext().getColor(R.color.control_highlight_color)), shape, null);
        ripple.setLayerInset(0, 0, first ? 0 : gap, 0, last ? 0 : gap);
        return ripple;
    }
}
