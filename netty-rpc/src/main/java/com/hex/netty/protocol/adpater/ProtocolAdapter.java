package com.hex.netty.protocol.adpater;

/**
 * @author: hs
 * <p>
 * 协议适配器
 */
public interface ProtocolAdapter<T, R> {

    R encode(T command);

    T decode(R packet);

}
