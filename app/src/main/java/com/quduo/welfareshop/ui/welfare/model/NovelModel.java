package com.quduo.welfareshop.ui.welfare.model;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.quduo.welfareshop.http.api.ApiUtil;
import com.quduo.welfareshop.http.base.LzyResponse;
import com.quduo.welfareshop.http.callback.JsonCallback;
import com.quduo.welfareshop.http.listener.HttpResultListener;
import com.quduo.welfareshop.ui.welfare.entity.NovelResultInfo;

/**
 * Author:scene
 * Time:2018/2/24 11:13
 * Description:小爽文
 */

public class NovelModel {

    public void getNovelListData(final HttpResultListener<NovelResultInfo> listener) {
        OkGo.<LzyResponse<NovelResultInfo>>get(ApiUtil.API_PRE + ApiUtil.NOVEL_LIST)
                .tag(ApiUtil.NOVEL_LIST_TAG)
                .execute(new JsonCallback<LzyResponse<NovelResultInfo>>() {
                    @Override
                    public void onSuccess(Response<LzyResponse<NovelResultInfo>> response) {
                        try {
                            listener.onSuccess(response.body().data);
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onFail("数据获取失败，请重试");
                        }
                    }

                    @Override
                    public void onError(Response<LzyResponse<NovelResultInfo>> response) {
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
                        try {
                            listener.onFinish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
    }
}
