package io.github.rockerhieu.emojicon;

import android.content.Context;
import android.text.Spannable;
import android.util.SparseIntArray;
import java.util.HashMap;
import java.util.Map;
import org.opencv.videoio.Videoio;

import com.camscanner.paperscanner.pdfcreator.R;

/* renamed from: io.github.rockerhieu.emojicon.EmojiconHandler */
public final class EmojiconHandler {
    private static final SparseIntArray sEmojiModifiersMap = new SparseIntArray(5);
    private static final SparseIntArray sEmojisMap = new SparseIntArray(1029);
    private static Map<String, Integer> sEmojisModifiedMap = new HashMap();
    private static final SparseIntArray sSoftbanksMap = new SparseIntArray(Videoio.CAP_PROP_XI_APPLY_CMS);

    private EmojiconHandler() {
    }

    static {
        sEmojiModifiersMap.put(127995, 1);
        sEmojiModifiersMap.put(127996, 1);
        sEmojiModifiersMap.put(127997, 1);
        sEmojiModifiersMap.put(127998, 1);
        sEmojiModifiersMap.put(127999, 1);
        sEmojisMap.put(58672, R.drawable.emoji_e530);
        sEmojisMap.put(58673, R.drawable.emoji_e531);
        sEmojisMap.put(58674, R.drawable.emoji_e532);
        sEmojisMap.put(58675, R.drawable.emoji_e533);
        sEmojisMap.put(58677, R.drawable.emoji_e535);
        sEmojisMap.put(58678, R.drawable.emoji_e536);
        sEmojisMap.put(58679, R.drawable.emoji_e537);
    }

    private static boolean isSoftBankEmoji(char c) {
        return (c >> 12) == 14;
    }

    private static int getEmojiResource(Context context, int codePoint) {
        return sEmojisMap.get(codePoint);
    }

    private static int getSoftbankEmojiResource(char c) {
        return sSoftbanksMap.get(c);
    }

    public static void addEmojis(Context context, Spannable text, int emojiSize, int emojiAlignment, int textSize) {
        addEmojis(context, text, emojiSize, emojiAlignment, textSize, 0, -1, false);
    }

    public static void addEmojis(Context context, Spannable text, int emojiSize, int emojiAlignment, int textSize, int index, int length) {
        addEmojis(context, text, emojiSize, emojiAlignment, textSize, index, length, false);
    }

    public static void addEmojis(Context context, Spannable text, int emojiSize, int emojiAlignment, int textSize, boolean useSystemDefault) {
        addEmojis(context, text, emojiSize, emojiAlignment, textSize, 0, -1, useSystemDefault);
    }

    public static void addEmojis(Context context, Spannable text, int emojiSize, int emojiAlignment, int textSize, int index, int length, boolean useSystemDefault) {
        int textLengthToProcess;
        int textLengthToProcessMax;
        int textLength;
        int resourceId;
        int nextFollowUnicode;
        Spannable spannable = text;
        int i = length;
        if (useSystemDefault) {
            return;
        }
        if (spannable != null) {
            int textLength2 = text.length();
            int textLengthToProcessMax2 = textLength2 - index;
            int textLengthToProcess2 = (i < 0 || i >= textLengthToProcessMax2) ? textLength2 : i + index;
            EmojiconSpan[] oldSpans = (EmojiconSpan[]) spannable.getSpans(0, textLength2, EmojiconSpan.class);
            for (EmojiconSpan removeSpan : oldSpans) {
                spannable.removeSpan(removeSpan);
            }
            int i2 = index;
            while (i2 < textLengthToProcess2) {
                int skip = 0;
                int icon = 0;
                char c = spannable.charAt(i2);
                if (isSoftBankEmoji(c)) {
                    icon = getSoftbankEmojiResource(c);
                    skip = icon == 0 ? 0 : 1;
                }
                if (icon == 0) {
                    int unicode = Character.codePointAt(spannable, i2);
                    skip = Character.charCount(unicode);
                    if (unicode > 255) {
                        icon = getEmojiResource(context, unicode);
                    }
                    if (i2 + skip < textLengthToProcess2) {
                        int followUnicode = Character.codePointAt(spannable, i2 + skip);
                        if (followUnicode == 65039) {
                            int followSkip = Character.charCount(followUnicode);
                            if (i2 + skip + followSkip < textLengthToProcess2 && (nextFollowUnicode = Character.codePointAt(spannable, i2 + skip + followSkip)) == 8419) {
                                int nextFollowSkip = Character.charCount(nextFollowUnicode);
                                int tempIcon = getKeyCapEmoji(unicode);
                                if (tempIcon == 0) {
                                    followSkip = 0;
                                    nextFollowSkip = 0;
                                } else {
                                    icon = tempIcon;
                                }
                                skip += followSkip + nextFollowSkip;
                            }
                            textLength = textLength2;
                            textLengthToProcessMax = textLengthToProcessMax2;
                            textLengthToProcess = textLengthToProcess2;
                        } else if (followUnicode == 8419) {
                            int followSkip2 = Character.charCount(followUnicode);
                            int tempIcon2 = getKeyCapEmoji(unicode);
                            if (tempIcon2 == 0) {
                                followSkip2 = 0;
                            } else {
                                icon = tempIcon2;
                            }
                            skip += followSkip2;
                            textLength = textLength2;
                            textLengthToProcessMax = textLengthToProcessMax2;
                            textLengthToProcess = textLengthToProcess2;
                        } else if (sEmojiModifiersMap.get(followUnicode, 0) > 0) {
                            int followSkip3 = Character.charCount(followUnicode);
                            String hexUnicode = Integer.toHexString(unicode);
                            String hexFollowUnicode = Integer.toHexString(followUnicode);
                            StringBuilder sb = new StringBuilder();
                            textLength = textLength2;
                            sb.append("emoji_");
                            sb.append(hexUnicode);
                            sb.append("_");
                            sb.append(hexFollowUnicode);
                            String resourceName = sb.toString();
                            if (sEmojisModifiedMap.containsKey(resourceName)) {
                                resourceId = sEmojisModifiedMap.get(resourceName).intValue();
                                textLengthToProcessMax = textLengthToProcessMax2;
                                textLengthToProcess = textLengthToProcess2;
                            } else {
                                textLengthToProcessMax = textLengthToProcessMax2;
                                textLengthToProcess = textLengthToProcess2;
                                resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getApplicationContext().getPackageName());
                                if (resourceId != 0) {
                                    sEmojisModifiedMap.put(resourceName, Integer.valueOf(resourceId));
                                }
                            }
                            if (resourceId == 0) {
                                followSkip3 = 0;
                            } else {
                                icon = resourceId;
                            }
                            skip += followSkip3;
                        } else {
                            textLength = textLength2;
                            textLengthToProcessMax = textLengthToProcessMax2;
                            textLengthToProcess = textLengthToProcess2;
                        }
                    } else {
                        textLength = textLength2;
                        textLengthToProcessMax = textLengthToProcessMax2;
                        textLengthToProcess = textLengthToProcess2;
                    }
                } else {
                    textLength = textLength2;
                    textLengthToProcessMax = textLengthToProcessMax2;
                    textLengthToProcess = textLengthToProcess2;
                }
                if (icon > 0) {
                    spannable.setSpan(new EmojiconSpan(context, icon, emojiSize, emojiAlignment, textSize), i2, i2 + skip, 33);
                }
                i2 += skip;
                textLength2 = textLength;
                textLengthToProcessMax2 = textLengthToProcessMax;
                textLengthToProcess2 = textLengthToProcess;
            }
        }
    }

    private static int getKeyCapEmoji(int unicode) {
        return 0;
    }
}
