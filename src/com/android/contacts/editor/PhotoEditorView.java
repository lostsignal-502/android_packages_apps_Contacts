/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.contacts.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.contacts.ContactPhotoManager;
import com.android.contacts.R;
import com.android.contacts.model.ValuesDelta;
import com.android.contacts.util.MaterialColorMapUtils.MaterialPalette;
import com.android.contacts.widget.QuickContactImageView;

/**
 * Displays a photo and calls the host back when the user clicks it.
 */
public class PhotoEditorView extends RelativeLayout implements View.OnClickListener {

    /**
     * Callbacks for the host of this view.
     */
    public interface Listener {

        /**
         * Invoked when the user wants to change their photo.
         */
        void onPhotoEditorViewClicked();
    }

    private Listener mListener;


    private QuickContactImageView mPhotoImageView;
    private View mPhotoIcon;
    private View mPhotoTouchInterceptOverlay;
    private MaterialPalette mMaterialPalette;

    private boolean mReadOnly;
    private boolean mIsNonDefaultPhotoBound;

    public PhotoEditorView(Context context) {
        this(context, null);
    }

    public PhotoEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPhotoImageView = (QuickContactImageView) findViewById(R.id.photo);
        mPhotoIcon = findViewById(R.id.photo_icon);
        mPhotoTouchInterceptOverlay = findViewById(R.id.photo_touch_intercept_overlay);

    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void setReadOnly(boolean readOnly) {
        mReadOnly = readOnly;
        if (mReadOnly) {
            mPhotoIcon.setVisibility(View.GONE);
            mPhotoTouchInterceptOverlay.setClickable(false);
            mPhotoTouchInterceptOverlay.setContentDescription(getContext().getString(
                    R.string.editor_contact_photo_content_description));
        } else {
            mPhotoIcon.setVisibility(View.VISIBLE);
            mPhotoTouchInterceptOverlay.setOnClickListener(this);
            updatePhotoDescription();
        }
    }

    public void setPalette(MaterialPalette palette) {
        mMaterialPalette = palette;
    }

    /**
     * Tries to bind a full size photo or a bitmap loaded from the given ValuesDelta,
     * and falls back to the default avatar, tinted using the given MaterialPalette (if it's not
     * null);
     */
    public void setPhoto(ValuesDelta valuesDelta) {
        // Check if we can update to the full size photo immediately
        final Long photoFileId = EditorUiUtils.getPhotoFileId(valuesDelta);
        if (photoFileId != null) {
            final Uri photoUri = ContactsContract.DisplayPhoto.CONTENT_URI.buildUpon()
                    .appendPath(photoFileId.toString()).build();
            setFullSizedPhoto(photoUri);
            return;
        }

        // Use the bitmap image from the values delta
        final Bitmap bitmap = EditorUiUtils.getPhotoBitmap(valuesDelta);
        if (bitmap != null) {
            setPhoto(bitmap);
            return;
        }

        setDefaultPhoto(mMaterialPalette);
    }


    /**
     * Whether a removable, non-default photo is bound to this view.
     */
    public boolean isWritablePhotoSet() {
        return !mReadOnly && mIsNonDefaultPhotoBound;
    }

    /**
     * Binds the given bitmap.
     */
    private void setPhoto(Bitmap bitmap) {
        mPhotoImageView.setImageBitmap(bitmap);
        mIsNonDefaultPhotoBound = true;
        updatePhotoDescription();
    }

    private void setDefaultPhoto(MaterialPalette materialPalette) {
        mIsNonDefaultPhotoBound = false;
        updatePhotoDescription();
        EditorUiUtils.setDefaultPhoto(mPhotoImageView, getResources(), materialPalette);
    }

    private void updatePhotoDescription() {
        mPhotoTouchInterceptOverlay.setContentDescription(getContext().getString(
                mIsNonDefaultPhotoBound
                        ? R.string.editor_change_photo_content_description
                        : R.string.editor_add_photo_content_description));
    }
    /**
     * Binds a full size photo loaded from the given Uri.
     */
    public void setFullSizedPhoto(Uri photoUri) {
        mPhotoImageView.setImageURI(photoUri);
        mIsNonDefaultPhotoBound = true;
        updatePhotoDescription();
    }

    /**
     * Removes the current bound photo bitmap.
     */
    public void removePhoto() {
        setDefaultPhoto(mMaterialPalette);
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            mListener.onPhotoEditorViewClicked();
        }
    }
}
