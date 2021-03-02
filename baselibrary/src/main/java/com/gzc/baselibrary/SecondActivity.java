package com.gzc.baselibrary;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

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

    @Observe(threadMode = ThreadMode.MAIN,key="TestBean1")
    public void getData(Test1Bean test1Bean){
        Log.e("guanzhenchuang","收到消息1");
    }

    @Observe(threadMode = ThreadMode.MAIN,key="TestBean2",sticky = true)
    public void getData(Test2Bean test2Bean){
        Log.e("guanzhenchuang","收到消息2");
    }

    public void sendDataToMainActivity(View view) {
        LiveDataBusX.getInstance().post("TestBean3",new Test3Bean());
    }
}