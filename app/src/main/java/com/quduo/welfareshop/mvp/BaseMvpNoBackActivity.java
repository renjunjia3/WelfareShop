package com.quduo.welfareshop.mvp;

import android.os.Bundle;

import me.yokeyword.fragmentation.SupportActivity;

/**
 * Case By: 基类
 * package:
 * Author：scene on 2017/6/27 10:52
 */
public abstract class BaseMvpNoBackActivity<V, T extends BasePresenter<V>> extends SupportActivity {
    public T presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = initPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attach((V) this);
    }

    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        presenter.dettach();
        super.onDestroy();
    }


    // 实例化presenter
    public abstract T initPresenter();
}
