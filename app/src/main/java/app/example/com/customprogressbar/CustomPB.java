package app.example.com.customprogressbar;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mnkj on 2017/11/13.
 */

public class CustomPB extends View {
    private Path capsulePath;//胶囊路径
    private Path rectPath;//进度条路径
    private Paint pathPaint;//画笔
    private Paint textPaint;//字体画笔
    private int radius = 120;
    private int kzradius = 40;//控制圆的半径
    private int windowWidth, windowHeight;
    private int BaseLineY;//绘制百分比y(基线)
    private RectF mRectF;//圆角矩形
    private List<PointF> points;
    private List<PointF> tempPoints = new ArrayList<>();//存储点击确认是的两个控制点
    private int index;//记录当前移动的点
    private ValueAnimator circleAnimStrat, circleAnimEnd, ProgressAnim;
    private int changeRadius = 10;//控制圆的变化的半径
    private float RectNowW;//进度条的当前长度
    private int RectH = radius * 3;//进度条的高度
    private int RectW;//进度条的总长度


    public CustomPB(Context context) {
        super(context);
        initView(context);
    }

    public CustomPB(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CustomPB(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context) {
        WindowManager windowManager = ((Activity) context).getWindowManager();
        capsulePath = new Path();
        windowWidth = windowManager.getDefaultDisplay().getWidth();
        windowHeight = windowManager.getDefaultDisplay().getHeight();

        BaseLineY = windowHeight / 2;

        pathPaint = new Paint();
        pathPaint.setColor(Color.BLACK);
        pathPaint.setStrokeWidth(4);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeCap(Paint.Cap.ROUND);
        pathPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.GRAY);
        textPaint.setTextSize(48);
        textPaint.setTypeface(Typeface.SERIF);
        textPaint.setFakeBoldText(true);

        circleAnimStrat = ValueAnimator.ofInt(10, 40);
        circleAnimStrat.setDuration(200);
        circleAnimStrat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                changeRadius = (int) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLine(canvas);
        drawStroke(canvas);
        canvas.clipPath(capsulePath);
        drawRect(canvas);
        drawText(canvas);
    }

    private void drawStroke(Canvas canvas) {
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setColor(Color.WHITE);
        canvas.drawRoundRect(mRectF, 1000, 1000, pathPaint);
    }

    private void drawLine(Canvas canvas) {
        capsulePath.reset();
        //获取胶囊体的坐标
        int left = (windowWidth / 2) - 2 * radius;
        int top = (windowHeight / 2) - radius;
        int right = left + 4 * radius;
        int bottom = top + 2 * radius;
        if (points == null) {
            points = new ArrayList<>();
            //添加起始点终值点
            points.add(new PointF(left, top + radius));
            points.add(new PointF(right, top + radius));

            mRectF = new RectF(left + 2, top + 2, right - 2, bottom - 2);
        }

        capsulePath.setFillType(Path.FillType.WINDING);
        RectF rectF = new RectF(left + radius, top, right - radius, bottom);
        capsulePath.addRect(rectF, Path.Direction.CW);

        capsulePath.addCircle(left + radius, top + radius, radius, Path.Direction.CW);
        capsulePath.addCircle(right - radius, top + radius, radius, Path.Direction.CW);
        pathPaint.setColor(Color.WHITE);
        pathPaint.setStrokeWidth(4);
        RectF rectF1 = new RectF(points.get(0).x - 2, windowHeight / 2 - 2, points.get(1).x + 2, windowHeight / 2 + 2);
        capsulePath.addRect(rectF1, Path.Direction.CW);
        capsulePath.addCircle(points.get(0).x, points.get(0).y, changeRadius, Path.Direction.CW);
        capsulePath.addCircle(points.get(1).x, points.get(1).y, changeRadius, Path.Direction.CW);

        canvas.drawLine(points.get(0).x, points.get(0).y, windowWidth / 2 - radius * 2, windowHeight / 2, pathPaint);
        canvas.drawLine(windowWidth / 2 + radius * 2, windowHeight / 2, points.get(1).x, points.get(1).y, pathPaint);

        pathPaint.setColor(Color.BLUE);
        canvas.drawCircle(points.get(0).x, points.get(0).y, index == 0 ? changeRadius : 10, pathPaint);
        pathPaint.setColor(Color.CYAN);
        canvas.drawCircle(points.get(1).x, points.get(1).y, index == 1 ? changeRadius : 10, pathPaint);
    }

    private void drawRect(Canvas canvas) {
        if (tempPoints.size() == 0) return;
        RectF rectF = new RectF(tempPoints.get(0).x, tempPoints.get(0).y - RectH / 2, tempPoints.get(0).x + RectNowW, tempPoints.get(0).y + RectH / 2);
        pathPaint.setColor(Color.parseColor("#FF3300"));
        pathPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rectF, pathPaint);
    }

    private void drawText(Canvas canvas) {
        int p = (int) (((RectNowW*100) / RectW));
        canvas.drawText(p + "%", windowWidth/2, windowHeight / 2, textPaint);
    }


    public void startAnim() {
        tempPoints.clear();
        tempPoints.addAll(points);
        RectW = (int) (tempPoints.get(1).x - tempPoints.get(0).x);
        ProgressAnim = ValueAnimator.ofFloat(0, RectW);
        ProgressAnim.setDuration(2000);
        ProgressAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                RectNowW = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        ProgressAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                RectNowW = 0;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        ProgressAnim.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < 2; i++) {
                    if (event.getX() > points.get(i).x - kzradius && event.getX() < points.get(i).x + kzradius
                            && event.getY() < points.get(i).y + kzradius && event.getY() > points.get(i).y - radius) {
                        index = i;
                        if (!circleAnimStrat.isRunning()) {
                            circleAnimStrat.start();
                        }
                        return true;
                    } else {
                        index = -1;
                    }
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                if (index == -1) return false;
                if (index == 0 && event.getX() > windowWidth / 2 - radius * 2 + 6) {
                    points.get(index).x = windowWidth / 2 - radius * 2;
                    return false;
                } else if (index == 1 && event.getX() < windowWidth / 2 + radius * 2 - 6) {
                    points.get(index).x = windowWidth / 2 + radius * 2;
                    return false;
                }
                points.get(index).x = event.getX();
                postInvalidate();
                return true;
            case MotionEvent.ACTION_UP:
                circleAnimEnd = ValueAnimator.ofInt(changeRadius, 10);
                circleAnimEnd.setDuration(200);
                circleAnimEnd.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        changeRadius = (int) valueAnimator.getAnimatedValue();
                        postInvalidate();
                    }
                });
                circleAnimEnd.start();
                return true;
        }

        return false;
    }
}
