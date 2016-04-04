package cn.ubuilding.lurker.serializer;


/**
 * @author Wu Jianfeng
 * @since 16/4/3 10:59
 */
public interface Serializer {

    <T> byte[] encode(T object);

    <T> T decode(byte[] data, Class<T> clazz);
}
