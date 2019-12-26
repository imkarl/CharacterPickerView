package com.weigan.loopview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cn.jeesoft.widget.pickerview.R;

/**
 * Created by Weidongjian on 2015/8/18.
 */
public class LoopView extends View {

    private float scaleX = 1.05F;

    private static final int DEFAULT_TEXT_SIZE = (int) (Resources.getSystem().getDisplayMetrics().density * 15);

    private static final float DEFAULT_LINE_SPACE = 1f;

    private static final int DEFAULT_VISIBIE_ITEMS = 9;

    public enum ACTION {
        // 点击，滑翔(滑到尽头)，拖拽事件
        CLICK, FLING, DRAG
    }

    private Context context;

    Handler handler;
    private GestureDetector flingGestureDetector;
    OnItemSelectedListener onItemSelectedListener;

    // Timer mTimer;
    ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mFuture;

    private Paint paintOuterText;
    private Paint paintCenterText;
    private Paint paintIndicator;

    List<String> items;

    int textSize;
    int itemTextHeight;
    private int maxTextSize = -1;

    // 文本的高度
    int textHeight;

    int outerTextColor;

    int centerTextColor;
    int dividerColor;

    // 条目间距倍数
    float lineSpacingMultiplier;
    boolean isLoop;

    // 第一条线Y坐标值
    int firstLineY;
    int secondLineY;

    int totalScrollY;
    int initPosition;
    int preCurrentIndex;
    int selectedIndex = -1;
    int change;

    // 显示几个条目
    int itemsVisibleCount;

    String[] drawingStrings;

    int measuredHeight;
    int measuredWidth;

    // 半圆周长
    int halfCircumference;
    // 半径
    int radius;

    private int mOffset = 0;
    private float previousY;
    long startTime = 0;

    private Rect tempRect = new Rect();

    private int paddingLeft, paddingRight;

    private Typeface typeface = Typeface.MONOSPACE;

    /**
     * set text line space, must more than 1
     */
    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        if (lineSpacingMultiplier > 1.0f) {
            this.lineSpacingMultiplier = lineSpacingMultiplier;
        }
    }

    /**
     * set outer text color
     */
    public void setCenterTextColor(int centerTextColor) {
        this.centerTextColor = centerTextColor;
        if (paintCenterText != null) {
            paintCenterText.setColor(centerTextColor);
        }
    }

    /**
     * set center text color
     */
    public void setOuterTextColor(int outerTextColor) {
        this.outerTextColor = outerTextColor;
        if (paintOuterText != null) {
            paintOuterText.setColor(outerTextColor);
        }
    }

    /**
     * set divider color
     */
    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        if (paintIndicator != null) {
            paintIndicator.setColor(dividerColor);
        }
    }

    /**
     * set text typeface
     */
    public void setTypeface(Typeface typeface) {
        if (typeface == null) {
            this.typeface = Typeface.MONOSPACE;
        } else {
            this.typeface = typeface;
        }
    }

    public LoopView(Context context) {
        super(context);
        initLoopView(context, null);
    }

    public LoopView(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        initLoopView(context, attributeset);
    }

    public LoopView(Context context, AttributeSet attributeset, int defStyleAttr) {
        super(context, attributeset, defStyleAttr);
        initLoopView(context, attributeset);
    }

    private void initLoopView(Context context, AttributeSet attributeset) {
        this.context = context;
        handler = new MessageHandler(this);
        flingGestureDetector = new GestureDetector(context, new LoopViewGestureListener(this));
        flingGestureDetector.setIsLongpressEnabled(false);

        TypedArray typedArray = context.obtainStyledAttributes(attributeset, R.styleable.LoopView);
        if (typedArray != null) {
            textSize = typedArray.getInteger(R.styleable.LoopView_awv_textsize, DEFAULT_TEXT_SIZE);
            textSize = (int) (Resources.getSystem().getDisplayMetrics().density * textSize);
            lineSpacingMultiplier = typedArray.getFloat(R.styleable.LoopView_awv_lineSpace, DEFAULT_LINE_SPACE);
            centerTextColor = typedArray.getInteger(R.styleable.LoopView_awv_centerTextColor, 0xff313131);
            outerTextColor = typedArray.getInteger(R.styleable.LoopView_awv_outerTextColor, 0xffafafaf);
            dividerColor = typedArray.getInteger(R.styleable.LoopView_awv_dividerTextColor, 0xffc5c5c5);
            itemsVisibleCount =
                    typedArray.getInteger(R.styleable.LoopView_awv_itemsVisibleCount, DEFAULT_VISIBIE_ITEMS);
            if (itemsVisibleCount % 2 == 0) {
                itemsVisibleCount = DEFAULT_VISIBIE_ITEMS;
            }
            isLoop = typedArray.getBoolean(R.styleable.LoopView_awv_isLoop, true);
            typedArray.recycle();
        }

        drawingStrings = new String[itemsVisibleCount];

        totalScrollY = 0;
        initPosition = -1;
    }

    /**
     * visible item count, must be odd number
     */
    public void setItemsVisibleCount(int visibleNumber) {
        if (visibleNumber % 2 == 0) {
            return;
        }
        if (visibleNumber != itemsVisibleCount) {
            itemsVisibleCount = visibleNumber;
            drawingStrings = new String[itemsVisibleCount];
        }
    }

    private void initPaintsIfPossible() {
        if (paintOuterText == null) {
            paintOuterText = new Paint();
            paintOuterText.setColor(outerTextColor);
            paintOuterText.setAntiAlias(true);
            paintOuterText.setTypeface(typeface);
            paintOuterText.setTextSize(textSize);
        }

        if (paintCenterText == null) {
            paintCenterText = new Paint();
            paintCenterText.setColor(centerTextColor);
            paintCenterText.setAntiAlias(true);
            paintCenterText.setTextScaleX(scaleX);
            paintCenterText.setTypeface(typeface);
            paintCenterText.setTextSize(textSize);
        }

        if (paintIndicator == null) {
            paintIndicator = new Paint();
            paintIndicator.setColor(dividerColor);
            paintIndicator.setAntiAlias(true);
        }
    }

    private void remeasure() {
        if (items == null || items.isEmpty()) {
            return;
        }

        measuredWidth = getMeasuredWidth();

        measuredHeight = getMeasuredHeight();

        if (measuredWidth == 0 || measuredHeight == 0) {
            return;
        }

        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();

        measuredWidth = measuredWidth - paddingRight;

        paintCenterText.getTextBounds("\u661F\u671F", 0, 2, tempRect); // 星期
        textHeight = tempRect.height();
        halfCircumference = (int) (measuredHeight * Math.PI / 2);

        itemTextHeight = (int) (halfCircumference / (lineSpacingMultiplier * (itemsVisibleCount - 1)));

        radius = measuredHeight / 2;
        firstLineY = (int) ((measuredHeight - lineSpacingMultiplier * itemTextHeight) / 2.0F);
        secondLineY = (int) ((measuredHeight + lineSpacingMultiplier * itemTextHeight) / 2.0F);
        if (initPosition == -1) {
            if (isLoop) {
                initPosition = (items.size() + 1) / 2;
            } else {
                initPosition = 0;
            }
        }

        preCurrentIndex = initPosition;
    }

    void smoothScroll(ACTION action) {
        cancelFuture();
        if (action == ACTION.FLING || action == ACTION.DRAG) {
            float itemHeight = lineSpacingMultiplier * itemTextHeight;
            mOffset = (int) ((totalScrollY % itemHeight + itemHeight) % itemHeight);
            if ((float) mOffset > itemHeight / 2.0F) {
                mOffset = (int) (itemHeight - (float) mOffset);
            } else {
                mOffset = -mOffset;
            }
        }
        mFuture = mExecutor.scheduleWithFixedDelay(new SmoothScrollTimerTask(this, mOffset), 0, 10, TimeUnit.MILLISECONDS);
    }

    protected final void scrollBy(float velocityY) {
        cancelFuture();
        // change this number, can change fling speed
        int velocityFling = 10;
        mFuture = mExecutor.scheduleWithFixedDelay(new InertiaTimerTask(this, velocityY), 0, velocityFling,
            TimeUnit.MILLISECONDS);
    }

    public void cancelFuture() {
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
            mFuture = null;
        }
    }

    /**
     * set not loop
     */
    public void setNotLoop() {
        isLoop = false;
    }

    public final void setLoop(boolean isLoop) {
        this.isLoop = isLoop;
    }

    /**
     * set text size in dp
     * @param size
     */
    public final void setTextSize(float size) {
        if (size > 0.0F) {
            textSize = (int) (context.getResources().getDisplayMetrics().density * size);
            if (paintOuterText != null) {
                paintOuterText.setTextSize(textSize);
            }
            if (paintCenterText != null) {
                paintCenterText.setTextSize(textSize);
            }
        }
    }

    /**
     * set max text size in dp
     */
    public final void setMaxTextSize(float dpVaule) {
        this.maxTextSize = dp2px(dpVaule);
    }

    public final void setInitPosition(int initPosition) {
        if (initPosition < 0) {
            this.initPosition = 0;
        } else {
            if (items != null && items.size() > initPosition) {
                this.initPosition = initPosition;
            }
        }
    }

    public final void setListener(OnItemSelectedListener OnItemSelectedListener) {
        onItemSelectedListener = OnItemSelectedListener;
    }

    public final void setItems(List<String> items) {
        this.items = items;
        remeasure();
        invalidate();
    }

    public final int getSelectedItem() {
        return selectedIndex;
    }
    //
    // protected final void scrollBy(float velocityY) {
    // Timer timer = new Timer();
    // mTimer = timer;
    // timer.schedule(new InertiaTimerTask(this, velocityY, timer), 0L, 20L);
    // }

    protected final void onItemSelected() {
        if (onItemSelectedListener != null) {
            postDelayed(new OnItemSelectedRunnable(this), 200L);
        }
    }

    /**
     * link https://github.com/weidongjian/androidWheelView/issues/10
     *
     * @param scaleX
     */
    @Override
    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    /**
     * set current item position
     * @param position
     */
    public void setCurrentPosition(int position) {
        if (items == null || items.isEmpty()) {
            return;
        }
        int size = items.size();
        if (position >= 0 && position < size) {
            if (position != getSelectedItem()) {
                initPosition = position;
                totalScrollY = 0;
                mOffset = 0;
                invalidate();
            }
            // changed: 即使选中项没有改变，也发出通知
            onItemSelected();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (items == null || items.isEmpty()) {
            return;
        }

        change = (int) (totalScrollY / (lineSpacingMultiplier * itemTextHeight));
        preCurrentIndex = initPosition + change % items.size();

        if (!isLoop) {
            if (preCurrentIndex < 0) {
                preCurrentIndex = 0;
            }
            if (preCurrentIndex > items.size() - 1) {
                preCurrentIndex = items.size() - 1;
            }
        } else {
            if (preCurrentIndex < 0) {
                preCurrentIndex = items.size() + preCurrentIndex;
            }
            if (preCurrentIndex > items.size() - 1) {
                preCurrentIndex = preCurrentIndex - items.size();
            }
        }
        // changed: 记录当前显示的index
        selectedIndex = preCurrentIndex;

        int j2 = (int) (totalScrollY % (lineSpacingMultiplier * itemTextHeight));
        // put value to drawingString
        int k1 = 0;
        while (k1 < itemsVisibleCount) {
            int l1 = preCurrentIndex - (itemsVisibleCount / 2 - k1);
            if (isLoop) {
                while (l1 < 0) {
                    l1 = l1 + items.size();
                }
                while (l1 > items.size() - 1) {
                    l1 = l1 - items.size();
                }
                drawingStrings[k1] = items.get(l1);
            } else if (l1 < 0) {
                drawingStrings[k1] = "";
            } else if (l1 > items.size() - 1) {
                drawingStrings[k1] = "";
            } else {
                drawingStrings[k1] = items.get(l1);
            }
            k1++;
        }
        canvas.drawLine(paddingLeft, firstLineY, measuredWidth, firstLineY, paintIndicator);
        canvas.drawLine(paddingLeft, secondLineY, measuredWidth, secondLineY, paintIndicator);

        final int itemWidth = ((View)getParent()).getWidth();

        int i = 0;
        while (i < itemsVisibleCount) {
            canvas.save();
            // L(弧长)=α（弧度）* r(半径) （弧度制）
            // 求弧度--> (L * π ) / (π * r)   (弧长X派/半圆周长)
            float itemHeight = itemTextHeight * lineSpacingMultiplier;
            double radian = ((itemHeight * i - j2) * Math.PI) / halfCircumference;
            if (radian >= Math.PI || radian <= 0) {
                canvas.restore();
            } else {
                int translateY = (int) (radius - Math.cos(radian) * radius - (Math.sin(radian) * itemTextHeight) / 2D);
                canvas.translate(0.0F, translateY);
                canvas.scale(1.0F, (float) Math.sin(radian));

                // changed: 字体大小自适应
                String text = drawingStrings[i];
                int textLength = text.isEmpty() ? 1 : text.length();
                if (maxTextSize == -1) {
                    maxTextSize = dp2px(35);
                }
                final int textSize = Math.min(this.textSize, Math.min(itemWidth/(textLength+2), maxTextSize));

                paintOuterText.setTextSize(textSize);
                paintCenterText.setTextSize(textSize);

                paintCenterText.getTextBounds("\u661F\u671F", 0, 2, tempRect); // 星期
                textHeight = tempRect.height();

                if (translateY <= firstLineY && itemTextHeight + translateY >= firstLineY) {
                    // first divider
                    canvas.save();
                    canvas.clipRect(0, 0, measuredWidth, firstLineY - translateY);
                    drawOuterText(canvas, i);
                    canvas.restore();
                    canvas.save();
                    canvas.clipRect(0, firstLineY - translateY, measuredWidth, (int) (itemHeight));
                    drawCenterText(canvas, i);
                    canvas.restore();
                } else if (translateY <= secondLineY && itemTextHeight + translateY >= secondLineY) {
                    // second divider
                    canvas.save();
                    canvas.clipRect(0, 0, measuredWidth, secondLineY - translateY);
                    drawCenterText(canvas, i);
                    canvas.restore();
                    canvas.save();
                    canvas.clipRect(0, secondLineY - translateY, measuredWidth, (int) (itemHeight));
                    drawOuterText(canvas, i);
                    canvas.restore();
                } else if (translateY >= firstLineY && itemTextHeight + translateY <= secondLineY) {
                    // center item
                    canvas.clipRect(0, 0, measuredWidth, (int) (itemHeight));
                    drawCenterText(canvas, i);
                } else {
                    // other item
                    canvas.clipRect(0, 0, measuredWidth, (int) (itemHeight));
                    drawOuterText(canvas, i);
                }
                canvas.restore();
            }
            i++;
        }
    }


    private void drawOuterText(Canvas canvas, int position) {
        canvas.drawText(drawingStrings[position], getTextX(drawingStrings[position], paintOuterText, tempRect),
                getDrawingY(), paintOuterText);
    }

    private void drawCenterText(Canvas canvas, int position) {
        canvas.drawText(drawingStrings[position], getTextX(drawingStrings[position], paintOuterText, tempRect),
                getDrawingY(), paintCenterText);
    }

    private int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    private int getDrawingY() {
        if (itemTextHeight > textHeight) {
            return itemTextHeight - ((itemTextHeight - textHeight) / 2);
        } else {
            return itemTextHeight;
        }
    }


    // text start drawing position
    private int getTextX(String a, Paint paint, Rect rect) {
        paint.getTextBounds(a, 0, a.length(), rect);
        // 获取到的是实际文字宽度
        int textWidth = rect.width();
        textWidth *= scaleX;
        return (measuredWidth - paddingLeft - textWidth) / 2 + paddingLeft;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initPaintsIfPossible();
        remeasure();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean eventConsumed = flingGestureDetector.onTouchEvent(event);
        float itemHeight = lineSpacingMultiplier * itemTextHeight;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                cancelFuture();
                previousY = event.getRawY();
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                float dy = previousY - event.getRawY();
                previousY = event.getRawY();

                totalScrollY = (int) (totalScrollY + dy);

                if (!isLoop) {
                    float top = -initPosition * itemHeight;
                    float bottom = (items.size() - 1 - initPosition) * itemHeight;

                    if (totalScrollY < top) {
                        totalScrollY = (int) top;
                    } else if (totalScrollY > bottom) {
                        totalScrollY = (int) bottom;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            default:
                if (!eventConsumed) {
                    float y = event.getY();
                    double l = Math.acos((radius - y) / radius) * radius;
                    int circlePosition = (int) ((l + itemHeight / 2) / itemHeight);

                    float extraOffset = (totalScrollY % itemHeight + itemHeight) % itemHeight;
                    mOffset = (int) ((circlePosition - itemsVisibleCount / 2) * itemHeight - extraOffset);

                    if ((System.currentTimeMillis() - startTime) > 120) {
                        // 处理拖拽事件
                        smoothScroll(ACTION.DRAG);
                    } else {
                        // 处理条目点击事件
                        smoothScroll(ACTION.CLICK);
                    }
                }
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }

        invalidate();
        return true;
    }
}
