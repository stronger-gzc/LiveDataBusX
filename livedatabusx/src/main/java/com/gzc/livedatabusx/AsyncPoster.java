package com.gzc.livedatabusx;

/**
 * author：gzc
 * date：2021/1/28
 * describe：
 */
class AsyncPoster implements Runnable{
    private final LiveDataBusX liveDataBusX;
    private final PendingPostQueue queue;

    AsyncPoster(LiveDataBusX liveDataBusX){
        this.liveDataBusX = liveDataBusX;
        queue = new PendingPostQueue();
    }

    public void enqueue(Observation observation){
        PendingPost pendingPost = PendingPost.obtainPendingPost(observation);
        queue.enqueue(pendingPost);
        liveDataBusX.getExecutorService().execute(this);
    }

    @Override
    public void run() {
        PendingPost pendingPost = queue.poll();
        if(pendingPost == null) {
            throw new IllegalStateException("No pending post available");
        }
        liveDataBusX.invokeSubscriber(pendingPost);
    }
}
