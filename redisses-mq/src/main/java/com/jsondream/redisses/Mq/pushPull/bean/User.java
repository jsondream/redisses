package com.jsondream.redisses.Mq.pushPull.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 16/7/16
 */
public class User  implements Serializable {

    private static final long serialVersionUID = 4271535158365485855L;
    // 主键
    private String id;
    // 用户名
    private String userName;
    // 密码
    private String pwd;
    // 注册时间
    private java.util.Date createTime;
    // 登陆次数
    private Integer loginCount;
    // 最后登录时间
    private java.util.Date lastLoginTime;
    // 状态1 正常 2禁用
    private Integer state;
    // 第三方
    private String thirdPart;
    // ios Token
    private String deviceToken;
    // 第三方账号Token
    private String accessToken;
    private String recommendRank;
    // 电话号码
    private String phone;

    /**
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the userName
     */
    @JSONField(serialize = false)
    public String getUserName() {
        return this.userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the pwd
     */
    @JSONField(serialize = false)
    public String getPwd() {
        return this.pwd;
    }

    /**
     * @param pwd the pwd to set
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * @return the createTime
     */
    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the loginCount
     */
    public Integer getLoginCount() {
        return this.loginCount;
    }

    /**
     * @param loginCount the loginCount to set
     */
    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    /**
     * @return the lastLoginTime
     */
    public java.util.Date getLastLoginTime() {
        return this.lastLoginTime;
    }

    /**
     * @param lastLoginTime the lastLoginTime to set
     */
    public void setLastLoginTime(java.util.Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    /**
     * @return the state
     */
    public Integer getState() {
        return this.state;
    }

    /**
     * @param state the state to set
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * @return the thirdPart
     */
    public String getThirdPart() {
        return this.thirdPart;
    }

    /**
     * @param thirdPart the thirdPart to set
     */
    public void setThirdPart(String thirdPart) {
        this.thirdPart = thirdPart;
    }



    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRecommendRank() {
        return recommendRank;
    }

    public void setRecommendRank(String recommendRank) {
        this.recommendRank = recommendRank;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

