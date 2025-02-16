package com.lion.example;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LeaderElectionFollower implements Watcher {
    private static final String ZK_ADDRESS = "localhost:2181";
    private static final String LEADER_NODE = "/leader";
    private ZooKeeper zooKeeper;
    private String nodeId;

    public LeaderElectionFollower(String nodeId) {
        this.nodeId = nodeId;
    }

    public void connectToZooKeeper() throws IOException {
        this.zooKeeper = new ZooKeeper(ZK_ADDRESS, 3000, this);
    }

    public void attemptLeadership() throws KeeperException, InterruptedException {
        try {
            // Try to become the leader
            zooKeeper.create(LEADER_NODE, nodeId.getBytes(StandardCharsets.UTF_8),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println(nodeId + " is the leader!");
        } catch (KeeperException.NodeExistsException e) {
            System.out.println(nodeId + " is a follower. Leader exists.");
            watchLeader();
        }
    }

    public void watchLeader() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(LEADER_NODE, event -> {
            if (event.getType() == Event.EventType.NodeDeleted) {
                System.out.println("Leader is down! Re-electing...");
                try {
                    attemptLeadership();
                } catch (KeeperException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });
        if (stat == null) {
            attemptLeadership();
        }
    }

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        String nodeId = args.length > 0 ? args[0] : "Node-" + System.currentTimeMillis();
        LeaderElectionFollower leaderElection = new LeaderElectionFollower(nodeId);
        leaderElection.connectToZooKeeper();
        leaderElection.attemptLeadership();
        Thread.sleep(Long.MAX_VALUE); // Keep process running
    }

    @Override
    public void process(WatchedEvent event) {
        // Auto-reconnect if session expires
        if (event.getState() == Event.KeeperState.Expired) {
            try {
                connectToZooKeeper();
                attemptLeadership();
            } catch (IOException | KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}