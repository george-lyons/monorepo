package com.lion.scheduler;

import org.agrona.concurrent.Agent;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.SleepingIdleStrategy;

import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

public class TaskScheduler implements Agent {
    private final PriorityQueue<ScheduledTask> taskQueue = new PriorityQueue<>();
    private volatile boolean running = true;

    public void schedule(Runnable task, long initialDelay, long period, TimeUnit unit) {
        long executeAt = System.nanoTime() + unit.toNanos(initialDelay);
        taskQueue.offer(new ScheduledTask(task, executeAt, unit.toNanos(period)));
    }

    @Override
    public int doWork() {
        long now = System.nanoTime();

        while (!taskQueue.isEmpty() && taskQueue.peek().executeAt <= now) {
            ScheduledTask task = taskQueue.poll();
            task.runnable.run();

            // Reschedule the periodic task
            if (task.period > 0) {
                task.executeAt = now + task.period;
                taskQueue.offer(task);
            }
        }
        return 0; // Stay in spin mode
    }

    @Override
    public String roleName() {
        return "LowLatencyPeriodicScheduler";
    }

    public void stop() {
        running = false;
    }

    private static class ScheduledTask implements Comparable<ScheduledTask> {
        final Runnable runnable;
        long executeAt;
        final long period; // 0 means one-time execution

        ScheduledTask(Runnable runnable, long executeAt, long period) {
            this.runnable = runnable;
            this.executeAt = executeAt;
            this.period = period;
        }

        @Override
        public int compareTo(ScheduledTask o) {
            return Long.compare(this.executeAt, o.executeAt);
        }
    }

    public static void main(String[] args) {
        TaskScheduler scheduler = new TaskScheduler();

        AgentRunner runner = new AgentRunner(
                new SleepingIdleStrategy(1), // Minimal sleep to avoid 100% CPU load
                Throwable::printStackTrace,
                null,
                scheduler
        );

        Thread thread = new Thread(runner);
        thread.start();

        // Schedule a periodic task to run every 500ms
        scheduler.schedule(() -> System.out.println("Periodic Task Executed at: " + System.currentTimeMillis()),
                0, 500, TimeUnit.MILLISECONDS);

        try {
            Thread.sleep(5000); // Let the scheduler run for 5 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        scheduler.stop();
    }
}