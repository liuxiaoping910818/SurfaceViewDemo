package com.example.android.surfaceviewdemo3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Administrator on 2016/4/26.
 */
public class MySurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Canvas mCanvas;

    // 用于给制子线程
    private Thread t;

    //线程开启开关
    private boolean isRunning;

    //设置所需要的变量:分别有名称，图片，颜色
    private String[] mStrs=new String[]{

            "单反相机","IPAD","恭喜发财",
            "IPHONE","服装一套","恭喜发财"
    };


    private int [] mImgIds=new int[]{

            R.drawable.danfan,
            R.drawable.ipad,
            R.drawable.f015,
            R.drawable.iphone,
            R.drawable.meizi,
            R.drawable.f040
    };
    private int mItemCount=6;
    private int[] mColor=new int[]{

            0xFFFC300,0XFFF17E01,
            0xFFFC300,0XFFF17E01,
            0xFFFC300,0XFFF17E01
    };
    //与图片相对应的BITMAP数组
    private Bitmap[] mImagBitmap;

    //整个盘块的范围
    private RectF mRange;

    //整个盘块的直径
    private int mRadius;
    //绘制盘块的画笔
    private Paint mArcPaint;
    //绘制文本的画笔
    private Paint mTextPaint;
    //盘块滚动的速度
    private double mSpeed=10;

    //盘块滚动的角度
    private volatile int mStartAngle=0;

    //判断是否点击了停止按钮
    private boolean isShouldEnd;
    //转盘的中心位置
    private int mCenter;

    //这里直接以paddingleft为准
    private int mPadding;
    //转盘的背景
    private Bitmap mBgBitmap= BitmapFactory.decodeResource(getResources(),R.drawable.bg2);

    //文字的大小
    private float mTextSize= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,20,
            getResources().getDisplayMetrics());

    public MySurfaceView(Context context) {
        super(context);
    }


    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder=getHolder();

        mHolder.addCallback(this);

        //可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常量
        setKeepScreenOn(true);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        //初始化盘块的画笔
        mArcPaint=new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);

        //初始化文本的画笔
        mTextPaint=new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSize);

        //盘块绘制的范围,其是一个正方形
        mRange=new RectF(mPadding,mPadding,mPadding+mRadius,mPadding+mRadius);

        //初始化图片
        mImagBitmap=new Bitmap[mItemCount];

        for (int i = 0; i < mItemCount; i++) {

            mImagBitmap[i]=BitmapFactory.decodeResource(getResources(),mImgIds[i]);
        }
        //开启子线程
        isRunning=true;
        t=new Thread(this);
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        isRunning=false;
    }


    //对应上面的surfaceCreated里的统一线程的开启
    @Override
    public void run() {

        while (isRunning){

            long start=System.currentTimeMillis();
            draw();
            long end=System.currentTimeMillis();

            if (end-start<50){

                try {
                    Thread.sleep(50-(end-start));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width=Math.min(getMeasuredWidth(),getMeasuredHeight());

        mPadding=getPaddingLeft();
        //直径
        mRadius=width-mPadding*2;
        //中心点
        mCenter=width/2;

        //设置为正方形
        setMeasuredDimension(width,width);

    }

    private void draw() {

        try {

            //获取SurfaceViewR Canvas
            mCanvas=mHolder.lockCanvas();
            if (mCanvas!=null){

                //开始绘制背景
                drawBg();

                //绘制盘块
                float tmpAngle=mStartAngle;
                float sweepAngle=360/mItemCount;
                for (int i = 0; i < mItemCount; i++) {

                    mArcPaint.setColor(mColor[i]);
                    mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);

                    //开始绘制文本
                    drawText(tmpAngle, sweepAngle, mStrs[i]);

                    //绘制icon
                    drawIcon(tmpAngle,mImagBitmap[i]);

                    tmpAngle+=sweepAngle;

                    //如果点击了停止
                    if (isShouldEnd){

                        mSpeed-=1;
                    }
                    if (mSpeed<=0){

                        mSpeed=0;
                        isShouldEnd=false;
                    }

                }

               mStartAngle+=mSpeed;

            }
        }catch (Exception e){

            e.printStackTrace();
        }finally {

            //釋放canvas
            if (mCanvas!=null){

                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }

    }

    /**
     * 点击开始旋转
     */
    public void luckStart(){



       mSpeed=50;
        isShouldEnd=false;
    }
    /**
     * 点击开始旋转
     */
    public void luckStart(int index){

        //计算每一项的角度
        float angle=360/mItemCount;
        //计算每一项中奖的范围
        //1》150~210；2》210`270

        float from=270-(index+1)*angle;
        float end=from+angle;

        //设置停下来所需要的的旋转的距离
        float targetFrom=4*360+from;
        float targetEnd=4*360+end;

        float v1= (float) ((-1+Math.sqrt(1+8*targetFrom))/2);
        float v2= (float) ((-1+Math.sqrt(1+8*targetEnd))/2);

        mSpeed=v1+Math.random()*(v2-v1);

        //mSpeed=50;
        isShouldEnd=false;
    }

    /**
     * 点击停止旋转
     */
    public void luckStop(){

        mStartAngle=0;
        isShouldEnd=true;

    }

    /**
     * 转盘是否在旋转
     * @return
     */
    public boolean isStart(){

        return mSpeed!=0;
    }

    /**
     * 判断是否停止旋转
     * @return
     */
    public boolean isShouldEnd(){

        return isShouldEnd;
    }

    /**
     * 绘制icon :这块是最难理解的，
     * @param tmpAngle
     * @param
     */
    private void drawIcon(float tmpAngle, Bitmap bitmap) {

        //y设置图片的宽度为直径的1/8，每一张图片所在位图都是一个正方形
        int imgWidth=mRadius/8;
        float angle= (float) ((tmpAngle+360/mItemCount/2)*Math.PI/180);
        //图片中心坐标
        int  x= (int) (mCenter+mRadius/2/2*Math.cos(angle));
        int  y= (int) (mCenter+mRadius/2/2*Math.sin(angle));

        //确定图片的位置
        Rect rect=new Rect(x-imgWidth,y-imgWidth/2,x+imgWidth/2,y+imgWidth/2);
        mCanvas.drawBitmap(bitmap,null,rect,null);
    }

    /**
     *
     * 绘制每个盘块的文本：绘制思路：使用Path类进行绘制，具体方法里的参数说明可查看API
     * @param tmpAngle
     * @param sweepAngle
     * @param
     */
    private void drawText(float tmpAngle, float sweepAngle, String string) {

        Path path=new Path();
        path.addArc(mRange,tmpAngle,sweepAngle);

        float textWidth=mTextPaint.measureText(string);
        int hOffset= (int) (mRadius*Math.PI/mItemCount/2-textWidth/2);
        int vOffset=mRadius/2/6;//设置偏移量
        mCanvas.drawTextOnPath(string,path,hOffset,vOffset,mTextPaint);

    }

    //绘制背景
    private void drawBg() {

        mCanvas.drawColor(Color.WHITE);
        mCanvas.drawBitmap(mBgBitmap,null,new Rect(mPadding/2,mPadding/2,
                getMeasuredWidth()-mPadding/2,getMeasuredHeight()-mPadding/2),null);
    }
}
