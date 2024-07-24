package com.min.raft;

import com.min.common.LogUtil;
import com.min.raft.common.AppendEntriesReply;
import com.min.raft.common.AppendEntriesRequest;
import com.min.raft.common.VoteReply;
import com.min.raft.common.VoteRequest;
import com.min.rpc.client.NettyRpcClient;
import com.min.rpc.provider.NettyRpcProvider;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangmin
 * @date 2024/7/19 10:56
 */
public class RaftNode extends NettyRpcProvider{
    public static final String ROLE_LEADER = "leader";
    public static final String ROLE_CANDIDATE = "candidate";
    public static final String ROLE_FOLLOWER= "follower";

    private String role;

    /**
     * 当前任期
     */
    private int currentTerm;

    private Integer voteFor;

    private long latestHeartBeatTs = System.currentTimeMillis();
    // todo other fields

    /**
     * 端口 充当id
     */
    private final Integer port;

    private Map<Integer,NettyRpcClient> clients;

    private List<Integer> nodePorts;

    private ExecutorService clientExecutor;

    private Random random = new Random();


    public RaftNode(int serverPort,List<Integer> nodePorts) {
        super(serverPort);
        this.port = serverPort;
        clientExecutor = Executors.newFixedThreadPool(nodePorts.size()-1);
        role = ROLE_FOLLOWER;
        clients = new HashMap<>();
        this.nodePorts = nodePorts;
    }

    private void init(){
        this.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // do nothing
        }
        nodePorts.forEach(p->{
            if(!p.equals(port)){
                NettyRpcClient client = new NettyRpcClient();
                clients.put(p,client);
                clientExecutor.submit(()->{
                    client.run(p);
                });
            }
        });
        LogUtil.log(getNodeName(),"初始化成功");
    }

    public void runAsRaftNode(){
        init();
        while (true){
            if (ROLE_FOLLOWER.equals(role)){
                timeoutCheck();
            }else if(ROLE_CANDIDATE.equals(role)){
                startElection();
            }else if(ROLE_LEADER.equals(role)){
                sendHeartbeats();
/*                // 模拟节点失效，只会停止发心跳，netty接受请求线程仍然会处理请求
                if(random.nextDouble() < 0.005){
                    LogUtil.log(getNodeName(),"失效5s");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {

                    }
                }*/

                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        }
    }

    private int randomTimeout(){
        // 150-300 ms
        return random.nextInt(150)+150;
    }

    private void becomeCandidate(){
        this.latestHeartBeatTs = System.currentTimeMillis();
        this.role = ROLE_CANDIDATE;
        currentTerm++;
        voteFor = port;
        LogUtil.log(getNodeName(), "成为候选人，任期：" + currentTerm);
        startElection();

    }

    private void startElection() {
        AtomicInteger votes = new AtomicInteger(1);
        VoteRequest request = new VoteRequest();
        request.setTerm(currentTerm);
        request.setCandidateId(port);
        clients.forEach((key, client) -> {
            try {
                VoteReply reply = client.call("vote", new Object[]{request}, VoteReply.class, true);
                if(Objects.isNull(reply)) return;
                if (reply.isVoteGranted()) {
                    votes.incrementAndGet();
                }else if(reply.getTerm() > currentTerm){
                    becomeFollower();
                }
            } catch (Exception e) {
                // do noting
            }
        });
        if (ROLE_CANDIDATE.equals(role) && votes.get() > (1+clients.size())/2 ){
            becomeLeader();
        }else {
            //选举失败
            becomeFollower();
        }

    }

    private void becomeLeader() {
        this.latestHeartBeatTs = System.currentTimeMillis();
        role = ROLE_LEADER;
        LogUtil.log(getNodeName(), "成为领导者，任期：" + currentTerm);
        sendHeartbeats();
    }

    private void becomeFollower(){
        this.latestHeartBeatTs = System.currentTimeMillis();
        if(ROLE_FOLLOWER.equals(role)) return;
        role = ROLE_FOLLOWER;
        LogUtil.log(getNodeName(), "成为跟随者，任期：" + currentTerm);
        voteFor = null;

    }

    public AppendEntriesReply heartbeat(AppendEntriesRequest request){
//        LogUtil.log(getNodeName(),"收到心跳请求: ",request,"当前任期: ",currentTerm," 当前身份: ", role);
        AppendEntriesReply reply = new AppendEntriesReply();
        reply.setTerm(currentTerm);
        int leaderTerm = request.getTerm();
        if(leaderTerm >= currentTerm){
            nextTerm(leaderTerm);
            becomeFollower();
            reply.setSuccess(true);
        }
        return reply;
    }

    private void sendHeartbeats(){
        if(!ROLE_LEADER.equals(role)) return;
        AppendEntriesRequest request = new AppendEntriesRequest();
        request.setTerm(currentTerm);
        request.setLeaderId(port);
        clients.forEach((key, client) -> {
            try {
                AppendEntriesReply reply = client.call("heartbeat", new Object[]{request}, AppendEntriesReply.class, true);
                if (Objects.nonNull(reply) && reply.getTerm() > currentTerm) {
                    currentTerm = reply.getTerm();
                    becomeFollower();
                }
            } catch (Exception e) {

            }
        });
    }

    public VoteReply vote(VoteRequest request){
//        LogUtil.log(getNodeName(),"收到投票请求: ",request,"当前任期: ",currentTerm," 当前身份: ", role);
        VoteReply reply = new VoteReply();
        reply.setVoteGranted(false);
        reply.setTerm(currentTerm);
        if(Objects.isNull(request)) return null;
        if(request.getTerm() > currentTerm || (request.getTerm() == currentTerm && (voteFor == null || voteFor == request.getCandidateId()))){
            voteFor = request.getCandidateId();
            currentTerm = request.getTerm();
            becomeFollower();
            reply.setVoteGranted(true);
        }
//        LogUtil.log(getNodeName(),"响应投票请求: ",reply,"当前任期: ",currentTerm," 当前身份: ", role);
        return reply;
    }

    public String getNodeName(){
        return "RaftNode-"+port;
    }

    private void nextTerm(int newTerm){
        if(newTerm == currentTerm) return;
        this.currentTerm = newTerm;
        voteFor = null;
    }

    private void timeoutCheck() {
        long electionTimeout = randomTimeout();
        while (System.currentTimeMillis()-latestHeartBeatTs < electionTimeout){
            try {
                Thread.sleep(50);
            } catch (Exception e) {

            }
        }
        becomeCandidate();
    }
}
