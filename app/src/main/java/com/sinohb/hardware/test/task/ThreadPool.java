package com.sinohb.hardware.test.task;


import com.sinohb.logger.LogTools;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPool {
    private static final String TAG = "ThreadPool";
    private static ThreadPool pool = null;
    private static final int POOL_SIZE = 5;
    private List<Runnable> mAutoExecuteWorkerQueue = new LinkedList<>();
    private Worker[] mWorkers;
//    private QueueWorker mManualWorker;
    private Worker mManualWorker;
    private List<Runnable> mManualWorkerQueue = new LinkedList<>();
    private static final int CORE_SIZE = 5;
    private static final int THREAD_SIZE = 5;
    private static final ThreadFactory TF = new ThreadFactory() {
        private AtomicInteger atomicInteger = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "sendTask" + atomicInteger.getAndIncrement());
        }
    };
    private ThreadPoolExecutor TASK_EXECUTOR;

    public static ThreadPool getPool() {
        if (pool == null) {
            synchronized (ThreadPool.class) {
                if (pool == null) {
                    pool = new ThreadPool();
                }
            }
        }
        return pool;
    }

    private ThreadPool() {
        mWorkers = new Worker[POOL_SIZE];
        for (int i = 0; i < POOL_SIZE; i++) {
            mWorkers[i] = new Worker(mAutoExecuteWorkerQueue);
            new Thread(mWorkers[i]).start();
        }
        mManualWorker = new Worker(mManualWorkerQueue);
        new Thread(mManualWorker).start();
        TASK_EXECUTOR = new ThreadPoolExecutor(CORE_SIZE, THREAD_SIZE, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), TF);
    }

    public void executeSingleTask(Runnable r) {
        if (!isShutDown()) {
            TASK_EXECUTOR.execute(r);
        }
    }

    private boolean isShutDown() {
        return TASK_EXECUTOR != null && TASK_EXECUTOR.isShutdown();
    }

    public void execute(Runnable r) {
        synchronized (mAutoExecuteWorkerQueue) {
            if (mAutoExecuteWorkerQueue.size() >= 3) {
                executeSingleTask(r);
                return;
            }
            mAutoExecuteWorkerQueue.add(r);
            mAutoExecuteWorkerQueue.notifyAll();
        }
    }

    public void execute(List<Runnable> tasks) {
        LogTools.p(TAG, "execute队列加入任务:" + tasks.size());
        synchronized (mAutoExecuteWorkerQueue) {
            for (Runnable r : tasks) {
                mAutoExecuteWorkerQueue.add(r);
            }
            mAutoExecuteWorkerQueue.notifyAll();
        }
    }

    public void execute(Runnable[] tasks) {
        synchronized (mAutoExecuteWorkerQueue) {
            for (Runnable r : tasks) {
                mAutoExecuteWorkerQueue.add(r);
            }
            mAutoExecuteWorkerQueue.notifyAll();
        }
    }

    public void executeOrderTask(Runnable r) {
        LogTools.p(TAG, "队列加入任务:" + r.getClass().getSimpleName());
        synchronized (mManualWorkerQueue) {
            mManualWorkerQueue.add(r);
            mManualWorkerQueue.notify();
        }
    }

    public void executeOrderTask(List<Runnable> tasks) {
        LogTools.p(TAG, "executeOrderTask队列加入任务:" + tasks.size());
        synchronized (mManualWorkerQueue) {
            for (Runnable r : tasks) {
                mManualWorkerQueue.add(r);
            }
            mManualWorkerQueue.notify();
        }
    }

    public void executeOrderTask(Runnable[] tasks) {
        synchronized (mManualWorkerQueue) {
            for (Runnable r : tasks) {
                mManualWorkerQueue.add(r);
            }
            mManualWorkerQueue.notify();
        }
    }

    public synchronized void destroy() {
        while (!mAutoExecuteWorkerQueue.isEmpty()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (!mManualWorkerQueue.isEmpty()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (Worker worker : mWorkers) {
            worker.stopWork();
        }
        mManualWorker.stopWork();
        mAutoExecuteWorkerQueue.clear();
        mManualWorkerQueue.clear();
        if (TASK_EXECUTOR!=null){
            TASK_EXECUTOR.shutdownNow();
            TASK_EXECUTOR = null;
        }

        pool = null;
    }

    private class Worker implements Runnable {
        private boolean isRun = true;
        private List<Runnable> workerQueue;

        Worker(List<Runnable> workerQueue) {
            this.workerQueue = workerQueue;
        }

        @Override
        public void run() {
            if (workerQueue == null) {
                return;
            }
            Runnable r = null;
            while (isRun) {
                synchronized (workerQueue) {
                    while (isRun && workerQueue.isEmpty()) {
                        try {
                            workerQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!workerQueue.isEmpty()) {
                        r = workerQueue.remove(0);
                    }
                    LogTools.p(TAG, "mAutoExecuteWorkerQueue:" + workerQueue.size());
                }
                if (r != null) {
                    r.run();
                    r = null;
                }
            }
            LogTools.p(TAG, "pool结束工作：" + Thread.currentThread().getName());
        }

        public void stopWork() {
            if (workerQueue == null) {
                return;
            }
            synchronized (workerQueue) {
                isRun = false;
                workerQueue.notifyAll();
            }
        }
    }

//    private class QueueWorker implements Runnable {
//        private boolean isRun = true;
//        private List<Runnable> workerQueue;
//
//        QueueWorker(List<Runnable> workerQueue) {
//            this.workerQueue = workerQueue;
//        }
//
//        @Override
//        public void run() {
//            if (workerQueue == null) {
//                return;
//            }
//            Runnable r = null;
//            while (isRun) {
//                synchronized (workerQueue) {
//                    while (isRun && workerQueue.isEmpty()) {
//                        try {
//                            workerQueue.wait();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (!workerQueue.isEmpty()) {
//                        r = workerQueue.remove(0);
//                    }
//                    LogTools.p(TAG, "mManualWorkerQueue:" + workerQueue.size());
//
//                }
//                if (r != null) {
//                    r.run();
//                    r = null;
//                }
//            }
//            LogTools.p(TAG, "pool结束工作：" + Thread.currentThread().getName());
//        }
//
//        public void stopWork() {
//            if (workerQueue == null) {
//                return;
//            }
//            synchronized (workerQueue) {
//                isRun = false;
//                workerQueue.notify();
//            }
//        }
//    }
}
