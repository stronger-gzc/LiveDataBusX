package com.gzc.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gzc.livedatabusx.LiveDataBusX;
import com.livedatabusx.annotation.Observe;
import com.livedatabusx.annotation.ThreadMode;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LiveDataBusX.getInstance().observe(this);
    }

    @Observe(threadMode = ThreadMode.MAIN,key = "test")
    public void test(String str){

    }
}
