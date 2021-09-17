package com.hex.srpc.core.protocol.adpater;

/**
 * @author: hs
 * <p>
 * 协议与Command适配器
 */
public interface ProtocolAdapter<T, R> {

    R convert(T command);

    T reverse(R packet);

}
