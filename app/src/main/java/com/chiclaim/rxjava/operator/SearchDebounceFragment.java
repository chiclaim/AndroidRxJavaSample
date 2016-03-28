package com.chiclaim.rxjava.operator;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.chiclaim.rxjava.BaseFragment;
import com.chiclaim.rxjava.R;
import com.chiclaim.rxjava.api.ApiServiceFactory;
import com.chiclaim.rxjava.api.SearchApi;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chiclaim on 2016/03/24
 */
public class SearchDebounceFragment extends BaseFragment {

    private final SearchApi searchApi = ApiServiceFactory.createService(SearchApi.class);


    private Subscription subscription;


    private TextView tvContent;
    private EditText etKey;
    private TextView tvKey;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvContent = (TextView) view.findViewById(R.id.tv_content);
        etKey = (EditText) view.findViewById(R.id.et_key);
        tvKey = (TextView) view.findViewById(R.id.tv_key);


        //===========================@TODO
        //1,避免EditText每改变一次就请求一次.
        //2,避免频繁的请求,多个导致结果顺序错乱,最终的结果也就有问题.

        // 但是对于第二个问题,也不能彻底的解决,但是从一定程度上缓解了错乱的问题. 比如停止输入400毫秒后,
        // 那么肯定会开始请求Search接口, 但是用户又会输入新的关键字,
        // 这个时候上个请求还没有返回, 新的请求又去请求Search接口.
        // 这个时候有可能最后的一个请求返回, 第一个请求最后返回,导致搜索结果不是想要的.
        //===========================@TODO


        subscription = RxTextView.textChanges(etKey)
                .debounce(400, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())// 对etKey[EditText]的监听操作 需要在主线程操作
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        Log.d("RxJava", "filter is main thread : " + (Looper.getMainLooper() == Looper.myLooper()));
                        return charSequence.toString().trim().length() > 0;
                    }
                })
                .switchMap(new Func1<CharSequence, Observable<List<String>>>() {
                    @Override
                    public Observable<List<String>> call(CharSequence charSequence) {
                        Log.d("RxJava", getMainText("switchMap"));
                        return searchApi.search(charSequence.toString());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> strings) {
                        tvContent.setText("search result:\n\n");
                        tvContent.append(strings.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        tvContent.append("Error:" + throwable.getMessage());
                    }
                });

        //flatMap和switchMap的区别
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

}
