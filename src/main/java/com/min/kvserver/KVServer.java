package com.min.kvserver;

import com.min.common.*;
import com.min.rpc.provider.NettyRpcProvider;
import io.netty.util.internal.StringUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangmin
 * @date 2024/7/17 12:40
 */
public class KVServer extends NettyRpcProvider {

    private final Map<String, String> store = new ConcurrentHashMap<>();

//    public final AtomicInteger putTotalCnt = new AtomicInteger();
//    public final AtomicInteger putComputeCnt = new AtomicInteger();
//
//    public final AtomicInteger putGetFromMap = new AtomicInteger();
//
//    public final AtomicInteger ackCnt = new AtomicInteger();
    // 记录请求id对应的请求值
    private final Map<String,String> requstMap = new ConcurrentHashMap<>();

    public KVServer(int port) {
        super(port);
    }

    public PutAppendReply putAppend(PutAppendArgs args){
        if(Objects.isNull(args) || StringUtil.isNullOrEmpty(args.getRequestId())){
            return null;
        }
        if (PutAppendArgs.ARGS_TYPE_REPORT.equals(args.getTag())){
            requstMap.remove(args.getRequestId());
            return null;
        }
        PutAppendReply reply = new PutAppendReply();

        String key = args.getKey();
        if(requstMap.containsKey(args.getRequestId())){
            reply.setVal(requstMap.get(args.getRequestId()));
            reply.setSuccess(true);
            return reply;
        }
        String val = args.getVal();
        Integer opType = args.getOpType();

        String ret;
        if(PutAppendArgs.OP_APPEND.equals(opType))
            ret = store.compute(key,(k,v)-> Objects.isNull(v)?val:v+val);
        else{
            store.put(key,val);
            ret = val;
        }
        requstMap.put(args.getRequestId(),ret);
        reply.setSuccess(true);
        reply.setVal(ret);

        return reply;
    }


    public GetReply get(GetArgs args){
        GetReply reply = new GetReply();
        String key = args.getKey();
        String value = store.get(key);

        if (value != null) {
            reply.setVal(value);
            reply.setSuccess(true);
        } else {
            reply.setSuccess(false);
        }

        return reply;
    }
}
