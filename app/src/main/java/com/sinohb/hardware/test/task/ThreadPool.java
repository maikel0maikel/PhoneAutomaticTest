package com.sinohb.hardware.test.task;

import java.util.LinkedList;
import java.util.List;

public final class ThreadPool {
    private List<Runnable> mTaskQueue = new LinkedList<>();
    Worker worker;
    private static ThreadPool mPool;

    private ThreadPool() {
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
        synchronized (mTaskQueue) {
            mTaskQueue.add(task);
            mTaskQueue.notifyAll();
        }
    }

    public void execute(Runnable[] tasks) {
        synchronized (mTaskQueue) {
            for (Runnable r : tasks) {
                mTaskQueue.add(r);
            }
            mTaskQueue.notifyAll();
        }
    }

    public void execute(List<Runnable> tasks) {
        synchronized (mTaskQueue) {
            for (Runnable r : tasks) {
                mTaskQueue.add(r);
            }
            mTaskQueue.notifyAll();
        }
    }

    public void destroy(){
        if (worker!=null){
            worker.stopWork();
            worker = null;
        }
        mTaskQueue.clear();
        mPool = null;
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
