package com.msg;

import com.lion.message.GlobalMsgType;
import com.lion.message.IntIdentifier;
import org.agrona.collections.Int2ObjectHashMap;

import java.util.Arrays;
import java.util.EnumMap;

public enum ExecutionEngineMsgType implements IntIdentifier {
    TOB_MARKET_DATA (1, GlobalMsgType.TOB_MARKET_DATA),
    QUOTE_STACK_EVENT_MARKET_DATA (1, GlobalMsgType.QUOTE_STACK_EVENT_MARKET_DATA);

    private static final EnumMap<GlobalMsgType, ExecutionEngineMsgType> globalToEngineMSgType = new EnumMap<>(GlobalMsgType.class);
    private static final Int2ObjectHashMap<ExecutionEngineMsgType> int2ObjectHashMap = new Int2ObjectHashMap<>();

    private final GlobalMsgType msgType;
    private final int id;


    ExecutionEngineMsgType (int id, GlobalMsgType msg) {
        this.msgType = msg;
        this.id = id;
    }

    static {
        Arrays.stream(ExecutionEngineMsgType.values()).forEach(engineMsgType -> globalToEngineMSgType.put(engineMsgType.msgType, engineMsgType));
    }

    public static ExecutionEngineMsgType from(GlobalMsgType globalMsgType) {
        return globalToEngineMSgType.get(globalMsgType);
    }


    static {
        for (ExecutionEngineMsgType in : ExecutionEngineMsgType.values()) {
            int2ObjectHashMap.put(in.id, in);
        }
    }

    public static ExecutionEngineMsgType fromId(int id) {
        if(int2ObjectHashMap.get(id) != null) {
            return int2ObjectHashMap.get(id);
        } else {
            return null;
        }
    }

    @Override
    public int getId() {
        return id;
    }
}
