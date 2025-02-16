package com.lion.sequence;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FixLeader {
    private static final String ZK_ADDRESS = "localhost:2181";
    private static final String PARENT_NODE = "/fix";
    private static final String LEADER_NODE = "/fix/leader";
    private static final String SEQUENCE_NODE = "/fix/session";
    private ZooKeeper zooKeeper;
    private String nodeId;
    private int sequenceNumber = 1; // Start from 1

    public FixLeader(String nodeId) {
        this.nodeId = nodeId;
    }

    public void connect() throws IOException, KeeperException, InterruptedException {
        this.zooKeeper = new ZooKeeper(ZK_ADDRESS, 3000, event -> {
            if (event.getType() == Watcher.Event.EventType.NodeDeleted && event.getPath().equals(LEADER_NODE)) {
                System.out.println("Leader is down! Attempting to become leader...");
                try {
                    attemptLeadership();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Ensure parent node exists
        ensureParentNodeExists();

        // Try to become leader or watch the existing leader
        attemptLeadership();
    }

    private void ensureParentNodeExists() throws KeeperException, InterruptedException {
        if (zooKeeper.exists(PARENT_NODE, false) == null) {
            zooKeeper.create(PARENT_NODE, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("Created parent node: " + PARENT_NODE);
        }
    }

    public void attemptLeadership() throws KeeperException, InterruptedException {
        try {
            zooKeeper.create(LEADER_NODE, nodeId.getBytes(StandardCharsets.UTF_8),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println(nodeId + " is the leader!");
            startUpdatingSequenceNumber();
        } catch (KeeperException.NodeExistsException e) {
            System.out.println(nodeId + " is a follower. Leader exists.");
            watchLeader();
        }
    }

    private void watchLeader() throws KeeperException, InterruptedException {
        if (zooKeeper.exists(LEADER_NODE, true) != null) {
            System.out.println(nodeId + " is watching the leader.");
        } else {
            System.out.println("No leader found. Retrying leadership...");
            attemptLeadership();
        }
    }

    private void startUpdatingSequenceNumber() {
        new Thread(() -> {
            try {
                while (true) {
                    sequenceNumber++;
                    byte[] data = String.valueOf(sequenceNumber).getBytes(StandardCharsets.UTF_8);
                    if (zooKeeper.exists(SEQUENCE_NODE, false) != null) {
                        zooKeeper.setData(SEQUENCE_NODE, data, -1);
                    } else {
                        zooKeeper.create(SEQUENCE_NODE, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    }
                    System.out.println("Leader updated sequence: " + sequenceNumber);
                    Thread.sleep(3000); // Simulate FIX sequence updates
                }
            } catch (Exception e) {
                System.out.println("Leader lost! Stopping updates.");
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {
        String nodeId = args.length > 0 ? args[0] : "Leader-" + System.currentTimeMillis();
        FixLeader leader = new FixLeader(nodeId);
        leader.connect();
        Thread.sleep(Long.MAX_VALUE); // Keep process running
    }
}