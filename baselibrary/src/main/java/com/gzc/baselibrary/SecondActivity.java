package com.gzc.baselibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.gzc.livedatabusx.LiveDataBusX;
import com.livedatabusx.annotation.Observe;
import com.livedatabusx.annotation.ThreadMode;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        LiveDataBusX.getInstance().observe(this);
    }

    @Observe(threadMode = ThreadMode.MAIN,key="TestBean",sticky = true)
    public void getData(Test2Bean test2Bean){
        Log.e("guanzhenchuang","收到消息");
    }
}