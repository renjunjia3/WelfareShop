package com.quduo.welfareshop.ui.friend.presenter;

import com.quduo.welfareshop.http.listener.HttpResultListener;
import com.quduo.welfareshop.mvp.BasePresenter;
import com.quduo.welfareshop.ui.friend.entity.FollowUserInfo;
import com.quduo.welfareshop.ui.friend.model.FollowModel;
import com.quduo.welfareshop.ui.friend.view.IFollowView;
import com.quduo.welfareshop.ui.welfare.entity.UnlockResultInfo;

import java.util.List;

/**
 * Author:scene
 * Time:2018/2/1 17:08
 * Description:关注
 */

public class FollowPresenter extends BasePresenter<IFollowView> {
    private FollowModel model;

    public FollowPresenter(IFollowView view) {
        super(view);
        this.model = new FollowModel();
    }

    public void getData(final boolean isFirst) {
        try {
            if (isFirst) {
                mView.showLoadingPage();
            }
            model.getData(new HttpResultListener<List<FollowUserInfo>>() {
                @Override
                public void onSuccess(List<FollowUserInfo> data) {
                    try {
                        mView.bindData(data);
                        if (isFirst) {
                            mView.showContentPage();
                        } else {
                            mView.refreshFinish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail(String message) {
                    try {
                        mView.showMessage(message);
                        if (isFirst) {
                            mView.showErrorPage();
                        } else {
                            mView.refreshFinish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFinish() {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unlockChat() {
        try {
            mView.showLoadingDialog();
            model.unlockChat(new HttpResultListener<UnlockResultInfo>() {
                @Override
                public void onSuccess(UnlockResultInfo data) {
                    try {
                        mView.showAlert("解锁成功");
                        mView.unlockChatSuccess(data.getScore());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail(String message) {
                    try {
                        mView.showAlert(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFinish() {
                    try {
                        mView.hideLoadingDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
