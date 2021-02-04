package com.gzc.livedatabusx;

/**
 * author：gzc
 * date：2021/1/28
 * describe：
 */
public class BackgroundPoster implements Runnable{
    private final PendingPostQueue queue;
    private final LiveDataBusX liveDataBusX;

    private volatile boolean executorRunning;

    BackgroundPoster(LiveDataBusX liveDataBusX) {
        this.liveDataBusX = liveDataBusX;
        queue = new PendingPostQueue();
    }

    public void enqueue(Observation observation) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(observation);
        synchronized (this) {
            queue.enqueue(pendingPost);
            if (!executorRunning) {
                executorRunning = true;
                liveDataBusX.getExecutorService().execute(this);
            }
        }
    }

    @Override
    public void run() {
        try {
            try {
                while (true) {
                    PendingPost pendingPost = queue.poll(1000);
                    if (pendingPost == null) {
                        synchronized (this) {
                            // Check again, this time in synchronized
                            pendingPost = queue.poll();
                            if (pendingPost == null) {
                                executorRunning = false;
                                return;
                            }
                        }
                    }
                    liveDataBusX.invokeSubscriber(pendingPost);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            executorRunning = false;
        }
    }
}
