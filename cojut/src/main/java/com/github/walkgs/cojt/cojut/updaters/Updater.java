package com.github.walkgs.cojt.cojut.updaters;

import com.github.walkgs.cojt.cojut.Applicable;
import lombok.Getter;
import lombok.Setter;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

@Getter
@Setter
@SuppressWarnings("WeakerAccess")
public abstract class Updater implements Runnable, Applicable<Updater> {

    private final Counter counter = new Counter();
    private final Thread thread = new Thread(this);
    private transient final Queue<Update> updaters = new ConcurrentLinkedQueue<>();
    private int period = 0;
    private Consumer<Updater> onComplete = null;

    private boolean running = false;

    public boolean register(Update update) {
        if (updaters.contains(update))
            return false;
        this.updaters.add(update);
        return true;
    }

    public boolean unregister(Update update) {
        if (!updaters.contains(update))
            return false;
        this.updaters.remove(update);
        return true;
    }

    public void start() {
        running = true;
        thread.start();
    }

    public void stop() {
        running = false;
        thread.interrupt();
    }

    @Override
    public void run() {
        while (running) {
            counter.start = System.nanoTime();
            checker:
            try {
                Thread.sleep(period);
                if (!check())
                    break checker;
                for (Update update : updaters)
                    update.update();
            } catch (InterruptedException e) {
                System.out.println("UPDATER: Failed to update.");
            }
            counter.end = System.nanoTime();
            counter.elapsed = counter.end - counter.start;
            if (onComplete != null)
                onComplete.accept(this);
        }
    }

    @SuppressWarnings("SameReturnValue")
    public boolean check() {
        return true;
    }

    @Getter
    public static class Counter {

        private long start = 0;
        private long end = 0;
        private long elapsed = 0;

    }

}
