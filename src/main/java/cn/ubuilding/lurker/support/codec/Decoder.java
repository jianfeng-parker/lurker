package cn.ubuilding.lurker.support.codec;

import cn.ubuilding.lurker.support.serializer.ProtoStuffSerializer;
import cn.ubuilding.lurker.support.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 11:20
 */

public class Decoder extends ByteToMessageDecoder {

    private Serializer serializer;

    // 将msg序列化成该class的实例并返回
    private Class<?> clazz;

    public Decoder(Class<?> clazz) {
        this(new ProtoStuffSerializer(), clazz);
    }

    public Decoder(Serializer serializer, Class<?> clazz) {
        this.serializer = serializer;
        this.clazz = clazz;
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLen = in.readInt();
        if (dataLen < 0) {
            ctx.close();
        }
        if (in.readableBytes() < dataLen) {
            in.resetReaderIndex();
        }
        byte[] data = new byte[dataLen];
        in.readBytes(data);
        Object object = serializer.decode(data, clazz);
        out.add(object);
    }
}
