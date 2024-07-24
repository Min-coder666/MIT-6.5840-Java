package com.min.raft;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wangmin
 * @date 2024/7/22 16:56
 */
public class RaftStarter {
    private static final String NODE_PATH = "src/main/resources/node.txt";
    private static int nodeNum;

    private static List<Integer> ports;

    private static Map<Integer,RaftNode> raftNodeMap;
    private static ExecutorService nodeThreadPool;

    static {
        ports =  readFileAsIntList(NODE_PATH);
        nodeNum = ports.size();
        if(nodeNum%2 == 0){
            throw new RuntimeException("启动失败，节点数不为奇数");
        }
        nodeThreadPool = Executors.newFixedThreadPool(nodeNum);
    }

    public static void start(){
        raftNodeMap = new HashMap<>();
        ports.forEach((port)->{
            RaftNode node = new RaftNode(port,ports);
            raftNodeMap.put(port,node);
            nodeThreadPool.submit(node::runAsRaftNode);
        });
    }


    private static List<Integer> readFileAsIntList(String pathname){
        List<String> strs = readFile(pathname);
        List<Integer> content = new ArrayList<>();
        strs.forEach((str)-> content.add(Integer.parseInt(str)));
        return content;
    }
    private static List<String> readFile(String pathname) {
        List<String> result = new ArrayList<>();
        try (FileReader reader = new FileReader(pathname);
             BufferedReader br = new BufferedReader(reader);
        ) {
            String line = null;
            while((line = br.readLine())!= null){
                // 跳过注释
                if(!line.startsWith("#")){
                    result.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
