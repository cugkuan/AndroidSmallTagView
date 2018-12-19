package com.cugkuan.smalltag;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * 轻量级的Tags,主要适用于ListView recycleView中。
 * <p>
 * 在设置Tags后，什么时候需要重新布局，什么时候需要只需要重新绘制，进行了简单的优化。
 */
public final class SmallTagView extends View {


    /**
     * 默认的TagColor
     */
    private static final int DEFAULT_COLOR = Color.parseColor("#F2F2F2");

    private int mTagBackground = DEFAULT_COLOR;
    /**
     * 垂直方向Tag的间隔
     */
    private int mVerticalDivider;
    /**
     * 竖直方向Tag的间隔
     */
    private int mHorizontalDivider;
    /**
     * tag的圆角
     */
    private int mTagRadius;
    /**
     * Tag的文字Size.
     */
    private int mTagTextSize = 15;

    private int mTagTextColor = Color.parseColor("#000000");

    private int mTextPaddingLeft;

    private int mTextPaddingRight;

    private int mTextPaddingTop;

    private int mTextPaddingBottom;

    /**
     * tag的最大行数。
     */
    private int mMaxLines = Integer.MAX_VALUE;
    /**
     * 显示的最大个数
     */
    private int mMaxTagNum = Integer.MAX_VALUE;
    private List<TagDrawable> mTagDrawables;

    private Paint mTextPaint;
    /**
     * tag 的背景颜色
     */
    private Paint mTagBackgroundPaint;


    private List<String> mTags;


    private Layout mLayout;


    public SmallTagView(Context context) {
        this(context, null);
    }

    public SmallTagView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SmallTagView);
            if (array.hasValue(R.styleable.SmallTagView_tagBackground)) {
                mTagBackground = array.getColor(R.styleable.SmallTagView_tagBackground, DEFAULT_COLOR);
            }
            if (array.hasValue(R.styleable.SmallTagView_tagVerticalDivider)) {
                mVerticalDivider = array.getDimensionPixelSize(R.styleable.SmallTagView_tagVerticalDivider, 0);
            }
            if (array.hasValue(R.styleable.SmallTagView_tagHorizontalDivider)) {
                mHorizontalDivider = array.getDimensionPixelSize(R.styleable.SmallTagView_tagHorizontalDivider, 0);
            }
            if (array.hasValue(R.styleable.SmallTagView_tagRadius)) {
                mTagRadius = array.getDimensionPixelOffset(R.styleable.SmallTagView_tagRadius, 0);
            }
            if (array.hasValue(R.styleable.SmallTagView_tagTextSize)) {
                mTagTextSize = array.getDimensionPixelSize(R.styleable.SmallTagView_tagTextSize, 0);
            }
            if (array.hasValue(R.styleable.SmallTagView_tagTextColor)) {
                mTagTextColor = array.getColor(R.styleable.SmallTagView_tagTextColor, Color.parseColor("#000000"));
            }
            if (array.hasValue(R.styleable.SmallTagView_textPaddingBottom)) {
                mTextPaddingBottom = array.getDimensionPixelSize(R.styleable.SmallTagView_textPaddingBottom, 0);
            }
            if (array.hasValue(R.styleable.SmallTagView_textPaddingLeft)) {
                mTextPaddingLeft = array.getDimensionPixelSize(R.styleable.SmallTagView_textPaddingLeft, 0);
            }
            if (array.hasValue(R.styleable.SmallTagView_textPaddingRight)) {
                mTextPaddingRight = array.getDimensionPixelSize(R.styleable.SmallTagView_textPaddingRight, 0);
            }
            if (array.hasValue(R.styleable.SmallTagView_textPaddingTop)) {
                mTextPaddingTop = array.getDimensionPixelSize(R.styleable.SmallTagView_textPaddingTop, 0);
            }
            if (array.hasValue(R.styleable.SmallTagView_maxLines)) {
                mMaxLines = array.getInt(R.styleable.SmallTagView_maxLines, Integer.MAX_VALUE);
            }
            if (array.hasValue(R.styleable.SmallTagView_maxTagNum)) {
                mMaxTagNum = array.getInt(R.styleable.SmallTagView_maxTagNum, Integer.MAX_VALUE);
            }
            array.recycle();
        }

        mTagBackgroundPaint = new TextPaint();
        mTagBackgroundPaint.setColor(mTagBackground);
        mTagBackgroundPaint.setAntiAlias(true);
        mTagBackgroundPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(mTagTextSize);
        mTextPaint.setColor(mTagTextColor);
        mTextPaint.setAntiAlias(true);

    }

    /**
     * 设置Tags
     *
     * @param tags
     */
    public void setTags(List<String> tags) {

        if (mLayout != null) {
            mLayout.setNeedCalculate(true);
        }
        if (!isTagsSame(tags)) {
            mTags = tags;
            if (mTagDrawables == null) {
                mTagDrawables = new ArrayList<>(mTags.size());
            } else {
                mTagDrawables.clear();
            }
            int size = Math.min(mMaxTagNum, tags.size());
            for (int i = 0; i < size; i++) {
                String tag = tags.get(i);
                TagDrawable tagDrawable = new TagDrawable(tag, mTextPaint);
                tagDrawable.setPadding(mTextPaddingLeft,
                        mTextPaddingTop, mTextPaddingRight,
                        mTextPaddingBottom)
                        .setBackground(mTagBackgroundPaint)
                        .setRadius(mTagRadius)
                        .commit();
                mTagDrawables.add(tagDrawable);
            }
            if (mLayout != null) {
                int oldWidth = mLayout.getWidth();
                int oldHeight = mLayout.getHeight();
                mLayout.calculate();
                mLayout.setNeedCalculate(false);
                if (oldWidth == mLayout.getWidth() && oldHeight == mLayout.getHeight()) {
                    invalidate();
                    return;
                }
            }
            requestLayout();
            invalidate();
        } else {
            invalidate();
        }
    }

    private boolean isTagsSame(List<String> tags) {

        if (tags == null || mTags == null || tags.size() != mTags.size()) {
            return false;
        } else {
            for (int i = 0; i < tags.size(); i++) {
                String tag = tags.get(i);
                String old = mTags.get(i);
                if (!TextUtils.equals(tag, old)) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mLayout == null) {
            mLayout = new Layout(widthMeasureSpec, heightMeasureSpec);
        } else {
            mLayout.setHeightMeasureSpec(heightMeasureSpec);
            mLayout.setWidthMeasureSpec(widthMeasureSpec);

        }
        if (mLayout.isNeedCalculate()) {
            mLayout.calculate();
        }
        setMeasuredDimension(mLayout.getWidth(), mLayout.getHeight());

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mTagDrawables == null || mTagDrawables.isEmpty()) {
            return;
        }
        int cLeft = getPaddingLeft();
        int cRight = getMeasuredWidth() - getPaddingLeft();
        int cTop = getPaddingTop();
        int cBottom = getMeasuredHeight() - getPaddingTop();

        canvas.clipRect(cLeft, cTop, cRight, cBottom, Region.Op.REPLACE);
        canvas.translate(cLeft, cTop);

        canvas.save();
        int tagHeight = getTextHeight();
        int currentLine = -1;

        for (TagDrawable drawable : mTagDrawables) {
            int line = drawable.line;
            //最大行数的判断
            if (line > mMaxLines - 1) {
                break;
            }
            // 计算绘制
            if (line != currentLine) {
                currentLine = line;
                canvas.restore();
                canvas.save();
                //y方向上的移动
                int dy = line * (tagHeight + mVerticalDivider);
                canvas.translate(0, dy);
            }
            drawable.draw(canvas);
            int dx = drawable.getWidth() + mHorizontalDivider;
            canvas.translate(dx, 0);
        }
        canvas.restore();
    }

    private int getTextHeight() {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        int fontHeight = Math.round(fontMetrics.bottom - fontMetrics.top);
        return (fontHeight + mTextPaddingTop + mTextPaddingBottom);

    }

    public int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 一个内部类，封装了Tags的分布，高度计算等操作
     */
    private class Layout {

        private int widthMeasureSpec;
        private int heightMeasureSpec;
        /**
         * View 宽度
         */
        private int mWidth;
        /**
         * View 的高度
         */
        private int mHeight;

        /**
         * 是否需要进行计算。
         * 为避免多次计算
         */
        private boolean needCalculate = true;


        public Layout(int widthSpec, int heightSpec) {
            this.widthMeasureSpec = widthSpec;
            this.heightMeasureSpec = heightSpec;
        }

        public void setWidthMeasureSpec(int widthMeasureSpec) {
            this.widthMeasureSpec = widthMeasureSpec;
        }

        public void setHeightMeasureSpec(int heightMeasureSpec) {
            this.heightMeasureSpec = heightMeasureSpec;
        }

        public boolean isNeedCalculate() {
            return needCalculate;
        }

        public void setNeedCalculate(boolean needCalculate) {
            this.needCalculate = needCalculate;
        }

        /**
         * 开始计算布局了。
         */
        public void calculate() {

            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int width;
            int height;
            if (mTagDrawables == null || mTagDrawables.isEmpty()) {
                if (widthMode == MeasureSpec.AT_MOST) {
                    width = 0;
                } else {
                    width = widthSize;
                }
                if (heightMode == MeasureSpec.AT_MOST) {
                    height = 0;
                } else {
                    height = heightSize;
                }
                this.mWidth = width;
                this.mHeight = height;
                return;
            }
            /**
             * 列
             */
            int resultWidth = 0;
            int line = 0;
            int row = 0;
            int useWidth = getPaddingLeft() + getPaddingRight();

            for (TagDrawable tagDrawable : mTagDrawables) {
                useWidth = useWidth + tagDrawable.width + mHorizontalDivider;
                //是否需要换行的判断
                if (useWidth > widthSize && row > 0) {
                    row = 0;
                    line++;
                    useWidth = tagDrawable.width + mHorizontalDivider + getPaddingLeft() + getPaddingRight();
                    resultWidth = Math.max(resultWidth, useWidth);
                } else {
                    resultWidth = Math.max(resultWidth, useWidth);
                }
                tagDrawable.line = line;
                tagDrawable.row = row;
                row++;
            }
            int tagHeight = getTextHeight();
            int resultLine = Math.min(line + 1, mMaxLines);
            int resultHeight = tagHeight * resultLine + mVerticalDivider * (resultLine - 1) + getPaddingTop() + getPaddingBottom();
            if (widthMode == MeasureSpec.EXACTLY) {
                mWidth = widthSize;
            } else {
                mWidth = Math.min(widthSize, resultWidth) + mHorizontalDivider;
            }
            if (heightMode == MeasureSpec.EXACTLY) {
                mHeight = heightSize;
            } else {
                mHeight = resultHeight;
            }
        }

        public int getWidth() {
            return mWidth;
        }


        public int getHeight() {
            return mHeight;
        }

    }

    /**
     * 单个Tag Drawable封装
     */
    private static class TagDrawable extends Drawable {

        private final String text;
        private final Paint textContentPain;
        private Paint backgroundPaint;
        private RectF fBounds;
        private int leftPadding;
        private int topPadding;
        private int rightPadding;
        private int bottomPadding;
        private int radius;
        private int width;
        private int height;
        /**
         * 所处的行数
         */
        public int line = 0;
        /**
         * 所处的列数
         */
        public int row = 0;

        public TagDrawable(String text, Paint textPaint) {
            this.text = text;
            this.textContentPain = textPaint;
        }

        public TagDrawable setBackground(Paint paint) {
            this.backgroundPaint = paint;
            return this;
        }

        public TagDrawable setRadius(int radius) {
            this.radius = radius;
            return this;
        }


        public TagDrawable setPadding(int leftPadding, int topPadding, int rightPadding, int bottomPaddding) {
            this.leftPadding = leftPadding;
            this.topPadding = topPadding;
            this.rightPadding = rightPadding;
            this.bottomPadding = bottomPaddding;
            return this;
        }

        public TagDrawable commit() {
            Paint.FontMetrics fontMetrics = textContentPain.getFontMetrics();
            int fontHeight = Math.round(fontMetrics.bottom - fontMetrics.top);
            width = Math.round(textContentPain.measureText(text)) + leftPadding + rightPadding;
            height = fontHeight + topPadding + bottomPadding;
            setBounds(0, 0, width, height);
            fBounds = new RectF(getBounds());
            return this;
        }


        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }


        @Override
        public void draw(Canvas canvas) {
            canvas.drawRoundRect(fBounds, radius, radius, backgroundPaint);
            canvas.drawText(text, leftPadding,
                    textContentPain.getTextSize() + topPadding, textContentPain);
        }

        @Override
        public void setAlpha(int alpha) {
            textContentPain.setAlpha(alpha);
            backgroundPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            textContentPain.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }


}
