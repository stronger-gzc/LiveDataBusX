package com.gzc.example;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gzc.example.test.TestBean2;
import com.gzc.livedatabusx.LiveDataBusX;
import com.livedatabusx.annotion.Observe;
import com.livedatabusx.annotion.ThreadMode;

public class SecondActivity extends AppCompatActivity {
    private TestBean2 bean2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        LiveDataBusX.getInstance().observe(this);
    }

    @Observe(threadMode = ThreadMode.MAIN,key="TestBean2",sticky = true)
    public void testBean2(TestBean2 testBean2){
        bean2 = testBean2;
        Log.e("guanzhenchuang","SecondActivity  收到消息===>TestBean222222");

    }

}