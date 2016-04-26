package com.example.android.surfaceviewdemo3;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Administrator on 2016/4/26.,一般情况下的编写模版
 */
public class SurfaceViewTemplate extends SurfaceView implements SurfaceHolder.Callback,Runnable{

    private SurfaceHolder mHolder;
    private Canvas mCanvas;

   // 用于给制子线程
    private Thread t;

    //线程开启开关
    private boolean isRunning;


    public SurfaceViewTemplate(Context context) {
        super(context);
    }

    public SurfaceViewTemplate(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder=getHolder();

        mHolder.addCallback(this);

        //可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常量
        setKeepScreenOn(true);

    }

    public SurfaceViewTemplate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

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

            draw();
        }
    }

    private void draw() {

        try {

            //获取SurfaceViewR Canvas
            mCanvas=mHolder.lockCanvas();
            if (mCanvas!=null){

                //开始绘制

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
}
