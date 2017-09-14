package com.vincent.areaview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Vincent Woo
 * Date: 2016/10/28
 * Time: 11:40
 */

public class AreaView extends ImageView {
    private int mActualHeight;  // The Original Height of the image
    private int mActualWidth;   // The Original Width of the image
    private int mViewHeight;    // The ImageView Height
    private int mViewWidth;     // The ImageView Width
    private Paint mPaint;
    private boolean isTransform = false;
    private ArrayList<Shape> mShapeList;
    private Shape mCurrentShape;

    public AreaView(Context ctx) {
        this(ctx, null);
    }

    public AreaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.FIT_CENTER);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#80000000"));
        mPaint.setStrokeWidth(2.0f);
        mPaint.setStyle(Paint.Style.FILL);

        mShapeList = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        transform();

        if (mCurrentShape != null) {
            canvas.drawPath(mCurrentShape.getPath(), mPaint);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Shape shape = isEventInPath(event);
            if (shape != null) {
                mCurrentShape = shape;
                invalidate();
                return true;
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if(mCurrentShape.isInPath(event)){
                mCurrentShape.clickAreaView();
            }

            mCurrentShape = null;
            invalidate();
        }

        if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            mCurrentShape = null;
            invalidate();
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);

        if (bm != null) {
            mActualHeight = bm.getHeight();
            mActualWidth = bm.getWidth();

            transform();
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);

        if (drawable != null) {
            mActualHeight = drawable.getIntrinsicHeight();
            mActualWidth = drawable.getIntrinsicWidth();

            transform();
        }
    }

    private Shape isEventInPath(MotionEvent event) {
        for (Shape shape : mShapeList) {
            if (shape.isInPath(event)) {
                return shape;
            }
        }

        return null;
    }

    private float getScalePointX(float x) {
        return getScaleHeight() > getScaleWidth() ? x / getScale() + getDeltaWidth() : x / getScale();
    }

    private float getScalePointY(float y) {
        return getScaleHeight() > getScaleWidth() ? y / getScale() : y / getScale() + getDeltaHeight();
    }

    private float getScale() {
        return Math.max(getScaleHeight(), getScaleWidth());
    }

    private float getScaleHeight() {
        return mActualHeight * 1.0f / mViewHeight * 1.0f;
    }

    private float getScaleWidth() {
        return mActualWidth * 1.0f / mViewWidth * 1.0f;
    }

    private float getDeltaHeight() {
        float scaleHeight = getScaleHeight();
        float scaleWidth = getScaleWidth();

        if (scaleHeight > scaleWidth) {
            return 0;
        } else {
            return (mViewHeight - ((mActualHeight * 1.0f) / scaleWidth)) / 2;
        }
    }

    private float getDeltaWidth() {
        float scaleHeight = getScaleHeight();
        float scaleWidth = getScaleWidth();

        if (scaleWidth > scaleHeight) {
            return 0;
        } else {
            return (mViewWidth - ((mActualWidth * 1.0f) / scaleHeight)) / 2;
        }
    }

    private void transform() {
        if (!isTransform && !(mViewHeight == 0 || mViewWidth == 0 || mActualHeight == 0 || mActualWidth == 0)) {
            for (Shape shape : mShapeList) {
                for (int i = 0; i < shape.getPoints().length; i++) {
                    transformPoint(shape.getPoints()[i]);
                }
                if (shape.getType() == Shape.CIRCLE) {
                    float scale = getScale();
                    shape.setRadius(shape.mRadius/scale);
                }
                shape.initShape();
            }
            isTransform = true;
        }
    }

    private PointF transformPoint(PointF point) {
        point.x = getScalePointX(point.x);
        point.y = getScalePointY(point.y);
        return point;
    }

    public void addPoly(float[] poly, OnAreaViewClickListener listener) {
        Shape shape = new Shape(poly);
        shape.setListener(listener);
        addShape(shape);
    }

    public void addCircle(float[] circle, float radius, OnAreaViewClickListener listener) {
        Shape shape = new Shape(circle);
        shape.setRadius(radius);
        shape.setListener(listener);
        addShape(shape);
    }

    private void addShape(Shape shape) {
        mShapeList.add(shape);
    }

    public class Shape {
        public static final int CIRCLE = 0;
        public static final int POLY = 1;
        private int mType;   // 0:Circle, 1:Poly
        private PointF[] mPoints;
        private Region mRegion;
        private Path mPath;
        private float mRadius;
        private OnAreaViewClickListener mListener;

        public Shape(float[] coordinates) {
            if (coordinates.length == 0) {
                throw new RuntimeException("Coordinates empty");
            }

            if (coordinates.length % 2 == 1) {
                throw new RuntimeException("The number of coordinates array must be even");
            }

            if (coordinates.length == 2) {
                mType = CIRCLE;
            } else {
                mType = POLY;
            }

            mPoints = new PointF[coordinates.length / 2];
            for (int i = 0, j = 0; i < coordinates.length; i += 2, j++) {
                PointF point = new PointF(coordinates[i], coordinates[i + 1]);
                mPoints[j] = point;
            }

            mRegion = new Region();
            mPath = new Path();

        }

        public boolean isInPath(MotionEvent event) {
            return mRegion.contains((int) event.getX(), (int) event.getY());
        }

        private void initShape() {
            RectF bounds = new RectF();
            if (mType == POLY) {
                mPath.moveTo(mPoints[0].x, mPoints[0].y);
                for (int i = 1; i < mPoints.length; i++) {
                    mPath.lineTo(mPoints[i].x, mPoints[i].y);
                }
                mPath.close();
            } else {
                mPath.addCircle(mPoints[0].x, mPoints[0].y, mRadius, Path.Direction.CW);
            }
            mPath.computeBounds(bounds, true);
            mRegion.setPath(mPath, new Region((int) bounds.left,
                    (int) bounds.top, (int) bounds.right, (int) bounds.bottom));
        }

        public int getType() {
            return mType;
        }

        public void setType(int type) {
            this.mType = type;
        }

        public PointF[] getPoints() {
            return mPoints;
        }

        public Path getPath() {
            return mPath;
        }

        public Region getRegion() {
            return mRegion;
        }

        public void setRegion(Region region) {
            this.mRegion = region;
        }

        public float getRadius() {
            return mRadius;
        }

        public void setRadius(float radius) {
            this.mRadius = radius;
        }

        public void setListener(OnAreaViewClickListener listener) {
            this.mListener = listener;
        }

        public void clickAreaView() {
            if (mListener != null) {
                mListener.onAreaViewClick(this);
            }
        }
    }

    public interface OnAreaViewClickListener {
        void onAreaViewClick(Shape shape);
    }
}
