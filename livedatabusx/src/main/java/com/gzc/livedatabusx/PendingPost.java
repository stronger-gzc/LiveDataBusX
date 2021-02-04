package com.gzc.livedatabusx;

import java.util.ArrayList;
import java.util.List;

/**
 * author：gzc
 * date：2021/1/28
 * describe：
 */
final class PendingPost {
    private final static List<PendingPost>pendingPostPool = new ArrayList<>();

    Observation observation;
    PendingPost next;

    private PendingPost(Observation observation){
        this.observation = observation;
    }

    static PendingPost obtainPendingPost(Observation observation){
        synchronized (pendingPostPool){
            int size = pendingPostPool.size();
            if(size>0){
                PendingPost pendingPost = pendingPostPool.remove(size-1);
                pendingPost.observation = observation;
                pendingPost.next = null;
                return pendingPost;
            }
        }
        return new PendingPost(observation);
    }

    static void releasePendingPost(PendingPost pendingPost){
        pendingPost.observation = null;
        pendingPost.next = null;
        synchronized (pendingPostPool){
            if(pendingPostPool.size()<10000){
                pendingPostPool.add(pendingPost);
            }
        }
    }
}
