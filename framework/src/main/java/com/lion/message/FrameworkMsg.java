package com.lion.message;

import org.agrona.collections.Int2ObjectHashMap;

public enum FrameworkMsg implements IntIdentifier {
    TOB_MARKET_DATA(1);

    private final int id;

    FrameworkMsg(int id) {
        this.id = id;
    }

    private static final Int2ObjectHashMap<FrameworkMsg> int2ObjectHashMap = new Int2ObjectHashMap<>();

    static {
        for (FrameworkMsg in : FrameworkMsg.values()) {
            int2ObjectHashMap.put(in.id, in);
        }
    }

    @Override
    public int getId() {
        return id   ;
    }

    public static FrameworkMsg fromId(int id) {
        if(int2ObjectHashMap.get(id) != null) {
            return int2ObjectHashMap.get(id);
        } else {
            return null;
        }
    }
}
