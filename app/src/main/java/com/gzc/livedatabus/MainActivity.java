package com.gzc.livedatabus;

import androidx.appcompat.app.AppCompatActivity;

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
        new MainActivityLiveDataObserver().observe(this,null);

    }

    public void sendMessage(View view) {
        LiveDataBus.getInstance().post("TestBean4",new TestBean4());
    }

//    @Observe(threadMode = ThreadMode.MAIN,key = "",sticky = false,append = false)
//    public void testBean1(TestBean1 testBean1){
//        Log.e("guanzhenchuang","收到消息");
//    }
//
//    @Observe(threadMode = ThreadMode.MAIN,sticky = true,append = true)
//    public void testBean2(TestBean2 testBean2){
//
//    }
//
//    @Observe(threadMode = ThreadMode.MAIN,sticky = true,append = true)
//    public void testBean3(TestBean3 testBean3){
//
//    }

    @Observe(threadMode = ThreadMode.MAIN,sticky = true,key = "TestBean4")
    public void testBean4(TestBean4 testBean4){
        Log.e("guanzhenchuang","收到消息");
    }
}