package com.lion.example;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LeaderClient {
    private static final String ZK_ADDRESS = "localhost:2181";
    private static final String LEADER_NODE = "/leader";
    private ZooKeeper zooKeeper;

    public void connectToZooKeeper() throws IOException {
        this.zooKeeper = new ZooKeeper(ZK_ADDRESS, 3000, event -> {
            if (event.getType() == Watcher.Event.EventType.NodeDeleted) {
                System.out.println("Leader changed. Need to update.");
            }
        });
    }

    public String getLeader() throws KeeperException, InterruptedException {
        byte[] leaderData = zooKeeper.getData(LEADER_NODE, false, null);
        return new String(leaderData, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        LeaderClient client = new LeaderClient();
        client.connectToZooKeeper();

        while (true) {
            try {
                String leader = client.getLeader();
                System.out.println("Current Leader: " + leader);
            } catch (KeeperException.NoNodeException e) {
                System.out.println("No leader found. Retrying...");
            }
            Thread.sleep(5000); // Polling interval
        }
    }
}