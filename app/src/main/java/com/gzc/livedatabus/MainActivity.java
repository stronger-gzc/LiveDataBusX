package com.gzc.livedatabus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gzc.apt_annotation.Observe;
import com.gzc.apt_annotation.ThreadMode;
import com.gzc.livedatabus.test.TestBean1;
import com.gzc.livedatabus.test.TestBean2;
import com.gzc.livedatabus.test.TestBean3;
import com.gzc.livedatabus.test.TestBean4;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Observe(threadMode = ThreadMode.MAIN,sticky = false,append = false,key = "")
    public void testBean1(TestBean1 testBean1){

    }
//
//    @Observe(threadMode = ThreadMode.MAIN,sticky = true,append = false,key = "")
//    public void testBean2(TestBean2 testBean2){
//
//    }
//
//    @Observe(threadMode = ThreadMode.MAIN,sticky = true,append = true,key = "")
//    public void testBean3(TestBean3 testBean3){
//
//    }
//
//    @Observe(threadMode = ThreadMode.MAIN,sticky = true,key = "TestBean3")
//    public void testBean3(TestBean4 testBean4){
//
//    }
}