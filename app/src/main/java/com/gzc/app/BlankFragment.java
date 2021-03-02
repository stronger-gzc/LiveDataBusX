package com.gzc.app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gzc.livedatabusx.LiveDataBusX;
import com.livedatabusx.annotation.Observe;
import com.livedatabusx.annotation.ThreadMode;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment {

    private TextView dataView;

    private int index;

    public BlankFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static BlankFragment newInstance() {
        return new BlankFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        dataView = view.findViewById(R.id.data_view);
        dataView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveDataBusX.getInstance().post("MainActivity","这是BlankFragment发来的数据+"+index++);
            }
        });

        LiveDataBusX.getInstance().observe(this);
        return view;
    }

    @Observe(threadMode = ThreadMode.MAIN,key = "BlankFragment")
    public void getData(String data){
        dataView.setText(data);
    }

}