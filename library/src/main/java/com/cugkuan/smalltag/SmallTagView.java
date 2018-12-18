package com.cugkuan.smalltag;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public final class SmallTagView extends View {


    /**
     * 默认的TagColor
     */
    private static final int DEFALUT_COLOR = Color.parseColor("#F2F2F2");

    private int mTagBackground = DEFALUT_COLOR;

    /**
     * tag之间的间隔。
     */
    private int mDivider;

    /**
     * tag的圆角
     */
    private int mTagRadius;

    /**
     * Tag的文字Size.
     */
    private int mTagTextSize = 15;

    private int mTagTextColor = Color.parseColor("#000000");

    private int mTextPadingLeft;

    private int mTextPaddingRight;

    private int mTextPaddingTop;

    private int mTextPaddingBottom;

    /**
     * tag的最大行数。
     */
    private int mMaxLines = Integer.MAX_VALUE;

    private List<String> mTags;

    private List<TagDrawable> mTagDrawables;

    private Paint mTextPaint;


    public SmallTagView(Context context) {
        this(context, null);
    }

    public SmallTagView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SmallTagView);
            if (array.hasValue(R.styleable.SmallTagView_tagBackground)) {
                mTagBackground = array.getColor(R.styleable.SmallTagView_tagBackground, DEFALUT_COLOR);
            }
            if (array.hasValue(R.styleable.SmallTagView_tagDivider)) {
                mDivider = array.getDimensionPixelSize(R.styleable.SmallTagView_tagDivider, 0);
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
                mTextPadingLeft = array.getDimensionPixelSize(R.styleable.SmallTagView_textPaddingLeft, 0);
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
            array.recycle();
        }
        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(mTagTextSize);
        mTextPaint.setColor(mTagTextColor);
        mTextPaint.setAntiAlias(true);

    }

    /**
     * 设置Tags;
     *
     * @param tags
     */
    public void setTags(List<String> tags) {
        mTags = tags;
        if (mTags != null && !mTags.isEmpty()) {
            mTagDrawables = new ArrayList<>(mTags.size());
            for (String tag : mTags) {
                TagDrawable tagDrawable = new TagDrawable(tag, mTextPaint);
                tagDrawable.setPadding(mTextPadingLeft, mTextPaddingTop, mTextPaddingRight, mTextPaddingBottom)
                        .setBackgrounp(mTagBackground)
                        .setRadius(mTagRadius)
                        .commit();
                mTagDrawables.add(tagDrawable);
            }
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mTagDrawables == null || mTagDrawables.isEmpty()) {
            setMeasuredDimension(width, height);
            return;
        }
        /**
         * 列
         */
        int line = 0;
        int row = 0;
        int useWidth = 0;
        for (TagDrawable tagDrawable : mTagDrawables) {
            useWidth = useWidth + tagDrawable.width + mDivider;
            //需要换行了。
            if (useWidth > width && row > 0) {
                row = 0;
                line++;
                useWidth = tagDrawable.width + mDivider;
            }
            tagDrawable.line = line;
            tagDrawable.row = row;
            row++;
        }
        //计算高度了
        int tagHeight = getTextHeight();
        int resultHeight = tagHeight * line + mDivider *(line - 1);
        setMeasuredDimension(width,Math.max(resultHeight,width));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mTagDrawables == null || mTagDrawables.isEmpty()){
            return;
        }
        canvas.save();
        int tagHeight = getTextHeight();
        int  currentLine = -1;
        for (TagDrawable drawable : mTagDrawables){
            int line = drawable.line;
            // 计算绘制
            if (line != currentLine) {
                currentLine = line;
                canvas.restore();
                canvas.save();
                //y方向上的移动
                int dy = line * (tagHeight + mDivider);
                canvas.translate(0,dy);
            }
            drawable.draw(canvas);
            int dx = drawable.getWidth() + mDivider;
            canvas.translate(dx,0);
        }
    }

    private int getTextHeight() {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        int fontHeight = (int) (fontMetrics.bottom - fontMetrics.top);
        return fontHeight + mTextPaddingTop + mTextPaddingBottom;


    }

    public int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    private static class TagDrawable extends Drawable {
        private static final int MAGIC_PADDING_LEFT = 0;
        private static final int MAGIC_PADDING_BOTTOM = 3;
        private final String text;


        private final Paint textContentPain;
        private Paint backgroundPaint;
        private RectF fBounds;

        private int leftPadding;

        private int topPadding;

        private int rightPadding;

        private int bottomPadding;

        private int radius;


        private int backgrounp;


        private int width;

        private int height;


        public int line = 0;


        public int row = 0;


        public TagDrawable(String text, Paint textPaint) {
            this.text = text;
            this.textContentPain = textPaint;
        }

        public TagDrawable setBackgrounp(int color) {
            this.backgrounp = color;
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

            this.backgroundPaint = new Paint();
            this.backgroundPaint.setColor(backgrounp);
            this.backgroundPaint.setStyle(Paint.Style.FILL);
            this.backgroundPaint.setAntiAlias(true);
            Paint.FontMetrics fontMetrics = textContentPain.getFontMetrics();
            int fontHeight = (int) (fontMetrics.bottom - fontMetrics.top);
            width = (int) textContentPain.measureText(text) + leftPadding + rightPadding;
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
