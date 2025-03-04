package com.msg;


import com.lion.message.GlobalMsgType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExecutionEngineMsgTypeTest {

    @Test
    void from() {
        Assertions.assertEquals(ExecutionEngineMsgType.TOB_MARKET_DATA, ExecutionEngineMsgType.from(GlobalMsgType.TOB_MARKET_DATA));
    }

    @Test
    void fromId() {
        Assertions.assertEquals(ExecutionEngineMsgType.TOB_MARKET_DATA, ExecutionEngineMsgType.fromId(1));
    }

    @Test
    void getId() {
        Assertions.assertEquals(1, ExecutionEngineMsgType.TOB_MARKET_DATA.getId());
    }

}