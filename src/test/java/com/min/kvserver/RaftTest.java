package com.min.kvserver;

import com.min.raft.RaftStarter;
import org.junit.Test;

/**
 * @author wangmin
 * @date 2024/7/22 17:07
 */
public class RaftTest {

    @Test
    public void raftLeaderTest(){
        RaftStarter.start();
    }
}
