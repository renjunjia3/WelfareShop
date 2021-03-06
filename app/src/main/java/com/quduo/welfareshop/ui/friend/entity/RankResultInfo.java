package com.quduo.welfareshop.ui.friend.entity;

import com.quduo.welfareshop.base.BaseBean;

import java.util.List;

public class RankResultInfo extends BaseBean {
    private List<OtherSimpleUserInfo> data;
    private int current_page;
    private int last_page;

    public List<OtherSimpleUserInfo> getData() {
        return data;
    }

    public void setData(List<OtherSimpleUserInfo> data) {
        this.data = data;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

}
