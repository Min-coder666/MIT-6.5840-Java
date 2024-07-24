package com.min.raft.common;

/**
 * leader发送给follower的心跳
 * @author wangmin
 * @date 2024/7/19 13:25
 */
public class AppendEntriesRequest {

    /**
     * leader's term
     */
    private int term;

    private Integer leaderId;

    // todo other fields


    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public Integer getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Integer leaderId) {
        this.leaderId = leaderId;
    }

    @Override
    public String toString() {
        return "term=" + term +
                ", leaderId=" + leaderId;
    }
}
