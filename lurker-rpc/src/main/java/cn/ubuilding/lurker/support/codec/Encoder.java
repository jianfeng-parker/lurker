package cn.ubuilding.lurker.support.codec;

import cn.ubuilding.lurker.support.serializer.ProtoStuffSerializer;
import cn.ubuilding.lurker.support.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 11:26
 */

public class Encoder extends MessageToByteEncoder {

    private Serializer serializer;

    // 用于判断待encode的msg 是否属于该class的实例
    private Class<?> clazz;

    public Encoder(Class<?> clazz) {
        this(new ProtoStuffSerializer(), clazz);
    }

    public Encoder(Serializer serializer, Class<?> clazz) {
        this.serializer = serializer;
        this.clazz = clazz;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        if (clazz.isInstance(msg)) {
            byte[] data = serializer.encode(msg);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
