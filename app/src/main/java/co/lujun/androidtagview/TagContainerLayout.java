package co.lujun.androidtagview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.customview.widget.ViewDragHelper;

import com.camscanner.paperscanner.pdfcreator.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/* renamed from: co.lujun.androidtagview.TagContainerLayout */
public class TagContainerLayout extends ViewGroup {
    private static final float DEFAULT_INTERVAL = 5.0f;
    private static final int TAG_MIN_LENGTH = 3;
    private boolean isTagViewClickable;
    private int mBackgroundColor;
    private int mBorderColor;
    private float mBorderRadius;
    private float mBorderWidth;
    private float mCheckAreaPadding;
    private float mCheckAreaWidth;
    private int mCheckColor;
    private float mCheckLineWidth;
    private int mChildHeight;
    private List<View> mChildViews;
    private List<int[]> mColorArrayList;
    private float mCrossAreaPadding;
    private float mCrossAreaWidth;
    private int mCrossColor;
    private float mCrossLineWidth;
    private int mDefaultImageDrawableID;
    /* access modifiers changed from: private */
    public boolean mDragEnable;
    private boolean mEnableCheck;
    private boolean mEnableCross;
    private int mGravity;
    private int mHorizontalInterval;
    private int mMaxLines;
    private TagView.OnTagClickListener mOnTagClickListener;
    private Paint mPaint;
    private RectF mRectF;
    private int mRippleAlpha;
    private int mRippleColor;
    private int mRippleDuration;
    private float mSensitivity;
    private int mTagBackgroundColor;
    private int mTagBackgroundResource;
    private float mTagBdDistance;
    private int mTagBorderColor;
    private float mTagBorderRadius;
    private float mTagBorderWidth;
    private int mTagHorizontalPadding;
    private int mTagMaxLength;
    private boolean mTagSupportLettersRTL;
    private int mTagTextColor;
    private int mTagTextDirection;
    private float mTagTextSize;
    private Typeface mTagTypeface;
    private int mTagVerticalPadding;
    /* access modifiers changed from: private */
    public int mTagViewState;
    private List<TagItem> mTags;
    private int mTheme;
    private int mVerticalInterval;
    /* access modifiers changed from: private */
    public ViewDragHelper mViewDragHelper;
    private int[] mViewPos;

    public TagContainerLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public TagContainerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagContainerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mBorderWidth = 0.0f;
        this.mBorderRadius = 10.0f;
        this.mSensitivity = 1.0f;
        this.mBorderColor = Color.parseColor("#22FF0000");
        this.mBackgroundColor = Color.parseColor("#11FF0000");
        this.mGravity = 3;
        this.mMaxLines = 0;
        this.mTagMaxLength = 23;
        this.mTagBorderWidth = 0.5f;
        this.mTagBorderRadius = 15.0f;
        this.mTagTextSize = 14.0f;
        this.mTagTextDirection = 3;
        this.mTagHorizontalPadding = 10;
        this.mTagVerticalPadding = 8;
        this.mTagBorderColor = Color.parseColor("#88F44336");
        this.mTagBackgroundColor = Color.parseColor("#33F44336");
        this.mTagTextColor = Color.parseColor("#FF666666");
        this.mTagTypeface = Typeface.DEFAULT;
        this.mDefaultImageDrawableID = -1;
        this.mTagViewState = 0;
        this.mTagBdDistance = 2.75f;
        this.mTagSupportLettersRTL = false;
        this.mTheme = 1;
        this.mRippleDuration = 1000;
        this.mRippleAlpha = 128;
        this.mEnableCross = false;
        this.mCrossAreaWidth = 0.0f;
        this.mCrossAreaPadding = 10.0f;
        this.mCrossColor = -16777216;
        this.mCrossLineWidth = 1.0f;
        this.mEnableCheck = false;
        this.mCheckAreaWidth = 0.0f;
        this.mCheckAreaPadding = 10.0f;
        this.mCheckColor = -16777216;
        this.mCheckLineWidth = 1.0f;
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.AndroidTagView, defStyleAttr, 0);
        this.mVerticalInterval = (int) attributes.getDimension(R.styleable.AndroidTagView_vertical_interval, Utils.dp2px(context, 5.0f));
        this.mHorizontalInterval = (int) attributes.getDimension(R.styleable.AndroidTagView_horizontal_interval, Utils.dp2px(context, 5.0f));
        this.mBorderWidth = attributes.getDimension(R.styleable.AndroidTagView_container_border_width, Utils.dp2px(context, this.mBorderWidth));
        this.mBorderRadius = attributes.getDimension(R.styleable.AndroidTagView_container_border_radius, Utils.dp2px(context, this.mBorderRadius));
        this.mTagBdDistance = attributes.getDimension(R.styleable.AndroidTagView_tag_bd_distance, Utils.dp2px(context, this.mTagBdDistance));
        this.mBorderColor = attributes.getColor(R.styleable.AndroidTagView_container_border_color, this.mBorderColor);
        this.mBackgroundColor = attributes.getColor(R.styleable.AndroidTagView_container_background_color, this.mBackgroundColor);
        this.mDragEnable = attributes.getBoolean(R.styleable.AndroidTagView_container_enable_drag, false);
        this.mSensitivity = attributes.getFloat(R.styleable.AndroidTagView_container_drag_sensitivity, this.mSensitivity);
        this.mGravity = attributes.getInt(R.styleable.AndroidTagView_container_gravity, this.mGravity);
        this.mMaxLines = attributes.getInt(R.styleable.AndroidTagView_container_max_lines, this.mMaxLines);
        this.mTagMaxLength = attributes.getInt(R.styleable.AndroidTagView_tag_max_length, this.mTagMaxLength);
        this.mTheme = attributes.getInt(R.styleable.AndroidTagView_tag_theme, this.mTheme);
        this.mTagBorderWidth = attributes.getDimension(R.styleable.AndroidTagView_tag_border_width, Utils.dp2px(context, this.mTagBorderWidth));
        this.mTagBorderRadius = attributes.getDimension(R.styleable.AndroidTagView_tag_corner_radius, Utils.dp2px(context, this.mTagBorderRadius));
        this.mTagHorizontalPadding = (int) attributes.getDimension(R.styleable.AndroidTagView_tag_horizontal_padding, Utils.dp2px(context, (float) this.mTagHorizontalPadding));
        this.mTagVerticalPadding = (int) attributes.getDimension(R.styleable.AndroidTagView_tag_vertical_padding, Utils.dp2px(context, (float) this.mTagVerticalPadding));
        this.mTagTextSize = attributes.getDimension(R.styleable.AndroidTagView_tag_text_size, Utils.sp2px(context, this.mTagTextSize));
        this.mTagBorderColor = attributes.getColor(R.styleable.AndroidTagView_tag_border_color, this.mTagBorderColor);
        this.mTagBackgroundColor = attributes.getColor(R.styleable.AndroidTagView_tag_background_color, this.mTagBackgroundColor);
        this.mTagTextColor = attributes.getColor(R.styleable.AndroidTagView_tag_text_color, this.mTagTextColor);
        this.mTagTextDirection = attributes.getInt(R.styleable.AndroidTagView_tag_text_direction, this.mTagTextDirection);
        this.isTagViewClickable = attributes.getBoolean(R.styleable.AndroidTagView_tag_clickable, false);
        this.mRippleColor = attributes.getColor(R.styleable.AndroidTagView_tag_ripple_color, Color.parseColor("#EEEEEE"));
        this.mRippleAlpha = attributes.getInteger(R.styleable.AndroidTagView_tag_ripple_alpha, this.mRippleAlpha);
        this.mRippleDuration = attributes.getInteger(R.styleable.AndroidTagView_tag_ripple_duration, this.mRippleDuration);
        this.mEnableCross = attributes.getBoolean(R.styleable.AndroidTagView_tag_enable_cross, this.mEnableCross);
        this.mCrossAreaWidth = attributes.getDimension(R.styleable.AndroidTagView_tag_cross_width, Utils.dp2px(context, this.mCrossAreaWidth));
        this.mCrossAreaPadding = attributes.getDimension(R.styleable.AndroidTagView_tag_cross_area_padding, Utils.dp2px(context, this.mCrossAreaPadding));
        this.mCrossColor = attributes.getColor(R.styleable.AndroidTagView_tag_cross_color, this.mCrossColor);
        this.mCrossLineWidth = attributes.getDimension(R.styleable.AndroidTagView_tag_cross_line_width, Utils.dp2px(context, this.mCrossLineWidth));
        this.mEnableCheck = attributes.getBoolean(R.styleable.AndroidTagView_tag_enable_check, this.mEnableCheck);
        this.mCheckAreaWidth = attributes.getDimension(R.styleable.AndroidTagView_tag_check_width, Utils.dp2px(context, this.mCheckAreaWidth));
        this.mCheckAreaPadding = attributes.getDimension(R.styleable.AndroidTagView_tag_check_area_padding, Utils.dp2px(context, this.mCheckAreaPadding));
        this.mCheckColor = attributes.getColor(R.styleable.AndroidTagView_tag_check_color, this.mCheckColor);
        this.mCheckLineWidth = attributes.getDimension(R.styleable.AndroidTagView_tag_check_line_width, Utils.dp2px(context, this.mCheckLineWidth));
        this.mTagSupportLettersRTL = attributes.getBoolean(R.styleable.AndroidTagView_tag_support_letters_rlt, this.mTagSupportLettersRTL);
        this.mTagBackgroundResource = attributes.getResourceId(R.styleable.AndroidTagView_tag_background, this.mTagBackgroundResource);
        attributes.recycle();
        this.mPaint = new Paint(1);
        this.mRectF = new RectF();
        this.mChildViews = new ArrayList();
        this.mViewDragHelper = ViewDragHelper.create(this, this.mSensitivity, new DragHelperCallBack());
        setWillNotDraw(false);
        setTagMaxLength(this.mTagMaxLength);
        setTagHorizontalPadding(this.mTagHorizontalPadding);
        setTagVerticalPadding(this.mTagVerticalPadding);
        if (isInEditMode()) {
            addTag(new TagItem("sample tag", false));
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        int lines = childCount == 0 ? 0 : getChildLines(childCount);
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (childCount == 0) {
            setMeasuredDimension(0, 0);
        } else if (heightSpecMode == Integer.MIN_VALUE || heightSpecMode == 0) {
            int i = this.mVerticalInterval;
            setMeasuredDimension(widthSpecSize, (((this.mChildHeight + i) * lines) - i) + getPaddingTop() + getPaddingBottom());
        } else {
            setMeasuredDimension(widthSpecSize, heightSpecSize);
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mRectF.set(0.0f, 0.0f, (float) w, (float) h);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int childCount2 = childCount;
        if (childCount > 0) {
            int availableW = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
            int curRight = getMeasuredWidth() - getPaddingRight();
            int curTop = getPaddingTop();
            int curLeft = getPaddingLeft();
            int sPos = 0;
            this.mViewPos = new int[(childCount2 * 2)];
            for (int i = 0; i < childCount2; i++) {
                View childView = getChildAt(i);
                if (childView.getVisibility() != 8) {
                    int width = childView.getMeasuredWidth();
                    int i2 = this.mGravity;
                    if (i2 == 5) {
                        if (curRight - width < getPaddingLeft()) {
                            curRight = getMeasuredWidth() - getPaddingRight();
                            curTop += this.mChildHeight + this.mVerticalInterval;
                        }
                        int[] iArr = this.mViewPos;
                        iArr[i * 2] = curRight - width;
                        iArr[(i * 2) + 1] = curTop;
                        curRight -= this.mHorizontalInterval + width;
                    } else if (i2 == 17) {
                        if ((curLeft + width) - getPaddingLeft() > availableW) {
                            int leftW = ((getMeasuredWidth() - this.mViewPos[(i - 1) * 2]) - getChildAt(i - 1).getMeasuredWidth()) - getPaddingRight();
                            for (int j = sPos; j < i; j++) {
                                int[] iArr2 = this.mViewPos;
                                iArr2[j * 2] = iArr2[j * 2] + (leftW / 2);
                            }
                            sPos = i;
                            curLeft = getPaddingLeft();
                            curTop += this.mChildHeight + this.mVerticalInterval;
                        }
                        int[] iArr3 = this.mViewPos;
                        iArr3[i * 2] = curLeft;
                        iArr3[(i * 2) + 1] = curTop;
                        curLeft += this.mHorizontalInterval + width;
                        if (i == childCount2 - 1) {
                            int leftW2 = ((getMeasuredWidth() - this.mViewPos[i * 2]) - childView.getMeasuredWidth()) - getPaddingRight();
                            for (int j2 = sPos; j2 < childCount2; j2++) {
                                int[] iArr4 = this.mViewPos;
                                iArr4[j2 * 2] = iArr4[j2 * 2] + (leftW2 / 2);
                            }
                        }
                    } else {
                        if ((curLeft + width) - getPaddingLeft() > availableW) {
                            curLeft = getPaddingLeft();
                            curTop += this.mChildHeight + this.mVerticalInterval;
                        }
                        int[] iArr5 = this.mViewPos;
                        iArr5[i * 2] = curLeft;
                        iArr5[(i * 2) + 1] = curTop;
                        curLeft += this.mHorizontalInterval + width;
                    }
                }
            }
            for (int i3 = 0; i3 < this.mViewPos.length / 2; i3++) {
                View childView2 = getChildAt(i3);
                int[] iArr6 = this.mViewPos;
                childView2.layout(iArr6[i3 * 2], iArr6[(i3 * 2) + 1], iArr6[i3 * 2] + childView2.getMeasuredWidth(), this.mViewPos[(i3 * 2) + 1] + this.mChildHeight);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(this.mBackgroundColor);
        RectF rectF = this.mRectF;
        float f = this.mBorderRadius;
        canvas.drawRoundRect(rectF, f, f, this.mPaint);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(this.mBorderWidth);
        this.mPaint.setColor(this.mBorderColor);
        RectF rectF2 = this.mRectF;
        float f2 = this.mBorderRadius;
        canvas.drawRoundRect(rectF2, f2, f2, this.mPaint);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent event) {
        this.mViewDragHelper.processTouchEvent(event);
        return true;
    }

    public void computeScroll() {
        super.computeScroll();
        if (this.mViewDragHelper.continueSettling(true)) {
            requestLayout();
        }
    }

    private int getChildLines(int childCount) {
        int availableW = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
        int lines = 1;
        int i = 0;
        int curLineW = 0;
        while (i < childCount) {
            View childView = getChildAt(i);
            int dis = childView.getMeasuredWidth() + this.mHorizontalInterval;
            int height = childView.getMeasuredHeight();
            this.mChildHeight = i == 0 ? height : Math.min(this.mChildHeight, height);
            curLineW += dis;
            if (curLineW - this.mHorizontalInterval > availableW) {
                lines++;
                curLineW = dis;
            }
            i++;
        }
        int i2 = this.mMaxLines;
        return i2 <= 0 ? lines : i2;
    }

    private int[] onUpdateColorFactory() {
        int i = this.mTheme;
        if (i == 0) {
            return ColorFactory.onRandomBuild();
        }
        if (i == 2) {
            return ColorFactory.onPureBuild(ColorFactory.PURE_COLOR.TEAL);
        }
        if (i == 1) {
            return ColorFactory.onPureBuild(ColorFactory.PURE_COLOR.CYAN);
        }
        return new int[]{this.mTagBackgroundColor, this.mTagBorderColor, this.mTagTextColor};
    }

    private void onSetTag() {
        if (this.mTags != null) {
            removeAllTags();
            if (this.mTags.size() != 0) {
                for (int i = 0; i < this.mTags.size(); i++) {
                    onAddTag(this.mTags.get(i), this.mChildViews.size());
                }
                postInvalidate();
                return;
            }
            return;
        }
        throw new RuntimeException("NullPointer exception!");
    }

    private void onAddTag(TagItem tagItem, int position) {
        TagView tagView;
        if (position < 0 || position > this.mChildViews.size()) {
            throw new RuntimeException("Illegal position!");
        }
        if (this.mDefaultImageDrawableID != -1) {
            tagView = new TagView(getContext(), tagItem, this.mDefaultImageDrawableID);
        } else {
            tagView = new TagView(getContext(), tagItem);
        }
        initTagView(tagView, position);
        this.mChildViews.add(position, tagView);
        if (position < this.mChildViews.size()) {
            for (int i = position; i < this.mChildViews.size(); i++) {
                this.mChildViews.get(i).setTag(Integer.valueOf(i));
            }
        } else {
            tagView.setTag(Integer.valueOf(position));
        }
        addView(tagView, position);
    }

    private void onUpdateTag(TagItem tagItem, int position) {
        if (position < 0 || position > this.mChildViews.size()) {
            throw new RuntimeException("Illegal position!");
        }
        ((TagView) this.mChildViews.get(position)).setTagItem(tagItem);
    }

    private void initTagView(TagView tagView, int position) {
        int[] colors;
        List<int[]> list = this.mColorArrayList;
        if (list == null || list.size() <= 0) {
            colors = onUpdateColorFactory();
        } else if (this.mColorArrayList.size() != this.mTags.size() || this.mColorArrayList.get(position).length < 3) {
            throw new RuntimeException("Illegal color list!");
        } else {
            colors = this.mColorArrayList.get(position);
        }
        tagView.setTagBackgroundColor(colors[0]);
        tagView.setTagBorderColor(colors[1]);
        tagView.setTagTextColor(colors[2]);
        tagView.setTagMaxLength(this.mTagMaxLength);
        tagView.setTextDirection(this.mTagTextDirection);
        tagView.setTypeface(this.mTagTypeface);
        tagView.setBorderWidth(this.mTagBorderWidth);
        tagView.setBorderRadius(this.mTagBorderRadius);
        tagView.setTextSize(this.mTagTextSize);
        tagView.setHorizontalPadding(this.mTagHorizontalPadding);
        tagView.setVerticalPadding(this.mTagVerticalPadding);
        tagView.setIsViewClickable(this.isTagViewClickable);
        tagView.setBdDistance(this.mTagBdDistance);
        tagView.setOnTagClickListener(this.mOnTagClickListener);
        tagView.setRippleAlpha(this.mRippleAlpha);
        tagView.setRippleColor(this.mRippleColor);
        tagView.setRippleDuration(this.mRippleDuration);
        tagView.setEnableCross(this.mEnableCross);
        tagView.setCrossAreaWidth(this.mCrossAreaWidth);
        tagView.setCrossAreaPadding(this.mCrossAreaPadding);
        tagView.setCrossColor(this.mCrossColor);
        tagView.setCrossLineWidth(this.mCrossLineWidth);
        tagView.setEnableCheck(this.mEnableCheck);
        tagView.setCheckAreaWidth(this.mCheckAreaWidth);
        tagView.setCheckAreaPadding(this.mCheckAreaPadding);
        tagView.setCheckColor(this.mCheckColor);
        tagView.setCheckLineWidth(this.mCheckLineWidth);
        tagView.setTagSupportLettersRTL(this.mTagSupportLettersRTL);
        tagView.setBackgroundResource(this.mTagBackgroundResource);
    }

    private void invalidateTags() {
        Iterator<View> it = this.mChildViews.iterator();
        while (it.hasNext()) {
            ((TagView) it.next()).setOnTagClickListener(this.mOnTagClickListener);
        }
    }

    private void onRemoveTag(int position) {
        if (position < 0 || position >= this.mChildViews.size()) {
            throw new RuntimeException("Illegal position!");
        }
        this.mChildViews.remove(position);
        this.mTags.remove(position);
        removeViewAt(position);
        for (int i = position; i < this.mChildViews.size(); i++) {
            this.mChildViews.get(i).setTag(Integer.valueOf(i));
        }
    }

    /* access modifiers changed from: private */
    public int[] onGetNewPosition(View view) {
        int left = view.getLeft();
        int top = view.getTop();
        int bestMatchLeft = this.mViewPos[((Integer) view.getTag()).intValue() * 2];
        int bestMatchTop = this.mViewPos[(((Integer) view.getTag()).intValue() * 2) + 1];
        int tmpTopDis = Math.abs(top - bestMatchTop);
        int i = 0;
        while (true) {
            int[] iArr = this.mViewPos;
            if (i >= iArr.length / 2) {
                break;
            }
            if (Math.abs(top - iArr[(i * 2) + 1]) < tmpTopDis) {
                int[] iArr2 = this.mViewPos;
                bestMatchTop = iArr2[(i * 2) + 1];
                tmpTopDis = Math.abs(top - iArr2[(i * 2) + 1]);
            }
            i++;
        }
        int rowChildCount = 0;
        int tmpLeftDis = 0;
        int i2 = 0;
        while (true) {
            int[] iArr3 = this.mViewPos;
            if (i2 < iArr3.length / 2) {
                if (iArr3[(i2 * 2) + 1] == bestMatchTop) {
                    if (rowChildCount == 0) {
                        bestMatchLeft = iArr3[i2 * 2];
                        tmpLeftDis = Math.abs(left - bestMatchLeft);
                    } else if (Math.abs(left - iArr3[i2 * 2]) < tmpLeftDis) {
                        bestMatchLeft = this.mViewPos[i2 * 2];
                        tmpLeftDis = Math.abs(left - bestMatchLeft);
                    }
                    rowChildCount++;
                }
                i2++;
            } else {
                return new int[]{bestMatchLeft, bestMatchTop};
            }
        }
    }

    /* access modifiers changed from: private */
    public int onGetCoordinateReferPos(int left, int top) {
        int pos = 0;
        int i = 0;
        while (true) {
            int[] iArr = this.mViewPos;
            if (i >= iArr.length / 2) {
                return pos;
            }
            if (left == iArr[i * 2] && top == iArr[(i * 2) + 1]) {
                pos = i;
            }
            i++;
        }
    }

    /* access modifiers changed from: private */
    public void onChangeView(View view, int newPos, int originPos) {
        this.mChildViews.remove(originPos);
        this.mChildViews.add(newPos, view);
        for (View child : this.mChildViews) {
            child.setTag(Integer.valueOf(this.mChildViews.indexOf(child)));
        }
        removeViewAt(originPos);
        addView(view, newPos);
    }

    private int ceilTagBorderWidth() {
        return (int) Math.ceil((double) this.mTagBorderWidth);
    }

    /* renamed from: co.lujun.androidtagview.TagContainerLayout$DragHelperCallBack */
    private class DragHelperCallBack extends ViewDragHelper.Callback {
        private DragHelperCallBack() {
        }

        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            int unused = TagContainerLayout.this.mTagViewState = state;
        }

        public boolean tryCaptureView(View child, int pointerId) {
            TagContainerLayout.this.requestDisallowInterceptTouchEvent(true);
            return TagContainerLayout.this.mDragEnable;
        }

        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int leftX = TagContainerLayout.this.getPaddingLeft();
            return Math.min(Math.max(left, leftX), (TagContainerLayout.this.getWidth() - child.getWidth()) - TagContainerLayout.this.getPaddingRight());
        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            int topY = TagContainerLayout.this.getPaddingTop();
            return Math.min(Math.max(top, topY), (TagContainerLayout.this.getHeight() - child.getHeight()) - TagContainerLayout.this.getPaddingBottom());
        }

        public int getViewHorizontalDragRange(View child) {
            return TagContainerLayout.this.getMeasuredWidth() - child.getMeasuredWidth();
        }

        public int getViewVerticalDragRange(View child) {
            return TagContainerLayout.this.getMeasuredHeight() - child.getMeasuredHeight();
        }

        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            TagContainerLayout.this.requestDisallowInterceptTouchEvent(false);
            int[] pos = TagContainerLayout.this.onGetNewPosition(releasedChild);
            TagContainerLayout.this.onChangeView(releasedChild, TagContainerLayout.this.onGetCoordinateReferPos(pos[0], pos[1]), ((Integer) releasedChild.getTag()).intValue());
            TagContainerLayout.this.mViewDragHelper.settleCapturedViewAt(pos[0], pos[1]);
            TagContainerLayout.this.invalidate();
        }
    }

    public int getTagViewState() {
        return this.mTagViewState;
    }

    public float getTagBdDistance() {
        return this.mTagBdDistance;
    }

    public void setTagBdDistance(float tagBdDistance) {
        this.mTagBdDistance = Utils.dp2px(getContext(), tagBdDistance);
    }

    public void setTags(List<TagItem> tags) {
        this.mTags = tags;
        onSetTag();
    }

    public void setTags(List<TagItem> tags, List<int[]> colorArrayList) {
        this.mTags = tags;
        this.mColorArrayList = colorArrayList;
        onSetTag();
    }

    public void setTags(TagItem... tags) {
        this.mTags = Arrays.asList(tags);
        onSetTag();
    }

    public void addTag(TagItem tagItem) {
        addTag(tagItem, this.mChildViews.size());
    }

    public void addTag(TagItem tagItem, int position) {
        if (this.mTags == null) {
            this.mTags = new ArrayList();
        }
        this.mTags.add(tagItem);
        onAddTag(tagItem, position);
        postInvalidate();
    }

    public void updateTag(TagItem tagItem, int position) {
        List<TagItem> list = this.mTags;
        if (list != null) {
            list.set(position, tagItem);
            onUpdateTag(tagItem, position);
            postInvalidate();
        }
    }

    public TagItem getTagItem(int position) {
        return this.mTags.get(position);
    }

    public List<TagItem> getTagList() {
        return this.mTags;
    }

    public void removeTag(int position) {
        onRemoveTag(position);
        postInvalidate();
    }

    public void removeAllTags() {
        this.mChildViews.clear();
        removeAllViews();
        postInvalidate();
    }

    public void setOnTagClickListener(TagView.OnTagClickListener listener) {
        this.mOnTagClickListener = listener;
        invalidateTags();
    }

    public String getTagText(int position) {
        return ((TagView) this.mChildViews.get(position)).getText();
    }

    public List<TagItem> getTags() {
        return this.mTags;
    }

    public void setDragEnable(boolean enable) {
        this.mDragEnable = enable;
    }

    public boolean getDragEnable() {
        return this.mDragEnable;
    }

    public void setVerticalInterval(float interval) {
        this.mVerticalInterval = (int) Utils.dp2px(getContext(), interval);
        postInvalidate();
    }

    public int getVerticalInterval() {
        return this.mVerticalInterval;
    }

    public void setHorizontalInterval(float interval) {
        this.mHorizontalInterval = (int) Utils.dp2px(getContext(), interval);
        postInvalidate();
    }

    public int getHorizontalInterval() {
        return this.mHorizontalInterval;
    }

    public float getBorderWidth() {
        return this.mBorderWidth;
    }

    public void setBorderWidth(float width) {
        this.mBorderWidth = width;
    }

    public float getBorderRadius() {
        return this.mBorderRadius;
    }

    public void setBorderRadius(float radius) {
        this.mBorderRadius = radius;
    }

    public int getBorderColor() {
        return this.mBorderColor;
    }

    public void setBorderColor(int color) {
        this.mBorderColor = color;
    }

    public int getBackgroundColor() {
        return this.mBackgroundColor;
    }

    public void setBackgroundColor(int color) {
        this.mBackgroundColor = color;
    }

    public int getGravity() {
        return this.mGravity;
    }

    public void setGravity(int gravity) {
        this.mGravity = gravity;
    }

    public float getSensitivity() {
        return this.mSensitivity;
    }

    public void setSensitivity(float sensitivity) {
        this.mSensitivity = sensitivity;
    }

    public int getDefaultImageDrawableID() {
        return this.mDefaultImageDrawableID;
    }

    public void setDefaultImageDrawableID(int imageID) {
        this.mDefaultImageDrawableID = imageID;
    }

    public void setMaxLines(int maxLines) {
        this.mMaxLines = maxLines;
        postInvalidate();
    }

    public int getMaxLines() {
        return this.mMaxLines;
    }

    public void setTagMaxLength(int maxLength) {
        int i = 3;
        if (maxLength >= 3) {
            i = maxLength;
        }
        this.mTagMaxLength = i;
    }

    public int getTagMaxLength() {
        return this.mTagMaxLength;
    }

    public void setTheme(int theme) {
        this.mTheme = theme;
    }

    public int getTheme() {
        return this.mTheme;
    }

    public boolean getIsTagViewClickable() {
        return this.isTagViewClickable;
    }

    public void setIsTagViewClickable(boolean clickable) {
        this.isTagViewClickable = clickable;
    }

    public float getTagBorderWidth() {
        return this.mTagBorderWidth;
    }

    public void setTagBorderWidth(float width) {
        this.mTagBorderWidth = width;
    }

    public float getTagBorderRadius() {
        return this.mTagBorderRadius;
    }

    public void setTagBorderRadius(float radius) {
        this.mTagBorderRadius = radius;
    }

    public float getTagTextSize() {
        return this.mTagTextSize;
    }

    public void setTagTextSize(float size) {
        this.mTagTextSize = size;
    }

    public int getTagHorizontalPadding() {
        return this.mTagHorizontalPadding;
    }

    public void setTagHorizontalPadding(int padding) {
        int ceilWidth = ceilTagBorderWidth();
        this.mTagHorizontalPadding = padding < ceilWidth ? ceilWidth : padding;
    }

    public int getTagVerticalPadding() {
        return this.mTagVerticalPadding;
    }

    public void setTagVerticalPadding(int padding) {
        int ceilWidth = ceilTagBorderWidth();
        this.mTagVerticalPadding = padding < ceilWidth ? ceilWidth : padding;
    }

    public int getTagBorderColor() {
        return this.mTagBorderColor;
    }

    public void setTagBorderColor(int color) {
        this.mTagBorderColor = color;
    }

    public int getTagBackgroundColor() {
        return this.mTagBackgroundColor;
    }

    public void setTagBackgroundColor(int color) {
        this.mTagBackgroundColor = color;
    }

    public int getTagTextColor() {
        return this.mTagTextColor;
    }

    public void setTagTextDirection(int textDirection) {
        this.mTagTextDirection = textDirection;
    }

    public Typeface getTagTypeface() {
        return this.mTagTypeface;
    }

    public void setTagTypeface(Typeface typeface) {
        this.mTagTypeface = typeface;
    }

    public int getTagTextDirection() {
        return this.mTagTextDirection;
    }

    public void setTagTextColor(int color) {
        this.mTagTextColor = color;
    }

    public int getRippleAlpha() {
        return this.mRippleAlpha;
    }

    public void setRippleAlpha(int mRippleAlpha2) {
        this.mRippleAlpha = mRippleAlpha2;
    }

    public int getRippleColor() {
        return this.mRippleColor;
    }

    public void setRippleColor(int mRippleColor2) {
        this.mRippleColor = mRippleColor2;
    }

    public int getRippleDuration() {
        return this.mRippleDuration;
    }

    public void setRippleDuration(int mRippleDuration2) {
        this.mRippleDuration = mRippleDuration2;
    }

    public int getCrossColor() {
        return this.mCrossColor;
    }

    public void setCrossColor(int mCrossColor2) {
        this.mCrossColor = mCrossColor2;
    }

    public float getCrossAreaPadding() {
        return this.mCrossAreaPadding;
    }

    public void setCrossAreaPadding(float mCrossAreaPadding2) {
        this.mCrossAreaPadding = mCrossAreaPadding2;
    }

    public boolean isEnableCross() {
        return this.mEnableCross;
    }

    public void setEnableCross(boolean mEnableCross2) {
        this.mEnableCross = mEnableCross2;
    }

    public float getCrossAreaWidth() {
        return this.mCrossAreaWidth;
    }

    public void setCrossAreaWidth(float mCrossAreaWidth2) {
        this.mCrossAreaWidth = mCrossAreaWidth2;
    }

    public float getCrossLineWidth() {
        return this.mCrossLineWidth;
    }

    public void setCrossLineWidth(float mCrossLineWidth2) {
        this.mCrossLineWidth = mCrossLineWidth2;
    }

    public boolean isTagSupportLettersRTL() {
        return this.mTagSupportLettersRTL;
    }

    public void setTagSupportLettersRTL(boolean mTagSupportLettersRTL2) {
        this.mTagSupportLettersRTL = mTagSupportLettersRTL2;
    }

    public TagView getTagView(int position) {
        if (position >= 0 && position < this.mChildViews.size()) {
            return (TagView) this.mChildViews.get(position);
        }
        throw new RuntimeException("Illegal position!");
    }

    public int getTagBackgroundResource() {
        return this.mTagBackgroundResource;
    }

    public void setTagBackgroundResource(@DrawableRes int tagBackgroundResource) {
        this.mTagBackgroundResource = tagBackgroundResource;
    }
}
