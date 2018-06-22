package com.sinohb.hardware.test.task;

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class ThreadPool {
    private List<Runnable> mTaskQueue = new LinkedList<>();
    private Worker worker;
    private static ThreadPool mPool;
    private static ThreadFactory TF = new ThreadFactory() {
        AtomicInteger atomicInteger = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "task#" + atomicInteger.getAndIncrement());
        }
    };
    private static ThreadPoolExecutor EXECUTOR = null;

    private ThreadPool() {
        EXECUTOR = new ThreadPoolExecutor(0, 5, 0l, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), TF);
        worker = new Worker();
        worker.start();
    }

    public static final ThreadPool getPool() {
        if (mPool == null) {
            synchronized (ThreadPool.class) {
                if (mPool == null) {
                    mPool = new ThreadPool();
                }
            }
        }
        return mPool;
    }

    public void execute(Runnable task) {
        EXECUTOR.execute(task);
    }

    public void executeOrderTask(Runnable task) {
        synchronized (mTaskQueue) {
            mTaskQueue.add(task);
            mTaskQueue.notifyAll();
        }
    }

    public void executeOrderTask(Runnable[] tasks) {
        synchronized (mTaskQueue) {
            for (Runnable r : tasks) {
                mTaskQueue.add(r);
            }
            mTaskQueue.notifyAll();
        }
    }

    public void executeOrderTask(List<Runnable> tasks) {
        synchronized (mTaskQueue) {
            for (Runnable r : tasks) {
                mTaskQueue.add(r);
            }
            mTaskQueue.notifyAll();
        }
    }

    public void destroy() {
        while (!mTaskQueue.isEmpty()){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (worker != null) {
            worker.stopWork();
            worker = null;
        }
        mTaskQueue.clear();
        EXECUTOR.shutdownNow();
        mPool = null;
        EXECUTOR = null;
    }

    class Worker extends Thread {
        private boolean isRun = true;

        @Override
        public void run() {
            super.run();
            Runnable r = null;
            while (isRun) {
                synchronized (mTaskQueue) {
                    while (isRun && mTaskQueue.isEmpty()) {
                        try {
                            mTaskQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!mTaskQueue.isEmpty()) {
                        r = mTaskQueue.remove(0);
                    }
                }
                if (r != null) {
                    r.run();
                }
                r = null;
            }
        }

        public void stopWork() {
            isRun = false;
        }
    }
}
