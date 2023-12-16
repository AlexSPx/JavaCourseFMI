package bg.sofia.uni.fmi.mjt.photoalbum;

import java.util.ArrayList;
import java.util.List;

public class ProcessorManager {
    private final List<WorkerThread> threads;
    private final List<Runnable> taskQueue;
    private boolean isShutdown = false;

    public ProcessorManager(int numThreads) {
        threads = new ArrayList<>();
        taskQueue = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            WorkerThread thread = new WorkerThread();
            thread.setName("Worker " + i);
            threads.add(thread);
            thread.start();
        }
    }

    public void submit(Runnable task) {
        synchronized (taskQueue) {
            taskQueue.add(task);
            taskQueue.notify();
        }
    }

    public synchronized void shutdown() {
        isShutdown = true;
        try {
            for (Thread thread : threads) {
                System.out.println("waiting for - " + thread.getName());
                thread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private class WorkerThread extends Thread {
        @Override
        public void run() {
            while (true) {
                Runnable task = null;

                synchronized (taskQueue) {
                    while (taskQueue.isEmpty() && !isShutdown) {
                        try {
                            taskQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (taskQueue.isEmpty()) {
                        break;
                    }

                    task = taskQueue.remove(0);
                }

                if (task != null) {
                    task.run();
                }
            }
        }
    }
}

