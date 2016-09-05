package cn.ubuilding.lurker.v2.rpc.api;

/**
 * @author Wu Jianfeng
 * @since 16/8/10 18:09
 */

public interface Result {
    /**
     * Get invoke result.
     *
     * @return result. if no result return null.
     */
    Object getValue();

    /**
     * Get exception.
     *
     * @return exception. if no exception return null.
     */
    Throwable getException();

    /**
     * Has exception.
     *
     * @return has exception.
     */
    boolean hasException();

}
