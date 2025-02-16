package com.lion.message;

import org.agrona.collections.Int2ObjectHashMap;

public enum MsgType implements IntIdentifier {
    ORDER(1);

    private final int id;

    MsgType(int id) {
        this.id = id;
    }

    private static final Int2ObjectHashMap<MsgType> int2ObjectHashMap = new Int2ObjectHashMap<>();

    static {
        for (MsgType in : MsgType.values()) {
            int2ObjectHashMap.put(in.id, in);
        }
    }

    @Override
    public int getId() {
        return id   ;
    }

    public static MsgType fromId(int id) {
        if(int2ObjectHashMap.get(id) != null) {
            return int2ObjectHashMap.get(id);
        } else {
            return null;
        }
    }
}
