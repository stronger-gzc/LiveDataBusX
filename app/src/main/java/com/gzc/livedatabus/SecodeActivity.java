package com.gzc.livedatabus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.gzc.livedatabus.test.TestBean2;
import com.gzc.livedatabus.test.TestBean4;
import com.livedatabus.annotion.Observe;
import com.livedatabus.annotion.ThreadMode;

public class SecodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secode);

        LiveDataBus.getInstance().observe(this);
    }

    @Observe(threadMode = ThreadMode.MAIN,key="TestBean2",sticky = true)
    public void testBean2(TestBean2 testBean2){
        Log.e("guanzhenchuang","收到消息===>TestBean222222");
    }
}