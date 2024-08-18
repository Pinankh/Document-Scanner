package com.camscanner.paperscanner.pdfcreator.view.element.tagsedittext;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.view.element.tagsedittext.utils.ResourceUtils;

public class TagsEditText extends EditText implements View.OnKeyListener {
    private static final String ALLOW_SPACES_IN_TAGS = "allowSpacesInTags";
    private static final String DRAWABLE_PADDING = "drawablePadding";
    private static final String LAST_STRING = "lastString";
    private static final String LEFT_DRAWABLE_RESOURCE = "leftDrawable";
    public static final String NEW_LINE = "\n";
    private static final String RIGHT_DRAWABLE_RESOURCE = "rightDrawable";
    private static final String SUPER_STATE = "superState";
    private static final String TAGS = "tags";
    private static final String TAGS_BACKGROUND_RESOURCE = "tagsBackground";
    private static final String TAGS_TEXT_COLOR = "tagsTextColor";
    private static final String TAGS_TEXT_SIZE = "tagsTextSize";
    private static final String UNDER_CONSTRUCTION_TAG = "underConstructionTag";
    private int mDrawablePadding;
    /* access modifiers changed from: private */
    public boolean mIsAfterTextWatcherEnabled = true;
    private boolean mIsSetTextDisabled = false;
    private boolean mIsSpacesAllowedInTags = false;
    private String mLastString = "";
    private Drawable mLeftDrawable;
    private int mLeftDrawableResouce = 0;
    private TagsEditListener mListener;
    private Drawable mRightDrawable;
    private int mRightDrawableResouce = 0;
    private String mSeparator = " ";
    private List<TagSpan> mTagSpans = new ArrayList();
    private List<Tag> mTags = new ArrayList();
    private Drawable mTagsBackground;
    private int mTagsBackgroundResource = 0;
    private int mTagsPaddingBottom;
    private int mTagsPaddingLeft;
    private int mTagsPaddingRight;
    private int mTagsPaddingTop;
    private int mTagsTextColor;
    private float mTagsTextSize;
    /* access modifiers changed from: private */
    public final TextWatcher mTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (TagsEditText.this.mIsAfterTextWatcherEnabled) {
                TagsEditText.this.setTags();
            }
        }
    };

    public interface TagsEditListener {
        void onEditingFinished();

        void onTagsChanged(Collection<String> collection);
    }

    public List<String> getTags() {
        return convertTagSpanToList(this.mTagSpans);
    }

    public void setSeparator(String separator) {
        this.mSeparator = separator;
    }

    public TagsEditText(Context context) {
        super(context);
        init((AttributeSet) null, 0, 0);
        setSingleLine();
        setMaxLines(1);
    }

    public TagsEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
        setSingleLine();
        setMaxLines(1);
    }

    public TagsEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
        setSingleLine();
        setMaxLines(1);
    }

    @TargetApi(21)
    public TagsEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    /* access modifiers changed from: protected */
    public void onSelectionChanged(int selStart, int selEnd) {
        if (getText() != null) {
            setSelection(getText().length());
        } else {
            super.onSelectionChanged(selStart, selEnd);
        }
    }

    public void setText(CharSequence text, TextView.BufferType type) {
        if (!this.mIsSetTextDisabled) {
            if (!TextUtils.isEmpty(text)) {
                String source = this.mIsSpacesAllowedInTags ? text.toString().trim() : text.toString().replaceAll(" ", "");
                if (this.mTags.isEmpty()) {
                    Tag tag = new Tag();
                    tag.setIndex(0);
                    tag.setPosition(0);
                    tag.setSource(source);
                    tag.setSpan(true);
                    this.mTags.add(tag);
                } else {
                    int size = this.mTags.size();
                    Tag lastTag = this.mTags.get(size - 1);
                    if (!lastTag.isSpan()) {
                        lastTag.setSource(source);
                        lastTag.setSpan(true);
                    } else {
                        Tag newTag = new Tag();
                        newTag.setIndex(size);
                        newTag.setPosition(lastTag.getPosition() + lastTag.getSource().length() + 1);
                        newTag.setSource(source);
                        newTag.setSpan(true);
                        this.mTags.add(newTag);
                    }
                }
                buildStringWithTags(this.mTags);
                this.mTextWatcher.afterTextChanged(getText());
                return;
            }
            super.setText(text, type);
        }
    }

    public void setTags(CharSequence... tags) {
        this.mTagSpans.clear();
        this.mTags.clear();
        int length = tags != null ? tags.length : 0;
        int position = 0;
        for (int i = 0; i < length; i++) {
            Tag tag = new Tag();
            tag.setIndex(i);
            tag.setPosition(position);
            String source = this.mIsSpacesAllowedInTags ? tags[i].toString().trim() : tags[i].toString().replaceAll(" ", "");
            tag.setSource(source);
            tag.setSpan(true);
            this.mTags.add(tag);
            position += source.length() + 1;
        }
        buildStringWithTags(this.mTags);
        this.mTextWatcher.afterTextChanged(getText());
    }

    public void setTags(String[] tags) {
        this.mTagSpans.clear();
        this.mTags.clear();
        int length = tags != null ? tags.length : 0;
        int position = 0;
        for (int i = 0; i < length; i++) {
            Tag tag = new Tag();
            tag.setIndex(i);
            tag.setPosition(position);
            String source = this.mIsSpacesAllowedInTags ? tags[i].trim() : tags[i].replaceAll(" ", "");
            tag.setSource(source);
            tag.setSpan(true);
            this.mTags.add(tag);
            position += source.length() + 1;
        }
        buildStringWithTags(this.mTags);
        this.mTextWatcher.afterTextChanged(getText());
    }

    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_STATE, super.onSaveInstanceState());
        Tag[] tags = new Tag[this.mTags.size()];
        this.mTags.toArray(tags);
        bundle.putParcelableArray(TAGS, tags);
        bundle.putString(LAST_STRING, this.mLastString);
        bundle.putString(UNDER_CONSTRUCTION_TAG, getNewTag(getText().toString()));
        bundle.putInt(TAGS_TEXT_COLOR, this.mTagsTextColor);
        bundle.putInt(TAGS_BACKGROUND_RESOURCE, this.mTagsBackgroundResource);
        bundle.putFloat(TAGS_TEXT_SIZE, this.mTagsTextSize);
        bundle.putInt(LEFT_DRAWABLE_RESOURCE, this.mLeftDrawableResouce);
        bundle.putInt(RIGHT_DRAWABLE_RESOURCE, this.mRightDrawableResouce);
        bundle.putInt(DRAWABLE_PADDING, this.mDrawablePadding);
        bundle.putBoolean(ALLOW_SPACES_IN_TAGS, this.mIsSpacesAllowedInTags);
        return bundle;
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Context context = getContext();
            Bundle bundle = (Bundle) state;
            this.mTagsTextColor = bundle.getInt(TAGS_TEXT_COLOR, this.mTagsTextColor);
            this.mTagsBackgroundResource = bundle.getInt(TAGS_BACKGROUND_RESOURCE, this.mTagsBackgroundResource);
            int i = this.mTagsBackgroundResource;
            if (i != 0) {
                this.mTagsBackground = ContextCompat.getDrawable(context, i);
            }
            this.mTagsTextSize = bundle.getFloat(TAGS_TEXT_SIZE, this.mTagsTextSize);
            this.mLeftDrawableResouce = bundle.getInt(LEFT_DRAWABLE_RESOURCE, this.mLeftDrawableResouce);
            int i2 = this.mLeftDrawableResouce;
            if (i2 != 0) {
                this.mLeftDrawable = ContextCompat.getDrawable(context, i2);
            }
            this.mRightDrawableResouce = bundle.getInt(RIGHT_DRAWABLE_RESOURCE, this.mRightDrawableResouce);
            int i3 = this.mRightDrawableResouce;
            if (i3 != 0) {
                this.mRightDrawable = ContextCompat.getDrawable(context, i3);
            }
            this.mDrawablePadding = bundle.getInt(DRAWABLE_PADDING, this.mDrawablePadding);
            this.mIsSpacesAllowedInTags = bundle.getBoolean(ALLOW_SPACES_IN_TAGS, this.mIsSpacesAllowedInTags);
            this.mLastString = bundle.getString(LAST_STRING);
            Parcelable[] tagsParcelables = bundle.getParcelableArray(TAGS);
            if (tagsParcelables != null) {
                Tag[] tags = new Tag[tagsParcelables.length];
                System.arraycopy(tagsParcelables, 0, tags, 0, tagsParcelables.length);
                this.mTags = new ArrayList();
                Collections.addAll(this.mTags, tags);
                buildStringWithTags(this.mTags);
                this.mTextWatcher.afterTextChanged(getText());
            }
            Parcelable state2 = bundle.getParcelable(SUPER_STATE);
            this.mIsSetTextDisabled = true;
            super.onRestoreInstanceState(state2);
            this.mIsSetTextDisabled = false;
            String temp = bundle.getString(UNDER_CONSTRUCTION_TAG);
            if (!TextUtils.isEmpty(temp)) {
                getText().append(temp);
                return;
            }
            return;
        }
        super.onRestoreInstanceState(state);
    }

    private void buildStringWithTags(List<Tag> tags) {
        this.mIsAfterTextWatcherEnabled = false;
        getText().clear();
        for (Tag tag : tags) {
            getText().append(tag.getSource()).append(this.mSeparator);
        }
        this.mLastString = getText().toString();
        if (!TextUtils.isEmpty(this.mLastString)) {
            getText().append(NEW_LINE);
        }
        this.mIsAfterTextWatcherEnabled = true;
    }

    public void setTagsTextColor(@ColorRes int color) {
        this.mTagsTextColor = getColor(getContext(), color);
        setTags(convertTagSpanToArray(this.mTagSpans));
    }

    public void setTagsTextSize(@DimenRes int textSize) {
        this.mTagsTextSize = ResourceUtils.getDimension(getContext(), textSize);
        setTags(convertTagSpanToArray(this.mTagSpans));
    }

    public void setTagsBackground(@DrawableRes int drawable) {
        this.mTagsBackground = ContextCompat.getDrawable(getContext(), drawable);
        this.mTagsBackgroundResource = drawable;
        setTags(convertTagSpanToArray(this.mTagSpans));
    }

    public void setCloseDrawableLeft(@DrawableRes int drawable) {
        this.mLeftDrawable = ContextCompat.getDrawable(getContext(), drawable);
        this.mLeftDrawableResouce = drawable;
        setTags(convertTagSpanToArray(this.mTagSpans));
    }

    public void setCloseDrawableRight(@DrawableRes int drawable) {
        this.mRightDrawable = ContextCompat.getDrawable(getContext(), drawable);
        this.mRightDrawableResouce = drawable;
        setTags(convertTagSpanToArray(this.mTagSpans));
    }

    public void setCloseDrawablePadding(@DimenRes int padding) {
        this.mDrawablePadding = ResourceUtils.getDimensionPixelSize(getContext(), padding);
        setTags(convertTagSpanToArray(this.mTagSpans));
    }

    public void setTagsWithSpacesEnabled(boolean isSpacesAllowedInTags) {
        this.mIsSpacesAllowedInTags = isSpacesAllowedInTags;
        setTags(convertTagSpanToArray(this.mTagSpans));
    }

    public void setTagsListener(TagsEditListener listener) {
        this.mListener = listener;
    }

    @ColorInt
    private int getColor(Context context, @ColorRes int colorId) {
        if (Build.VERSION.SDK_INT >= 23) {
            return ContextCompat.getColor(context, colorId);
        }
        return context.getResources().getColor(colorId);
    }

    private void init(@Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        Context context = getContext();
        if (attrs == null) {
            this.mIsSpacesAllowedInTags = false;
            this.mTagsTextColor = getColor(context, R.color.defaultTagsTextColor);
            this.mTagsTextSize = (float) ResourceUtils.getDimensionPixelSize(context, R.dimen.defaultTagsTextSize);
            this.mTagsBackground = ContextCompat.getDrawable(context, R.drawable.oval);
            this.mRightDrawable = ContextCompat.getDrawable(context, R.drawable.tag_close);
            this.mDrawablePadding = ResourceUtils.getDimensionPixelSize(context, R.dimen.defaultTagsCloseImagePadding);
            this.mTagsPaddingRight = ResourceUtils.getDimensionPixelSize(context, R.dimen.defaultTagsPadding);
            this.mTagsPaddingLeft = ResourceUtils.getDimensionPixelSize(context, R.dimen.defaultTagsPadding);
            this.mTagsPaddingTop = ResourceUtils.getDimensionPixelSize(context, R.dimen.defaultTagsPadding);
            this.mTagsPaddingBottom = ResourceUtils.getDimensionPixelSize(context, R.dimen.defaultTagsPadding);
        } else {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TagsEditText, defStyleAttr, defStyleRes);
            try {
                this.mIsSpacesAllowedInTags = typedArray.getBoolean(0, false);
                this.mTagsTextColor = typedArray.getColor(9, getColor(context, R.color.defaultTagsTextColor));
                this.mTagsTextSize = (float) typedArray.getDimensionPixelSize(10, ResourceUtils.getDimensionPixelSize(context, R.dimen.defaultTagsTextSize));
                this.mTagsBackground = typedArray.getDrawable(1);
                this.mRightDrawable = typedArray.getDrawable(4);
                this.mLeftDrawable = typedArray.getDrawable(2);
                this.mDrawablePadding = typedArray.getDimensionPixelOffset(3, ResourceUtils.getDimensionPixelSize(context, R.dimen.defaultTagsCloseImagePadding));
                this.mTagsPaddingRight = typedArray.getDimensionPixelSize(7, ResourceUtils.getDimensionPixelSize(context, R.dimen.defaultTagsPadding));
                this.mTagsPaddingLeft = typedArray.getDimensionPixelSize(6, ResourceUtils.getDimensionPixelSize(context, R.dimen.defaultTagsPadding));
                this.mTagsPaddingTop = typedArray.getDimensionPixelSize(8, ResourceUtils.getDimensionPixelSize(context, R.dimen.defaultTagsPadding));
                this.mTagsPaddingBottom = typedArray.getDimensionPixelSize(5, ResourceUtils.getDimensionPixelSize(context, R.dimen.defaultTagsPadding));
            } finally {
                typedArray.recycle();
            }
        }
        setMovementMethod(LinkMovementMethod.getInstance());
        setInputType(655361);
        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= 16) {
                        TagsEditText.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        TagsEditText.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                    TagsEditText tagsEditText = TagsEditText.this;
                    tagsEditText.addTextChangedListener(tagsEditText.mTextWatcher);
                    TagsEditText.this.mTextWatcher.afterTextChanged(TagsEditText.this.getText());
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void setTags() {
        TagsEditListener tagsEditListener;
        this.mIsAfterTextWatcherEnabled = false;
        boolean isEnterClicked = false;
        Editable editable = getText();
        String str = editable.toString();
        if (str.endsWith(NEW_LINE)) {
            isEnterClicked = true;
        }
        boolean isDeleting = this.mLastString.length() > str.length();
        if (this.mLastString.endsWith(this.mSeparator) && !str.endsWith(NEW_LINE) && isDeleting && !this.mTagSpans.isEmpty()) {
            List<TagSpan> list = this.mTagSpans;
            TagSpan toRemoveSpan = list.get(list.size() - 1);
            Tag tag = toRemoveSpan.getTag();
            if (tag.getPosition() + tag.getSource().length() == str.length()) {
                removeTagSpan(editable, toRemoveSpan, false);
                str = editable.toString();
            }
        }
        if (str.endsWith(NEW_LINE) || (!this.mIsSpacesAllowedInTags && str.endsWith(this.mSeparator) && !isDeleting)) {
            buildTags(str);
        }
        this.mLastString = getText().toString();
        this.mIsAfterTextWatcherEnabled = true;
        if (isEnterClicked && (tagsEditListener = this.mListener) != null) {
            tagsEditListener.onEditingFinished();
        }
    }

    private void buildTags(String str) {
        if (str.length() != 0) {
            updateTags(str);
            SpannableStringBuilder sb = new SpannableStringBuilder();
            for (TagSpan tagSpan : this.mTagSpans) {
                addTagSpan(sb, tagSpan);
            }
            int size = this.mTags.size();
            for (int i = this.mTagSpans.size(); i < size; i++) {
                Tag tag = this.mTags.get(i);
                String source = tag.getSource();
                if (tag.isSpan()) {
                    Drawable bd = convertViewToDrawable(createTextView(source));
                    bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
                    TagSpan span = new TagSpan(bd, source);
                    addTagSpan(sb, span);
                    span.setTag(tag);
                    this.mTagSpans.add(span);
                } else {
                    sb.append(source);
                }
            }
            getText().clear();
            getText().append(sb);
            setSelection(sb.length());
            if (this.mListener != null && !str.equals(this.mLastString)) {
                this.mListener.onTagsChanged(convertTagSpanToList(this.mTagSpans));
            }
        }
    }

    private void updateTags(String newString) {
        String source = getNewTag(newString);
        if (!TextUtils.isEmpty(source) && !source.equals(NEW_LINE)) {
            boolean isSpan = source.endsWith(NEW_LINE) || (!this.mIsSpacesAllowedInTags && source.endsWith(this.mSeparator));
            if (isSpan) {
                source = source.substring(0, source.length() - 1).trim();
            }
            Tag tag = new Tag();
            tag.setSource(source);
            tag.setSpan(isSpan);
            int size = this.mTags.size();
            if (size <= 0) {
                tag.setIndex(0);
                tag.setPosition(0);
            } else {
                Tag lastTag = this.mTags.get(size - 1);
                tag.setIndex(size);
                tag.setPosition(lastTag.getPosition() + lastTag.getSource().length() + 1);
            }
            this.mTags.add(tag);
        }
    }

    private String getNewTag(String newString) {
        StringBuilder builder = new StringBuilder();
        for (Tag tag : this.mTags) {
            if (tag.isSpan()) {
                builder.append(tag.getSource());
                builder.append(this.mSeparator);
            }
        }
        return newString.replace(builder.toString(), "");
    }

    private void addTagSpan(SpannableStringBuilder sb, final TagSpan tagSpan) {
        String source = tagSpan.getSource();
        sb.append(source).append(this.mSeparator);
        int length = sb.length();
        int startSpan = length - (source.length() + 1);
        int endSpan = length - 1;
        sb.setSpan(tagSpan, startSpan, endSpan, 33);
        sb.setSpan(new ClickableSpan() {
            public void onClick(View widget) {
                Editable editable = ((EditText) widget).getText();
                boolean unused = TagsEditText.this.mIsAfterTextWatcherEnabled = false;
                TagsEditText.this.removeTagSpan(editable, tagSpan, true);
                boolean unused2 = TagsEditText.this.mIsAfterTextWatcherEnabled = true;
            }
        }, startSpan, endSpan, 33);
    }

    /* access modifiers changed from: private */
    public void removeTagSpan(Editable editable, TagSpan span, boolean includeSpace) {
        Tag tag = span.getTag();
        int tagPosition = tag.getPosition();
        int tagIndex = tag.getIndex();
        int tagLength = span.getSource().length() ;
        editable.replace(tagPosition, tagPosition + tagLength, "");
        int size = this.mTags.size();
        for (int i = tagIndex + 1; i < size; i++) {
            Tag newTag = this.mTags.get(i);
            newTag.setIndex(i - 1);
            newTag.setPosition(newTag.getPosition() - tagLength);
        }
        this.mTags.remove(tagIndex);
        this.mTagSpans.remove(tagIndex);
        TagsEditListener tagsEditListener = this.mListener;
        if (tagsEditListener != null) {
            tagsEditListener.onTagsChanged(convertTagSpanToList(this.mTagSpans));
        }
    }

    private static List<String> convertTagSpanToList(List<TagSpan> tagSpans) {
        List<String> tags = new ArrayList<>(tagSpans.size());
        for (TagSpan tagSpan : tagSpans) {
            tags.add(tagSpan.getSource());
        }
        return tags;
    }

    private static CharSequence[] convertTagSpanToArray(List<TagSpan> tagSpans) {
        int size = tagSpans.size();
        CharSequence[] values = new CharSequence[size];
        for (int i = 0; i < size; i++) {
            values[i] = tagSpans.get(i).getSource();
        }
        return values;
    }

    private Drawable convertViewToDrawable(View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, 0);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Canvas c = new Canvas(Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888));
        c.translate((float) (-view.getScrollX()), (float) (-view.getScrollY()));
        view.draw(c);
        view.setDrawingCacheEnabled(true);
        Bitmap viewBmp = view.getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);
        view.destroyDrawingCache();
        return new BitmapDrawable(getResources(), viewBmp);
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(getContext());
        if (getWidth() > 0) {
            textView.setMaxWidth(getWidth() - 50);
        }
        textView.setText(text);
        textView.setTextSize(0, this.mTagsTextSize);
        textView.setTextColor(this.mTagsTextColor);
        textView.setPadding(this.mTagsPaddingLeft, this.mTagsPaddingTop, this.mTagsPaddingRight, this.mTagsPaddingBottom);
        if (Build.VERSION.SDK_INT >= 16) {
            textView.setBackground(this.mTagsBackground);
        } else {
            textView.setBackgroundDrawable(this.mTagsBackground);
        }
        return textView;
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((keyCode != 3 && keyCode != 6 && (event.getAction() != 0 || event.getKeyCode() != 66)) || event.isShiftPressed()) {
            return false;
        }
        Log.e("Pinky", "Enter Pressed...");
        return true;
    }

    private static final class Tag implements Parcelable {
        public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
            public Tag createFromParcel(Parcel in) {
                return new Tag(in);
            }

            public Tag[] newArray(int size) {
                return new Tag[size];
            }
        };
        private int mIndex;
        private int mPosition;
        private String mSource;
        private boolean mSpan;

        private Tag() {
        }

        protected Tag(Parcel in) {
            this.mPosition = in.readInt();
            this.mIndex = in.readInt();
            this.mSource = in.readString();
            this.mSpan = in.readInt() != 1 ? false : true;
        }

        /* access modifiers changed from: private */
        public void setPosition(int pos) {
            this.mPosition = pos;
        }

        /* access modifiers changed from: private */
        public int getPosition() {
            return this.mPosition;
        }

        /* access modifiers changed from: private */
        public void setIndex(int index) {
            this.mIndex = index;
        }

        /* access modifiers changed from: private */
        public int getIndex() {
            return this.mIndex;
        }

        public void setSource(String source) {
            this.mSource = source;
        }

        public String getSource() {
            return this.mSource;
        }

        public void setSpan(boolean span) {
            this.mSpan = span;
        }

        public boolean isSpan() {
            return this.mSpan;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mPosition);
            dest.writeInt(this.mIndex);
            dest.writeString(this.mSource);
            dest.writeInt(this.mSpan ? 1 : 0);
        }
    }

    private static final class TagSpan extends ImageSpan {
        private Tag mTag;

        public TagSpan(Drawable d, String source) {
            super(d, source);
        }

        private TagSpan(Context context, Bitmap b) {
            super(context, b);
        }

        private TagSpan(Context context, Bitmap b, int verticalAlignment) {
            super(context, b, verticalAlignment);
        }

        private TagSpan(Drawable d) {
            super(d);
        }

        private TagSpan(Drawable d, int verticalAlignment) {
            super(d, verticalAlignment);
        }

        private TagSpan(Drawable d, String source, int verticalAlignment) {
            super(d, source, verticalAlignment);
        }

        private TagSpan(Context context, Uri uri) {
            super(context, uri);
        }

        private TagSpan(Context context, Uri uri, int verticalAlignment) {
            super(context, uri, verticalAlignment);
        }

        private TagSpan(Context context, int resourceId) {
            super(context, resourceId);
        }

        private TagSpan(Context context, int resourceId, int verticalAlignment) {
            super(context, resourceId, verticalAlignment);
        }

        /* access modifiers changed from: private */
        public void setTag(Tag tag) {
            this.mTag = tag;
        }

        public Tag getTag() {
            return this.mTag;
        }
    }

    public static class TagsEditListenerAdapter implements TagsEditListener {
        public void onTagsChanged(Collection<String> collection) {
        }

        public void onEditingFinished() {
        }
    }
}
