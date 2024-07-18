package com.min.common;

import java.util.UUID;

/**
 * @author wangmin
 * @date 2024/7/18 15:35
 */
public class Args {
    // 回复类型
    public static final Integer ARGS_TYPE_REPORT = 1;
    // 操作类型
    public static final Integer ARGS_TYPE_MODIFY = 2;
    Integer tag = ARGS_TYPE_MODIFY;
    String requestId = UUID.randomUUID().toString().replaceAll("-","");

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
