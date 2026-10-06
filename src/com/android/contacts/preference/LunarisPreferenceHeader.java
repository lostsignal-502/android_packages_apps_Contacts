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

public final class LunarisPreferenceHeader {

    private LunarisPreferenceHeader() {
    }

    public static void install(Activity activity, View root, CharSequence title) {
        final ListView list = root.findViewById(android.R.id.list);
        if (list == null) {
            return;
        }
        final int margin = activity.getResources().getDimensionPixelSize(
                R.dimen.lunaris_page_margin);
        list.setDivider(null);
        list.setSelector(android.R.color.transparent);
        list.setPadding(margin, 0, margin, margin);
        list.setClipToPadding(false);
        list.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        final View header = LayoutInflater.from(activity).inflate(
                R.layout.lunaris_preference_header, list, false);
        ((TextView) header.findViewById(R.id.preference_title)).setText(title);
        header.findViewById(R.id.preference_back).setOnClickListener(
                v -> activity.onBackPressed());
        list.addHeaderView(header, null, false);
    }
}
