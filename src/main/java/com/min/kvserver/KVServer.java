package com.min.kvserver;

import com.min.common.GetArgs;
import com.min.common.GetReply;
import com.min.common.PutAppendArgs;
import com.min.common.PutAppendReply;
import com.min.rpc.provider.NettyRpcProvider;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangmin
 * @date 2024/7/17 12:40
 */
public class KVServer extends NettyRpcProvider {

    private final Map<String, String> store = new ConcurrentHashMap<>();

    public KVServer(int port) {
        super(port);
    }

    public PutAppendReply putAppend(PutAppendArgs args){
        String key = args.getKey();
        String val = args.getVal();
        PutAppendReply reply = new PutAppendReply();
        String ret = store.compute(key,(k,v)-> Objects.isNull(v)?val:v+val);
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
