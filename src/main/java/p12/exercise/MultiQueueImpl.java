package p12.exercise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class MultiQueueImpl<T, Q> implements MultiQueue<T, Q> {
    private final Map<Q, Queue<T>> queues;

    public MultiQueueImpl() {
        this.queues = new HashMap<>();
    }

    private void queueExists(final Q queue) {
        if (queue == null) {
            throw new NullPointerException(
                    "The queue parameter can not be null or empty");
        }

        if (!this.queues.containsKey(queue)) {
            throw new IllegalArgumentException(
                    "The queue (" + queue + ") does not exists, please create it with openNewQueue method");
        }
    }

    @Override
    public Set<Q> availableQueues() {
        return Collections.unmodifiableSet(this.queues.keySet());
    }

    @Override
    public void openNewQueue(final Q queue) {
        if (this.queues.containsKey(queue)) {
            throw new IllegalArgumentException("The queue (" + queue + ") already exists");
        }

        this.queues.put(queue, new LinkedList<T>());
    }

    @Override
    public boolean isQueueEmpty(final Q queue) {
        this.queueExists(queue);

        final Queue<T> elements = this.queues.get(queue);

        return elements == null || elements.isEmpty();
    }

    @Override
    public void enqueue(final T elem, final Q queue) {
        this.queueExists(queue);

        final Queue<T> elements = this.queues.get(queue);
        elements.add(elem);
        this.queues.put(queue, elements);
    }

    @Override
    public T dequeue(final Q queue) {
        this.queueExists(queue);

        return this.queues.get(queue).poll();
    }

    @Override
    public Map<Q, T> dequeueOneFromAllQueues() {
        final Map<Q, T> queuesWithOneElement = new HashMap<>();

        for (Q queue : this.queues.keySet()) {
            queuesWithOneElement.put(queue, this.queues.get(queue).poll());
        }

        return queuesWithOneElement;
    }

    @Override
    public Set<T> allEnqueuedElements() {
        final Set<T> elements = new HashSet<>();

        for (Q queue : this.queues.keySet()) {
            elements.addAll(this.queues.get(queue));
        }

        return Collections.unmodifiableSet(elements);
    }

    @Override
    public List<T> dequeueAllFromQueue(final Q queue) {
        this.queueExists(queue);

        final Queue<T> elements = this.queues.get(queue);
        final List<T> copyOfElements = new LinkedList<>();

        while (!elements.isEmpty()) {
            copyOfElements.add(elements.poll());
        }

        return copyOfElements;
    }

    @Override
    public void closeQueueAndReallocate(final Q queue) {
        final List<T> dequeueElements = this.dequeueAllFromQueue(queue);

        this.queues.remove(queue);

        if (this.queues.size() == 0) {
            throw new IllegalStateException("There is not any available queue to move elements");
        }

        // Add all elements from queue to the first available queue
        Queue<T> firstQueueValue = new ArrayList<Queue<T>>(this.queues.values()).getFirst();

        for (T element : dequeueElements) {
            firstQueueValue.add(element);
        }
    }
}
