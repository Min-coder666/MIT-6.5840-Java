package com.min.raft.common;

/**
 * @author wangmin
 * @date 2024/7/19 13:29
 */
public class VoteReply {

    private int term;

    private boolean voteGranted;

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public boolean isVoteGranted() {
        return voteGranted;
    }

    public void setVoteGranted(boolean voteGranted) {
        this.voteGranted = voteGranted;
    }

    @Override
    public String toString() {
        return "term=" + term +
                ", voteGranted=" + voteGranted;
    }
}
