package com.quduo.welfareshop.ui.friend.model;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.quduo.welfareshop.http.api.ApiUtil;
import com.quduo.welfareshop.http.base.LzyResponse;
import com.quduo.welfareshop.http.callback.JsonCallback;
import com.quduo.welfareshop.http.listener.HttpResultListener;
import com.quduo.welfareshop.ui.friend.entity.RankResultInfo;
import com.quduo.welfareshop.ui.welfare.entity.FollowSuccessInfo;

/**
 * Author:scene
 * Time:2018/2/1 17:04
 * Description:人气榜
 */

public class RankModel {

    public void getData(HttpParams params,final HttpResultListener<RankResultInfo> listener) {
        OkGo.<LzyResponse<RankResultInfo>>get(ApiUtil.API_PRE + ApiUtil.RANK_LIST)
                .tag(ApiUtil.RANK_LIST_TAG)
                .params(params)
                .execute(new JsonCallback<LzyResponse<RankResultInfo>>() {
                    @Override
                    public void onSuccess(Response<LzyResponse<RankResultInfo>> response) {
                        try {
                            listener.onSuccess(response.body().data);
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onFail("数据获取失败请重试");
                        }
                    }

                    @Override
                    public void onError(Response<LzyResponse<RankResultInfo>> response) {
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

    public void followUser(HttpParams params, final HttpResultListener<FollowSuccessInfo> listener) {
        OkGo.<LzyResponse<FollowSuccessInfo>>get(ApiUtil.API_PRE + ApiUtil.FOLLOW_USER)
                .tag(ApiUtil.FOLLOW_USER_TAG)
                .params(params)
                .execute(new JsonCallback<LzyResponse<FollowSuccessInfo>>() {
                    @Override
                    public void onSuccess(Response<LzyResponse<FollowSuccessInfo>> response) {
                        try {
                            listener.onSuccess(response.body().data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<LzyResponse<FollowSuccessInfo>> response) {
                        super.onError(response);
                        try {
                            listener.onFail(response.getException().getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        listener.onFinish();
                    }
                });
    }

    public void cancelFollowUser(HttpParams params, final HttpResultListener<String> listener) {
        OkGo.<LzyResponse<String>>get(ApiUtil.API_PRE + ApiUtil.CENCEL_FOLLOW_USER)
                .tag(ApiUtil.CENCEL_FOLLOW_USER_TAG)
                .params(params)
                .execute(new JsonCallback<LzyResponse<String>>() {
                    @Override
                    public void onSuccess(Response<LzyResponse<String>> response) {
                        try {
                            listener.onSuccess(response.body().data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<LzyResponse<String>> response) {
                        super.onError(response);
                        try {
                            listener.onFail(response.getException().getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
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
