package com.lion.message;

import org.agrona.collections.Int2ObjectHashMap;

public enum GlobalMsgType implements IntIdentifier {
    TOB_MARKET_DATA(1),
    QUOTE_STACK_EVENT_MARKET_DATA(2);

    private final int id;

    GlobalMsgType(int id) {
        this.id = id;
    }

    private static final Int2ObjectHashMap<GlobalMsgType> int2ObjectHashMap = new Int2ObjectHashMap<>();

    static {
        for (GlobalMsgType in : GlobalMsgType.values()) {
            int2ObjectHashMap.put(in.id, in);
        }
    }

    @Override
    public int getId() {
        return id   ;
    }

    public static GlobalMsgType fromId(int id) {
        if(int2ObjectHashMap.get(id) != null) {
            return int2ObjectHashMap.get(id);
        } else {
            return null;
        }
    }
}
