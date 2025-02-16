package com.msg;

import com.lion.message.IntIdentifier;
import org.agrona.collections.Int2ObjectHashMap;

public enum MdMsgType implements IntIdentifier {
    MARKET_DATA_TOB(1),
    MARKET_DATA_DELTA(2),
    MARKET_DATA_SNAPSHOT(3);

    private final int id;

    MdMsgType(int id) {
        this.id = id;
    }

    private static final Int2ObjectHashMap<MdMsgType> int2ObjectHashMap = new Int2ObjectHashMap<>();

    static {
        for (MdMsgType in : MdMsgType.values()) {
            int2ObjectHashMap.put(in.id, in);
        }
    }

    @Override
    public int getId() {
        return id ;
    }

    public static MdMsgType fromId(int id) {
        if(int2ObjectHashMap.get(id) != null) {
            return int2ObjectHashMap.get(id);
        } else {
            return null;
        }
    }
}
