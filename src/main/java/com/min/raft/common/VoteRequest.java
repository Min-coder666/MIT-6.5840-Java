package com.min.raft.common;

/**
 * @author wangmin
 * @date 2024/7/19 13:28
 */
public class VoteRequest {

    private int term;

    private Integer candidateId;

    // todo other fields


    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public Integer getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Integer candidateId) {
        this.candidateId = candidateId;
    }

    @Override
    public String toString() {
        return "term=" + term +
                ", candidateId=" + candidateId;
    }
}
