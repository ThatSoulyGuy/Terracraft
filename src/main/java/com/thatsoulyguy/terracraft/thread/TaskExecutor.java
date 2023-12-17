package com.thatsoulyguy.terracraft.thread;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskExecutor
{
    private static final ConcurrentLinkedQueue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();

    public static void QueueTask(Runnable task)
    {
        taskQueue.add(task);
    }

    public static void UpdateTasks()
    {
        while (!taskQueue.isEmpty())
        {
            Runnable task = taskQueue.poll();

            if (task != null)
                task.run();
        }
    }
}