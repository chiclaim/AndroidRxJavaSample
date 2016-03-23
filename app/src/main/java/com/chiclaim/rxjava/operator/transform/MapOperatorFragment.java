package com.chiclaim.rxjava.operator.transform;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chiclaim.rxjava.BaseFragment;
import com.chiclaim.rxjava.R;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/** Demonstrate map operator of RxJava <br/>
 * Created by chiclaim on 2016/03/23
 */
public class MapOperatorFragment extends BaseFragment {

    private TextView tvLogs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map_operator, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_operator).setOnClickListener(this);
        tvLogs = (TextView) view.findViewById(R.id.tv_logs);
        tvLogs.setText("Click Button to test 'map operator'");
        Button btn = (Button) view.findViewById(R.id.btn_operator);
        btn.setText("Observable Operator Map");

    }


    private void observableMap() {
        Observable<String> sentenceObservable = Observable.from(new String[]{"This", "is", "RxJava"});
        sentenceObservable.map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                printLog(tvLogs, "Transform Data ToLowerCase: ", s);
                return s.toLowerCase();
            }
        }).toList().map(new Func1<List<String>, List<String>>() {
            @Override
            public List<String> call(List<String> strings) {
                printLog(tvLogs, "Transform Data Reverse List: ", strings.toString());
                Collections.reverse(strings);
                return strings;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> s) {
                        printLog(tvLogs, "Consume Data ", s.toString());
                    }
                });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_operator:
                tvLogs.setText("");
                observableMap();
                break;
        }
    }
}
