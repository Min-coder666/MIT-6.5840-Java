package com.min.common;

/**
 * @author wangmin
 * @date 2024/7/17 10:53
 */
public class PutAppendReply implements Reply{

    String val;

    boolean success;

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
