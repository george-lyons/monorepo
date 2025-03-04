package com.execution;

import com.msg.ExecutionEngineMsgType;
import org.agrona.DirectBuffer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class ExecutionManager {
    // Store multiple executions per message type
    private final EnumMap<ExecutionEngineMsgType, List<ExecutionTask>> executionTaskMap = new EnumMap<>(ExecutionEngineMsgType.class);

    /**
     * Registers a new execution task for a message type.
     *
     * @param messageType    the message type the task should handle
     * @param executionTask  the execution task to register
     */
    public void registerExecution(ExecutionEngineMsgType messageType, ExecutionTask executionTask) {
        executionTaskMap.computeIfAbsent(messageType, k -> new ArrayList<>()).add(executionTask);
    }

    /**
     * Routes the message to all registered execution tasks for that message type.
     *
     * @param messageType the type of the message
     * @param buffer the message buffer
     * @param offset the offset in the buffer
     * @param length the length of the message
     */
    public void dispatchEvent(ExecutionEngineMsgType messageType, DirectBuffer buffer, int offset, int length) {
        List<ExecutionTask> tasks = executionTaskMap.get(messageType);

        if (tasks != null && !tasks.isEmpty()) {
            for (ExecutionTask task : tasks) {
                if (task.isAvailable()) {
                    task.handleEvent(messageType, buffer, offset, length);
                }
            }
        }
    }
}