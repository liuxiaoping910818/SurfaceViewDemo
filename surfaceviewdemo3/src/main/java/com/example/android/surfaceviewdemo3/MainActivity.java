package com.example.android.surfaceviewdemo3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView mstart;

    private ImageView mStop;
    private MySurfaceView mySurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySurfaceView= (MySurfaceView) findViewById(R.id.lucky_pan);
        mstart= (ImageView) findViewById(R.id.iv_start);
        //mStop= (ImageView) findViewById(R.id.iv_stop);
        mstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mySurfaceView.isStart()){

                    mySurfaceView.luckStart(1);
                    mstart.setImageResource(R.drawable.stop);
                }else {

                    if (!mySurfaceView.isShouldEnd()){

                        mySurfaceView.luckStop();
                        mstart.setImageResource(R.drawable.start);
                    }
                }
            }
        });
    }
}
