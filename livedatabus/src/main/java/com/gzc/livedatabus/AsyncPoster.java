package com.gzc.livedatabus;

/**
 * author：gzc
 * date：2021/1/28
 * describe：
 */
class AsyncPoster implements Runnable{
    private final LiveDataBus liveDataBus;
    private final PendingPostQueue queue;

    AsyncPoster(LiveDataBus liveDataBus){
        this.liveDataBus = liveDataBus;
        queue = new PendingPostQueue();
    }

    public void enqueue(Observation observation){
        PendingPost pendingPost = PendingPost.obtainPendingPost(observation);
        queue.enqueue(pendingPost);
        liveDataBus.getExecutorService().execute(this);
    }

    @Override
    public void run() {
        PendingPost pendingPost = queue.poll();
        if(pendingPost == null) {
            throw new IllegalStateException("No pending post available");
        }
        liveDataBus.invokeSubscriber(pendingPost);
    }
}
