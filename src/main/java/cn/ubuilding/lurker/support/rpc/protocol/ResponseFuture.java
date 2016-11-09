package cn.ubuilding.lurker.support.rpc.protocol;

import cn.ubuilding.lurker.client.Client;
import cn.ubuilding.lurker.support.rpc.AsyncCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Wu Jianfeng
 * @since 16/4/10 16:05
 */

public final class ResponseFuture implements Future<Response> {

    private static final Logger logger = LoggerFactory.getLogger(ResponseFuture.class);

    private Request request;

    private Response response;

    private Mutex mutex;

    private long startTime;

    private List<AsyncCallback> pendingCallback = new ArrayList<AsyncCallback>();

    private ReentrantLock lock = new ReentrantLock();

    public ResponseFuture(Request request) {
        this.request = request;
        mutex = new Mutex();
        startTime = System.currentTimeMillis();
    }

    public Response get() {
        mutex.acquire(-1);
        return this.response;
    }

    public Response get(long timeout, TimeUnit unit) {
        try {
            boolean success = mutex.tryAcquireNanos(-1, unit.toNanos(timeout));
            if (success) return this.response;
            else throw new RuntimeException("time out to get response for request id(" + this.request.getId() + ")");
        } catch (InterruptedException e) {
            logger.error("get response for request(" + request.getId() + ") from future failure:" + e.getMessage());
            return null;
        }
    }

    /**
     * 在Handler中收到服务端数据后触发此动作(方法)
     */
    public void done(Response response) {
        long doneTime = System.currentTimeMillis();
        this.response = response;
        this.mutex.release(1);
        runPendingCallback();
        if (doneTime - this.startTime > 3000) {
            logger.info("the request(" + request.getId() + ") is so slowly");
        }

    }

    public ResponseFuture addCallback(AsyncCallback callback) {
        lock.lock();
        try {
            if (isDone()) {
                runCallback(callback);
            } else {
                this.pendingCallback.add(callback);
            }
        } finally {
            lock.unlock();
        }
        return this;
    }

    public boolean isDone() {
        return mutex.isDone();
    }

    public Request getRequest() {
        return request;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    private void runPendingCallback() {
        lock.lock();
        try {
            for (final AsyncCallback callback : pendingCallback) {
                runCallback(callback);
            }
        } finally {
            lock.unlock();
        }
    }

    private void runCallback(final AsyncCallback callback) {
        final Response res = this.response;
        Client.submit(new Runnable() {
            public void run() {
                Throwable t = res.getError();
                if (null == t) {
                    callback.success(res.getResult());
                } else {
                    callback.fail(t);
                }
            }
        });
    }

    static class Mutex extends AbstractQueuedSynchronizer {
        private final int done = 1;
        private final int doing = 0;

        protected boolean tryAcquire(int acquires) {
            return getState() == done;
        }

        protected boolean tryRelease(int releases) {
            if (getState() == doing) {
                if (compareAndSetState(doing, done)) {
                    return true;
                }
            }
            return false;
        }

        public boolean isDone() {
            return getState() == done;
        }

    }
}
