package com.gzc.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;

import com.gzc.baselibrary.SecondActivity;
import com.gzc.baselibrary.Test1Bean;
import com.gzc.baselibrary.Test2Bean;
import com.gzc.baselibrary.Test3Bean;
import com.gzc.livedatabusx.LiveDataBusX;
import com.livedatabusx.annotation.Observe;
import com.livedatabusx.annotation.ThreadMode;


public class MainActivity extends AppCompatActivity {
    private TextView dataView;
    private int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataView = (TextView) findViewById(R.id.data_view);

        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout,BlankFragment.newInstance());
        transaction.commit();

        LiveDataBusX.getInstance().observe(this);
    }

    @Observe(threadMode = ThreadMode.MAIN,key = "MainActivity")
    public void getData(String data){
        dataView.setText(data);
    }

    @Observe(threadMode = ThreadMode.MAIN,key = "TestBean3")
    public void getData2(Test3Bean test3Bean){
        Log.e("guanzhenchuang","收到Second的数据");
    }

    /**
     * 发送数据到BlankFragment
     * @param view
     */
    public void sendDataToFragment(View view) {
        LiveDataBusX.getInstance().post("BlankFragment","MainActivity发来了数据！+"+index++);
    }

    /**
     * 跳转到SecondActivity
     * @param view
     */
    public void jumpToSecond(View view) {
        LiveDataBusX.getInstance().post("TestBean1",new Test1Bean());
        LiveDataBusX.getInstance().post("TestBean2",new Test2Bean());
        startActivity(new Intent(this,SecondActivity.class));
    }
}
