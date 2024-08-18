package io.github.rockerhieu.emojicon;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.camscanner.paperscanner.pdfcreator.R;

/* renamed from: io.github.rockerhieu.emojicon.EmojiconTextView */
public class EmojiconTextView extends AppCompatTextView {
    private int mEmojiconAlignment;
    private int mEmojiconSize;
    private int mEmojiconTextSize;
    private int mTextLength = -1;
    private int mTextStart = 0;
    private boolean mUseSystemDefault = false;

    public EmojiconTextView(Context context) {
        super(context);
        init((AttributeSet) null);
    }

    public EmojiconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmojiconTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.mEmojiconTextSize = (int) getTextSize();
        if (attrs == null) {
            this.mEmojiconSize = (int) getTextSize();
        } else {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Emojicon);
            this.mEmojiconSize = (int) a.getDimension(R.styleable.Emojicon_emojiconSize, getTextSize());
            this.mEmojiconAlignment = a.getInt(R.styleable.Emojicon_emojiconAlignment, 1);
            this.mTextStart = a.getInteger(R.styleable.Emojicon_emojiconTextStart, 0);
            this.mTextLength = a.getInteger(R.styleable.Emojicon_emojiconTextLength, -1);
            this.mUseSystemDefault = a.getBoolean(R.styleable.Emojicon_emojiconUseSystemDefault, false);
            a.recycle();
        }
        setText(getText());
    }

    public void setText(CharSequence text, TextView.BufferType type) {
        if (!TextUtils.isEmpty(text)) {
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            EmojiconHandler.addEmojis(getContext(), builder, this.mEmojiconSize, this.mEmojiconAlignment, this.mEmojiconTextSize, this.mTextStart, this.mTextLength, this.mUseSystemDefault);
            text = builder;
        }
        super.setText(text, type);
    }

    public void setEmojiconSize(int pixels) {
        this.mEmojiconSize = pixels;
        super.setText(getText());
    }

    public void setUseSystemDefault(boolean useSystemDefault) {
        this.mUseSystemDefault = useSystemDefault;
    }
}
