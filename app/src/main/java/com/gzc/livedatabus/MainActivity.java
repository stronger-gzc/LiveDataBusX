package com.gzc.livedatabus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gzc.livedatabus.test.TestBean1;
import com.gzc.livedatabus.test.TestBean2;
import com.gzc.livedatabus.test.TestBean3;
import com.gzc.livedatabus.test.TestBean4;
import com.livedatabus.annotion.Observe;
import com.livedatabus.annotion.ThreadMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LiveDataBus.getInstance()
                .observe(this,"MainActivity");

    }

    public void sendMessage1(View view) {
        LiveDataBus.getInstance().post("TestBean1",new TestBean1());
    }
    public void sendMessage2(View view) {
        LiveDataBus.getInstance().post("TestBean2",new TestBean2());
    }
    public void sendMessage3(View view) {
        LiveDataBus.getInstance().post("TestBean3","MainActivity",new TestBean3());
    }
    public void sendMessage4(View view) {
        LiveDataBus.getInstance().post("TestBean4","MainActivity",new TestBean4());
    }

    @Observe(threadMode = ThreadMode.MAIN,key = "TestBean1")
    public void testBean1(TestBean1 testBean1){
        Log.e("guanzhenchuang","收到消息===>TestBean"+"   当前线程====>"+Thread.currentThread().getName());
    }

    @Observe(threadMode = ThreadMode.MAIN,key="TestBean2",sticky = true)
    public void testBean2(TestBean2 testBean2){
        Log.e("guanzhenchuang","收到消息===>TestBean222222");
    }

    @Observe(threadMode = ThreadMode.ASYNC,key="TestBean3",append = true)
    public void testBean3(TestBean3 testBean3){
        Log.e("guanzhenchuang","收到消息===>TestBean3   当前线程====>"+Thread.currentThread().getName());

    }

    @Observe(threadMode = ThreadMode.ASYNC,key = "TestBean4",sticky = true,append = true)
    public void testBean4(TestBean4 testBean4){
        Log.e("guanzhenchuang","收到消息===>TestBean4   当前线程====>"+Thread.currentThread().getName());

    }

    public void jump(View view) {
        startActivity(new Intent(this,SecodeActivity.class));
    }
}