package com.quduo.welfareshop.ui.friend.view;

import com.quduo.welfareshop.mvp.BaseView;
import com.quduo.welfareshop.ui.friend.entity.ChatMessageInfo;

import java.util.List;

/**
 * Author:scene
 * Time:2018/2/5 16:40
 * Description:聊天
 */
public interface IChatView extends BaseView {
    String getOtherUserId();

    void refreshComplete();

    void updateRecyclerView(List<ChatMessageInfo> list);

    String getOtherNickName();

    void moveToBottom(boolean isFirstEnter);

    void showLoadingDialog();

    void hideLoadingDialog();

    void followUserSuccess(int followId);

    void showMessage(String message);

    int getFromNearby();

    double getLongitude();

    double getLatitude();
}
