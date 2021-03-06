package com.quduo.welfareshop.ui.mine.entity;

import com.quduo.welfareshop.base.BaseBean;

public class CheckPayResultInfo extends BaseBean {
    /**
     * status : true
     * type : score goods--购买商品 score--积分
     * coupon_cost : 38
     * score : 1801
     * diamond : 1042
     */

    private boolean status;
    private String type;
    private int coupon_cost;
    private int score;
    private int diamond;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCoupon_cost() {
        return coupon_cost;
    }

    public void setCoupon_cost(int coupon_cost) {
        this.coupon_cost = coupon_cost;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

}
