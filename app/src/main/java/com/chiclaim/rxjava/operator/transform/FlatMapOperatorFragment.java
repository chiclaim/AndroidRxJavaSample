package com.chiclaim.rxjava.operator.transform;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chiclaim.rxjava.BaseFragment;
import com.chiclaim.rxjava.R;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chiclaim on 2016/03/23
 */
public class FlatMapOperatorFragment extends BaseFragment {

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
    }

    private Observable<String> processUrlsIpByOneMap() {
        return Observable.just(
                "http://www.baidu.com/",
                "http://www.google.com/",
                "https://www.bing.com/")
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return getIp(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private Observable<String> processUrlIpByTwoMap() {
        return Observable.just(
                "http://www.baidu.com/",
                "http://www.google.com/",
                "https://www.bing.com/")
                .toList()
                .flatMap(new Func1<List<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<String> s) {
                        return Observable.from(s);
                    }
                })
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return getIp(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void returnIpByList() {
        processUrlIpByTwoMap()
                .toList() //to list
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> s) {
                        printLog(tvLogs, "Consume Data <- ", s.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        printErrorLog(tvLogs, "throwable call()", throwable.getMessage());
                    }
                });
    }

    private void returnIpOneByOne() {
        processUrlIpByTwoMap().subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                printLog(tvLogs, "Consume Data <- ", s);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                printErrorLog(tvLogs, "throwable call()", throwable.getMessage());
            }
        });
    }


    /**
     * 需求:获取urls的ip,返回所有urls的ips或者单个返回ip
     */
    private void observableFlatMap() {
        //==============把ip作为list返回
//        returnIpByList();
        //===============单个的返回
        returnIpOneByOne();
    }


    private String getIPByUrl(String str) throws MalformedURLException, UnknownHostException {
        URL urls = new URL(str);
        String host = urls.getHost();
        String address = InetAddress.getByName(host).toString();
        int b = address.indexOf("/");
        return address.substring(b + 1);

    }


    private Observable<String> getIp(final String url) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {

                try {
                    String ip = getIPByUrl(url);
                    subscriber.onNext(ip);
                    printLog(tvLogs, "Emit Data -> ", ip);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        });
        //.subscribeOn(Schedulers.io()) 注意该方法在这里调用和放在使用该Observable的地方调 产生不同的影响
        //把注释去掉会使用不同的线程去执行,放在放在使用该Observable的地方调会共用一个线程去执行
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_operator:
                tvLogs.setText("");
                observableFlatMap();
                break;
        }
    }
}
