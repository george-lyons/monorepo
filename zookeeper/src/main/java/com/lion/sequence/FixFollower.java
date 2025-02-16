package com.lion.sequence;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FixFollower {
    private static final String ZK_ADDRESS = "localhost:2181";
    private static final String LEADER_NODE = "/fix/leader";
    private static final String SEQUENCE_NODE = "/fix/session";
    private ZooKeeper zooKeeper;
    private String nodeId;

    public FixFollower(String nodeId) {
        this.nodeId = nodeId;
    }

    public void connect() throws IOException, KeeperException, InterruptedException {
        this.zooKeeper = new ZooKeeper(ZK_ADDRESS, 3000, event -> {
            if (event.getType() == Watcher.Event.EventType.NodeDeleted && event.getPath().equals(LEADER_NODE)) {
                System.out.println("Leader is down! Checking sequence number...");
                try {
                    recoverSequenceNumber();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Ensure follower watches leader node
        watchLeader();
    }

    public void watchLeader() throws KeeperException, InterruptedException {
        if (zooKeeper.exists(LEADER_NODE, true) != null) {
            System.out.println(nodeId + " is watching the leader.");
        } else {
            System.out.println("No leader found. Attempting to become leader...");
            attemptLeadership();
        }
    }

    public void recoverSequenceNumber() throws KeeperException, InterruptedException {
        byte[] data = zooKeeper.getData(SEQUENCE_NODE, false, null);
        int lastSequence = Integer.parseInt(new String(data));
        System.out.println(nodeId + " recovered sequence number: " + lastSequence);
        System.out.println(nodeId + " taking over as leader...");
        attemptLeadership();
    }

    public void attemptLeadership() throws KeeperException, InterruptedException {
        try {
            zooKeeper.create(LEADER_NODE, nodeId.getBytes(StandardCharsets.UTF_8),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println(nodeId + " is now the new leader!");
        } catch (KeeperException.NodeExistsException e) {
            System.out.println(nodeId + " is still a follower. Re-watching leader.");
            watchLeader(); // Re-watch the leader
        }
    }

    public static void main(String[] args) throws Exception {
        String nodeId = args.length > 0 ? args[0] : "Follower-1";
        FixFollower follower = new FixFollower(nodeId);
        follower.connect();
        Thread.sleep(Long.MAX_VALUE); // Keep process running
    }
}