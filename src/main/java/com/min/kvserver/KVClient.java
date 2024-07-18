package com.min.kvserver;

import com.min.common.*;
import com.min.rpc.client.NettyRpcClient;

import java.util.Objects;

/**
 * @author wangmin
 * @date 2024/7/17 12:39
 */
public class KVClient extends NettyRpcClient {

    private final int retryCnt = 10;

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
        PutAppendReply reply = retryCall("putAppend", args, PutAppendReply.class,true);
        if(Objects.isNull(reply) || !reply.isSuccess()){
            LogUtil.log(PutAppendArgs.OP_APPEND.equals(opType)?"append":"put","操作失败");
            return null;
        }else {
            return reply.getVal();
        }
    }

    public String get(String key){
        GetArgs args = new GetArgs(key);
        GetReply reply = retryCall("get",args,GetReply.class,false);
        return reply == null? null:reply.getVal();
    }

    private <T extends Reply> T retryCall(String methodName, Args args,Class<T> clazz,boolean isAck){
        for (int i = 0; i < retryCnt; i++) {
            if(i>=1)
                LogUtil.log(methodName,"重试了",i,"次, ",args);
            try{
                T reply = call(methodName,new Object[]{args},clazz,true);
                if(reply.isSuccess()){
                    if (isAck) {
                        args.setTag(Args.ARGS_TYPE_REPORT);
                        ackCall(methodName, args);
                    }
                    return reply;
                }
            }catch (Exception e){
                // retry
            }
        }
        return null;
    }

    private void ackCall(String methodName, Args args){
        try {
            call(methodName,new Object[]{args},Args.class,false);
        } catch (Exception e) {
            // 只回复一次
            e.printStackTrace();
        }
    }

}
