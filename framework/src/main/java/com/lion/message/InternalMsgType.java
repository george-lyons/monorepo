package com.lion.message;

import org.agrona.collections.Int2ObjectHashMap;

public enum InternalMsgType implements IntIdentifier {
    TOB_MARKET_DATA(1);

    private final int id;

    InternalMsgType(int id) {
        this.id = id;
    }

    private static final Int2ObjectHashMap<InternalMsgType> int2ObjectHashMap = new Int2ObjectHashMap<>();

    static {
        for (InternalMsgType in : InternalMsgType.values()) {
            int2ObjectHashMap.put(in.id, in);
        }
    }

    @Override
    public int getId() {
        return id   ;
    }

    public static InternalMsgType fromId(int id) {
        if(int2ObjectHashMap.get(id) != null) {
            return int2ObjectHashMap.get(id);
        } else {
            return null;
        }
    }
}
