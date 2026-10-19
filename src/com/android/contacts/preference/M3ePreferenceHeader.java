/*
 * SPDX-FileCopyrightText: 2026 Lunaris-AOSP
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.contacts.preference;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.contacts.R;

public final class M3ePreferenceHeader {

    private M3ePreferenceHeader() {
    }

    public static void install(Activity activity, View root, CharSequence title) {
        install(activity, root, title, false);
    }

    /** Like {@link #install}, optionally with the expressive hero card under the title. */
    public static void install(Activity activity, View root, CharSequence title,
            boolean showHero) {
        final ListView list = root.findViewById(android.R.id.list);
        if (list == null) {
            return;
        }
        final int margin = activity.getResources().getDimensionPixelSize(
                R.dimen.m3e_page_margin);
        list.setDivider(null);
        list.setSelector(android.R.color.transparent);
        list.setPadding(margin, 0, margin, margin);
        list.setClipToPadding(false);
        list.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        final View header = LayoutInflater.from(activity).inflate(
                R.layout.m3e_preference_header, list, false);
        ((TextView) header.findViewById(R.id.preference_title)).setText(title);
        header.findViewById(R.id.preference_hero).setVisibility(
                showHero ? View.VISIBLE : View.GONE);
        header.findViewById(R.id.preference_back).setOnClickListener(
                v -> activity.onBackPressed());
        list.addHeaderView(header, null, false);
    }
}
