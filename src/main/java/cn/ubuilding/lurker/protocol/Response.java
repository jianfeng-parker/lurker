package cn.ubuilding.lurker.protocol;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 10:11
 */

public class Response {

    private String requestId;

    private Object result;

    private Throwable error;

    public Response(String requestId, Object result, Throwable error) {
        this.requestId = requestId;
        this.result = result;
        this.error = error;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
