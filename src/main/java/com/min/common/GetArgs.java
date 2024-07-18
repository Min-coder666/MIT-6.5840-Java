package com.min.common;

/**
 * @author wangmin
 * @date 2024/7/17 10:51
 */
public class GetArgs extends Args{

    String key;

    public GetArgs() {
    }

    public GetArgs(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
