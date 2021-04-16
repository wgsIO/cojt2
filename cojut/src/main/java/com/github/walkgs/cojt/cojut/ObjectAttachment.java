package com.github.walkgs.cojt.cojut;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ObjectAttachment<T, A> {

    private final Map<T, Queue<A>> attachmentsMap = new ConcurrentHashMap<>();

    public void attach(T owner, A attachment) {
        attachmentsMap.computeIfAbsent(owner, it -> new ConcurrentLinkedQueue<>()).add(attachment);
    }

    public void detach(T owner, A attachment) {
        attachmentsMap.computeIfPresent(owner, (k, v) -> {
            v.remove(attachment);
            return v;
        });
    }

    public Queue<A> getAttachments(T owner) {
        return attachmentsMap.getOrDefault(owner, null);
    }

    public void destroy(T owner) {
        attachmentsMap.remove(owner);
    }

}
