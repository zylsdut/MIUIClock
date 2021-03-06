package com.dafasoft.miuiclock;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import java.util.Calendar;

/**
 * Created by zhangyulong on 2016/12/6.
 */

public class MIUIClock extends View {

    private Paint mPaint;
    private Context mContext;
    private Paint mDefaultPaint;
    private Paint mGraduationPaint;
    private Rect mContentRect;
    private Path mTriangle;
    private Point mGraduationPoint;
    private Point mCenterPoint;
    private Rect mDstCircleRect; //时钟中心圆圈所在位置
    private Rect mDstHourRect; //时针所在位置
    private Rect mDstMinuteRect; //分针所在位置
    private Calendar mCalendar;
    private ValueAnimator mClockAnimator;
    private ValueAnimator mSecondAnimator;
    private float mSecondStartAngle; //圆环的起始角度
    private float mClockAngle; //三角指针角度
    private int mSecondAngle; //圆环角度
    private float mHourAngle; //时针角度
    private float mMinuteAngle; //分针角度
    private static final int GRADUATION_LENGTH = 50; //圆环刻度长度
    private static final int GRADUATION_COUNT = 180; //一圈圆环刻度的数量
    private static final int ROUND_ANGLE = 360; //圆一周的角度
    private static final int PER_GRADUATION_ANGLE = ROUND_ANGLE / GRADUATION_COUNT; //每个刻度的角度
    private Bitmap mCircleBitmap; //时钟中心的圆圈
    private Bitmap mHourBitmap; //时针
    private Bitmap mMinuteBitmap; //分针

    public MIUIClock(Context context) {
        super(context);
        init(context);
    }

    public MIUIClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mDefaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAlpha(120);

        mGraduationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGraduationPaint.setColor(Color.WHITE);
        mGraduationPaint.setStrokeWidth(4);
        mGraduationPaint.setStrokeCap(Paint.Cap.ROUND);
        mCalendar = Calendar.getInstance();

        mCircleBitmap = BitmapFactory.decodeResource(mContext.getResources() , R.mipmap.ic_circle);
        mHourBitmap = BitmapFactory.decodeResource(mContext.getResources() , R.mipmap.ic_hour);
        mMinuteBitmap = BitmapFactory.decodeResource(mContext.getResources() , R.mipmap.ic_minute);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mContentRect = new Rect(0 , 0 , w, h);
        mGraduationPoint = new Point(w /2 , 0);
        mCenterPoint = new Point(w /2 , h /2);
        //初始化三角
        mTriangle = new Path();
        mTriangle.moveTo(mGraduationPoint.x , mGraduationPoint.y + 70);// 此点为多边形的起点
        mTriangle.lineTo(mGraduationPoint.x - 20, mGraduationPoint.y + 97);
        mTriangle.lineTo(mGraduationPoint.x + 20, mGraduationPoint.y + 97);
        mTriangle.close(); // 使这些点构成封闭的多边形

        //初始化circle所在位置
        int circleWidth = mCircleBitmap.getWidth();
        int circleHeight = mCircleBitmap.getHeight();
        mDstCircleRect = new Rect(mCenterPoint.x - circleWidth /2 , mCenterPoint.y - circleHeight/2 ,
                mCenterPoint.x + circleWidth /2 , mCenterPoint.y  + circleHeight /2);

        //初始化时针所在位置
        int hourWidth = mHourBitmap.getWidth();
        int hourHeight = mHourBitmap.getHeight();
        mDstHourRect = new Rect(mCenterPoint.x - hourWidth / 2 , mCenterPoint.y - hourHeight - 20 ,
                mCenterPoint.x + hourWidth / 2 , mCenterPoint.y - 20);

        //初始化分针所在位置
        int minuteWidth = mMinuteBitmap.getWidth();
        int minuteHeight = mMinuteBitmap.getHeight();
        mDstMinuteRect = new Rect(mCenterPoint.x - minuteWidth / 2 , mCenterPoint.y - minuteHeight - 20 ,
                mCenterPoint.x + minuteWidth / 2 , mCenterPoint.y - 20);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int layerCount = canvas.saveLayer(0 , 0 , canvas.getWidth() , canvas.getHeight() , mDefaultPaint , Canvas.ALL_SAVE_FLAG);
        canvas.rotate(mClockAngle + mSecondStartAngle , mCenterPoint.x , mCenterPoint.y);

        //画三角
        canvas.drawPath(mTriangle, mPaint);

        //画圆圈
        canvas.drawBitmap(mCircleBitmap , null , mDstCircleRect , mDefaultPaint);
        canvas.restoreToCount(layerCount);

        //画时针
        layerCount = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), mDefaultPaint, Canvas.ALL_SAVE_FLAG); //新建图层
        canvas.rotate(mHourAngle , mCenterPoint.x , mCenterPoint.y);
        canvas.drawBitmap(mHourBitmap , null , mDstHourRect , mDefaultPaint);
        canvas.restoreToCount(layerCount);//将图层恢复到屏幕上

        //画分针
        layerCount = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), mDefaultPaint, Canvas.ALL_SAVE_FLAG);
        canvas.rotate(mMinuteAngle , mCenterPoint.x , mCenterPoint.y);
        canvas.drawBitmap(mMinuteBitmap , null , mDstMinuteRect , mDefaultPaint);
        canvas.restoreToCount(layerCount);

        //画刻度
        layerCount = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), mDefaultPaint, Canvas.ALL_SAVE_FLAG);
        canvas.rotate(mSecondAngle + mSecondStartAngle , mCenterPoint.x, mCenterPoint.y);
        for (int i = 0; i < GRADUATION_COUNT; i++) {
            int alpha = 255 - i * 3;
            if (alpha > 120) {
                mGraduationPaint.setAlpha(alpha);
            }
            canvas.drawLine(mGraduationPoint.x, mGraduationPoint.y + 5, mGraduationPoint.x, mGraduationPoint.y + GRADUATION_LENGTH, mGraduationPaint);
            canvas.rotate(-PER_GRADUATION_ANGLE, mCenterPoint.x, mCenterPoint.y);
        }
        canvas.restoreToCount(layerCount);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec , heightMeasureSpec);
    }

    public void startAnimation() {
        //三角刻度动画
        mClockAnimator = ValueAnimator.ofFloat(0 , GRADUATION_COUNT);
        mClockAnimator.setDuration(Constants.MINUTE);
        mClockAnimator.setInterpolator(new LinearInterpolator());
        mClockAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mClockAngle = (float) valueAnimator.getAnimatedValue() * PER_GRADUATION_ANGLE;
            }
        });
        mClockAnimator.setRepeatCount(ValueAnimator.INFINITE);

        //圆圈刻度动画
        mSecondAnimator = ValueAnimator.ofInt(0 , GRADUATION_COUNT);
        mSecondAnimator.setDuration(Constants.MINUTE);
        mSecondAnimator.setInterpolator(new LinearInterpolator());
        mSecondAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mSecondAngle = (int) valueAnimator.getAnimatedValue() * PER_GRADUATION_ANGLE;
                mHourAngle = (mCalendar.get(Calendar.HOUR) + ((float)mCalendar.get(Calendar.MINUTE)) / 60) * (360 / 12);
                mMinuteAngle = (mCalendar.get(Calendar.MINUTE) + ((float)mCalendar.get(Calendar.SECOND)) / 60) * (360 / 60);
                invalidate();
            }
        });
        mSecondAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mSecondStartAngle = Math.round((mCalendar.get(Calendar.SECOND) + mCalendar.get(Calendar.MILLISECOND) / Constants.SECOND) * (360 / 60));
                mHourAngle = (mCalendar.get(Calendar.HOUR) + ((float)mCalendar.get(Calendar.MINUTE)) / 60) * (360 / 12);
                mMinuteAngle = (mCalendar.get(Calendar.MINUTE) + ((float)mCalendar.get(Calendar.SECOND)) / 60) * (360 / 60);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mSecondAnimator.setRepeatCount(ValueAnimator.INFINITE);

        mSecondAnimator.start();
        mClockAnimator.start();
    }

    public void cancelAnimation() {
        if (mClockAnimator != null) {
            mClockAnimator.removeAllUpdateListeners();
            mClockAnimator.removeAllListeners();
            mClockAnimator.cancel();
            mClockAnimator = null;
        }
    }
}
