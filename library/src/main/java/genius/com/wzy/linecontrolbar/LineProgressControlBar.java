package genius.com.wzy.linecontrolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

@SuppressWarnings("unused")
public class LineProgressControlBar extends View {
    private static final int DEFAULT_FIRST_COLOR = Color.parseColor("#3e3e3e");
    private static final int DEFAULT_SECOND_COLOR = Color.parseColor("#fd592a");
    private static final float DEFAULT_TOTAL_WIDTH = 180f;
    private static final float DEFAULT_DIVIDE_WIDTH = 2f;
    private static final float DEFAULT_PROGRESS_HEIGHT = 10f;

    /** 背景颜色. */
    private int mFirstColor;

    /** 进度颜色. */
    private int mSecondColor;

    /** 进度条总宽度(单位:px). */
    private float mTotalWidth;

    /** 进度条间隔宽度(单位:px). */
    private float mDivideWidth;

    /** 进度条高度(单位:px). */
    private float mStrokeWidth;

    /** 进度总个数. */
    private int mDotCount;

    /** 进度条的最大值. */
    private int mMaxProgress;

    /** 进度条的最小值. */
    private int mMinProgress;

    /** 当前进度. */
    private int mCurrentCount;

    /** 当前进度值. */
    private int mCurrentProgress;

    /** 背景画笔. */
    private Paint mFirstPaint;

    /** 进度条画笔. */
    private Paint mSecondPaint;

    /** 进度中心点的X和Y坐标. */
    @SuppressWarnings("FieldCanBeLocal")
    private int mCenterX, mCenterY;

    /** 防止多次测量 */
    private boolean mIsOnce;

    /** 每个进度条的宽度. */
    private int itemWidth;

    /** 进度起始X值 */
    private float mProgressStartX;

    /** 进度结束X值 */
    private float mProgressStopX;

    public LineProgressControlBar(Context context) {
        this(context, null);
    }

    public LineProgressControlBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineProgressControlBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray ta = context.
                obtainStyledAttributes(attrs, R.styleable.LineProgressControlBar);
        mFirstColor = ta.
                getColor(R.styleable.LineProgressControlBar_first_color, DEFAULT_FIRST_COLOR);
        mSecondColor = ta.
                getColor(R.styleable.LineProgressControlBar_second_color, DEFAULT_SECOND_COLOR);
        mTotalWidth = ta.getDimension(R.styleable.LineProgressControlBar_total_width,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, DEFAULT_TOTAL_WIDTH,
                        context.getResources().getDisplayMetrics()));
        mDivideWidth = ta.getDimension(R.styleable.LineProgressControlBar_divide_width,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, DEFAULT_DIVIDE_WIDTH,
                        context.getResources().getDisplayMetrics()));
        mStrokeWidth = ta.getDimension(R.styleable.LineProgressControlBar_progress_height,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, DEFAULT_PROGRESS_HEIGHT,
                        context.getResources().getDisplayMetrics()));
        mDotCount = ta.getInt(R.styleable.LineProgressControlBar_dot_count, 0);
        ta.recycle();

        initPaint();
    }

    private void initPaint() {
        mFirstPaint = createPaint(mFirstColor, mStrokeWidth);
        mSecondPaint = createPaint(mSecondColor, mStrokeWidth);
    }

    private Paint createPaint(int color, float width) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStrokeWidth(width);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!mIsOnce && !isInEditMode()) {
            setup();
            mIsOnce = true;
        }
    }

    private void setup() {
        mCenterX = getMeasuredWidth() / 2;
        mCenterY = getMeasuredHeight() / 2;
        itemWidth = (int) ((mTotalWidth - (mDotCount - 1) * mDivideWidth) / mDotCount);
        Log.e("TAG", "item width=" + itemWidth);
        mProgressStartX = mCenterX - (mTotalWidth / 2);
        mProgressStopX = mProgressStartX + mTotalWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawAllProgressBar(canvas);
        drawCurrentProgressBar(canvas);
    }

    private void drawCurrentProgressBar(Canvas canvas) {
        float startX = mProgressStartX;
        for (int i = 0; i < mCurrentCount; i ++) {
            float stopX = startX + itemWidth;
            Log.e("TAG", "====startX=" + startX + ", stopX=" + stopX + "====");
            canvas.drawLine(startX, mCenterY, stopX, mCenterY, mSecondPaint);
            startX = stopX + mDivideWidth;
        }
    }

    private void drawAllProgressBar(Canvas canvas) {
        Log.e("TAG", "all progress bar===" + ", startX=" + mProgressStartX +
                ", stopX=" + mProgressStopX);
        canvas.drawLine(mProgressStartX, mCenterY, mProgressStopX, mCenterY, mFirstPaint);
    }

    /**
     * 增加进度
     */
    public void plusControl() {
        if (mCurrentCount < mDotCount) {
            mCurrentCount ++;
            mCurrentProgress = calculateProgress(mCurrentCount);
            postInvalidate();
            if (mListener != null) {
                mListener.progressChange(mCurrentProgress);
            }
        }
    }

    /**
     * 减少进度
     */
    public void minusControl() {
        if (mCurrentCount > 0) {
            mCurrentCount --;
            mCurrentProgress = calculateProgress(mCurrentCount);
            postInvalidate();
            if (mListener != null) {
                mListener.progressChange(mCurrentProgress);
            }
        }
    }

    /**
     * 设置当前进度栏.
     */
    public void setCurrentCount(int current) {
        if (current > mDotCount) {
            mCurrentCount = mDotCount;
        }

        if (current < 0) {
            mCurrentCount = 0;
        }

        mCurrentProgress = calculateProgress(mCurrentCount);
        if (mListener != null) {
            mListener.adjustProgress(mCurrentProgress);
        }

        postInvalidate();
    }

    /**
     * 设置进度条能表示的最大值和最小值.
     * @param maxProgress 进度条最大值
     * @param minProgress 进度条最小值
     */
    public void setMaxAndMinProgress(int maxProgress, int minProgress) {
        mMaxProgress = maxProgress;
        mMinProgress = minProgress;
    }

    /**
     * 设置当前进度条的进度值.
     */
    public void setCurrentProgress(int currentProgress) {
        mCurrentProgress = currentProgress;
        if (mCurrentProgress > mMaxProgress) {
            mCurrentProgress = mMaxProgress;
        }

        if (mCurrentProgress < mMinProgress) {
            mCurrentProgress = mMinProgress;
        }

        mCurrentCount = Math.round((mCurrentProgress - mMinProgress) * mDotCount * 1.0f
                / (mMaxProgress - mMinProgress));
        mCurrentProgress = calculateProgress(mCurrentCount);

        // 进行进度调整
        if (mCurrentProgress != currentProgress && mListener != null) {
            mListener.adjustProgress(mCurrentProgress);
        }

        postInvalidate();
    }

    private int calculateProgress(int currentCount) {
        return currentCount * (mMaxProgress - mMinProgress) / mDotCount + mMinProgress;
    }

    private OnProgressChangeListener mListener;

    public void setOnProgressChangeListener(OnProgressChangeListener listener) {
        mListener = listener;
    }

    public interface OnProgressChangeListener {
        void progressChange(int progress);
        void adjustProgress(int progress);
    }
}
