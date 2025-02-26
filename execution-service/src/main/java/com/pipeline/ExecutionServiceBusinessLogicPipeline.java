package com.pipeline;

import org.agrona.concurrent.Agent;

public class ExecutionServiceBusinessLogicPipeline implements Agent {

    @Override
    public int doWork() throws Exception {
        return 0;
    }

    @Override
    public String roleName() {
        return "BLP";
    }

}
