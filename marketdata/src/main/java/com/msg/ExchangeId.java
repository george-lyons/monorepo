package com.msg;

import org.agrona.collections.Int2ObjectHashMap;

public enum ExchangeId  {
    BINANCE((short) 1),
    KRAKEN((short) 2);

    private final short id;

    ExchangeId(short id) {
        this.id = id;
    }

    private static final Int2ObjectHashMap<ExchangeId> int2ObjectHashMap = new Int2ObjectHashMap<>();

    static {
        for (ExchangeId in : ExchangeId.values()) {
            int2ObjectHashMap.put(in.id, in);
        }
    }

    public short getId() {
        return id ;
    }

    public static ExchangeId fromId(short id) {
        if(int2ObjectHashMap.get(id) != null) {
            return int2ObjectHashMap.get(id);
        } else {
            return null;
        }
    }
}
