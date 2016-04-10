package cn.ubuilding.lurker.cusumer;

import cn.ubuilding.lurker.common.Request;
import cn.ubuilding.lurker.common.Response;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author Wu Jianfeng
 * @since 16/4/10 16:05
 */

public class ResponseFuture implements Future<Response> {

    private Request request;

    private Response response;

    private Mutex mutex;

    private long startTime;

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
            // TODO logging...
            return null;
        }
    }

    /**
     * 在Handler中收到服务端数据后触发此动作(方法)
     *
     * @param response
     */
    public void done(Response response) {
        long doneTime = System.currentTimeMillis();
        this.response = response;
        this.mutex.release(1);
        // TODO  此处还可以执行其它逻辑
        if (doneTime - this.startTime > 3000) {
            // TODO logging: request is so slowly
        }

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
