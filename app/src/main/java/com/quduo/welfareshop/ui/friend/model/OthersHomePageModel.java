package com.quduo.welfareshop.ui.friend.model;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.quduo.welfareshop.http.api.ApiUtil;
import com.quduo.welfareshop.http.base.LzyResponse;
import com.quduo.welfareshop.http.callback.JsonCallback;
import com.quduo.welfareshop.http.listener.HttpResultListener;
import com.quduo.welfareshop.ui.friend.entity.HomePageInfo;
import com.quduo.welfareshop.ui.welfare.model.ZanModel;

public class OthersHomePageModel extends ZanModel{
    public void getData(HttpParams params, final HttpResultListener<HomePageInfo> listener) {
        OkGo.<LzyResponse<HomePageInfo>>get(ApiUtil.API_PRE + ApiUtil.OTHERS_HOME_PAGE)
                .tag(ApiUtil.OTHERS_HOME_PAGE_TAG)
                .params(params)
                .execute(new JsonCallback<LzyResponse<HomePageInfo>>() {
                    @Override
                    public void onSuccess(Response<LzyResponse<HomePageInfo>> response) {
                        try {
                            listener.onSuccess(response.body().data);
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onFail("数据获取失败请重试");
                        }
                    }

                    @Override
                    public void onError(Response<LzyResponse<HomePageInfo>> response) {
                        super.onError(response);
                        try {
                            listener.onFail(response.getException().getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onFail("数据获取失败请重试");
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        listener.onFinish();
                    }
                });
    }
}
