package com.min.kvserver;

import com.min.common.*;
import com.min.rpc.consumer.NettyRpcClient;

/**
 * @author wangmin
 * @date 2024/7/17 12:39
 */
public class KVClient extends NettyRpcClient {

    public KVClient(int serverPort) {
        run(serverPort);
    }

    public String put(String key, String val){
        return putAppend(key,val,PutAppendArgs.OP_PUT);
    }

    public String append(String key, String val){
        return putAppend(key,val,PutAppendArgs.OP_APPEND);
    }

    private String putAppend(String key,String val, Integer opType){
        PutAppendArgs args = new PutAppendArgs(key, val, opType);
        try {
            PutAppendReply reply = call("putAppend",new Object[]{args}, PutAppendReply.class);
            return reply.getVal();
        } catch (Exception e){
            LogUtil.log(PutAppendArgs.OP_APPEND == opType?"append":"put","操作失败: ");
        }
        return null;
    }

    public String get(String key){
        GetArgs args = new GetArgs(key);
        try {
            GetReply reply = call("get",new Object[]{args},GetReply.class);
            return reply.getVal();
        } catch (Exception e){
            LogUtil.log("get操作失败: ",e.getMessage());
        }
        return null;
    }

}
