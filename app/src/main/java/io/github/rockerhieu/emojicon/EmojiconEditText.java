package io.github.rockerhieu.emojicon;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import com.camscanner.paperscanner.pdfcreator.R;

/* renamed from: io.github.rockerhieu.emojicon.EmojiconEditText */
public class EmojiconEditText extends AppCompatEditText {
    private int mEmojiconAlignment;
    private int mEmojiconSize;
    private int mEmojiconTextSize;
    private boolean mUseSystemDefault;

    public EmojiconEditText(Context context) {
        super(context);
        this.mUseSystemDefault = false;
        this.mEmojiconSize = (int) getTextSize();
        this.mEmojiconTextSize = (int) getTextSize();
    }

    public EmojiconEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mUseSystemDefault = false;
        init(attrs);
    }

    public EmojiconEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mUseSystemDefault = false;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Emojicon);
        this.mEmojiconSize = (int) a.getDimension(R.styleable.Emojicon_emojiconSize, getTextSize());
        this.mEmojiconAlignment = a.getInt(R.styleable.Emojicon_emojiconAlignment, 1);
        this.mUseSystemDefault = a.getBoolean(R.styleable.Emojicon_emojiconUseSystemDefault, false);
        a.recycle();
        this.mEmojiconTextSize = (int) getTextSize();
        setText(getText());
    }

    /* access modifiers changed from: protected */
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        updateText();
    }

    public void setEmojiconSize(int pixels) {
        this.mEmojiconSize = pixels;
        updateText();
    }

    private void updateText() {
        EmojiconHandler.addEmojis(getContext(), getText(), this.mEmojiconSize, this.mEmojiconAlignment, this.mEmojiconTextSize, this.mUseSystemDefault);
    }

    public void setUseSystemDefault(boolean useSystemDefault) {
        this.mUseSystemDefault = useSystemDefault;
    }
}
