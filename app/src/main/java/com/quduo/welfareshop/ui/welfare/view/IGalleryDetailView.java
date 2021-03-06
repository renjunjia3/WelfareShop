package com.quduo.welfareshop.ui.welfare.view;

import com.quduo.welfareshop.mvp.BaseView;
import com.quduo.welfareshop.ui.welfare.entity.GalleryDetailResultInfo;

/**
 * Author:scene
 * Time:2018/3/5 16:54
 * Description:图库详情
 */

public interface IGalleryDetailView extends BaseView {
    void showMessage(String msg);

    void refreshFinish();

    void bindData(GalleryDetailResultInfo data);

    int getFollowId();

    int getGalleryId();

    void showLoadingDialog();

    void showHasFollow(int followId);

    void showNoFollow();

    void hideLoadingDialog();

    int getDataId();

    void unlockSuccess(int score);
}
