package com.min.kvserver;

import com.min.common.GetArgs;
import com.min.common.GetReply;
import com.min.common.PutAppendArgs;
import com.min.common.PutAppendReply;
import com.min.rpc.provider.NettyRpcProvider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangmin
 * @date 2024/7/17 12:40
 */
public class KVServer extends NettyRpcProvider {

    private final ConcurrentMap<String, String> store = new ConcurrentHashMap<>();

    public final AtomicInteger putAppendCnt = new AtomicInteger(0);
    public KVServer(int port) {
        super(port);
    }

    public PutAppendReply putAppend(PutAppendArgs args){
        putAppendCnt.incrementAndGet();
        Integer op = args.getOpType();
        String key = args.getKey();
        String val = args.getVal();
        PutAppendReply reply = new PutAppendReply();
        if (PutAppendArgs.OP_APPEND.equals(op)){
            String oldContent = store.getOrDefault(key,"");
            String newVal = oldContent+val;
            reply.setSuccess(true);
            reply.setVal(store.put(key,newVal));
        } else if (PutAppendArgs.OP_PUT.equals(op)) {
            reply.setSuccess(true);
            reply.setVal(store.put(key,val));
        }
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
