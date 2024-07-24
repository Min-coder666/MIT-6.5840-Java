package com.min.raft.common;

/**
 * @author wangmin
 * @date 2024/7/19 13:27
 */
public class AppendEntriesReply {

    private int term;

    private boolean success;

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
