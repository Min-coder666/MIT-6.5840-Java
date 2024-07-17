package com.min.common;

/**
 * @author wangmin
 * @date 2024/7/17 10:53
 */
public class PutAppendArgs {
    public static final Integer OP_APPEND = 1;
    public static final Integer OP_PUT = 2;

    String key;
    String val;

    Integer opType = OP_APPEND;

    public PutAppendArgs() {
    }

    public PutAppendArgs(String key, String val) {
        this.key = key;
        this.val = val;
    }

    public PutAppendArgs(String key, String val, Integer opType) {
        this.key = key;
        this.val = val;
        if(OP_APPEND == opType || OP_PUT == opType)
            this.opType = opType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public Integer getOpType() {
        return opType;
    }

    public void setOpType(Integer opType) {
        this.opType = opType;
    }
}
