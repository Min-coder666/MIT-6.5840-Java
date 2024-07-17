package com.min.kvserver;

import com.min.common.GetArgs;
import com.min.common.JsonUtil;
import com.min.common.PutAppendArgs;
import com.min.rpc.RpcRequest;
import org.junit.Test;

/**
 * @author wangmin
 * @date 2024/7/17 14:55
 */
public class JsonTest {

    @Test
    public void testJson(){
        GetArgs args1 = new GetArgs("hello");
        GetArgs args2 = new GetArgs("hell1o");
        PutAppendArgs args3 = new PutAppendArgs("hello","world");
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setParameters(new Object[]{args1,args2,args3});
        String jsonStr = JsonUtil.parseJsonString(rpcRequest);
        System.out.println(jsonStr);
        RpcRequest parseJson = JsonUtil.parseObject(jsonStr, RpcRequest.class);
        System.out.println(parseJson);
    }
}
