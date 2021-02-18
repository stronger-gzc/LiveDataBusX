package com.gzc.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

//import com.gzc.livedatabusx.LiveDataBusX;
import com.gzc.livedatabusx.LiveDataBusX;
import com.livedatabusx.annotation.Observe;
import com.livedatabusx.annotation.ThreadMode;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LiveDataBusX.getInstance().observe(this);

        LiveDataBusX.getInstance()
                .post("test1",new Test1Bean());

        LiveDataBusX.getInstance()
                .post("test1","动态key",new Test1Bean());
    }

    /**
     * （1）接收事件的方法在主线程
     * （2）非粘性
     * （3）动态key
     * @param test1Bean
     */
    @Observe(threadMode = ThreadMode.MAIN,sticky = false,append = true,key = "test1")
    public void test1(Test1Bean test1Bean){

    }

//    @Observe(threadMode = ThreadMode.MAIN,key = "test1")
//    public void test2(Test1Bean test1Bean){
//
//    }
}
