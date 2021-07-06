package com.hex.netty.rpc;

import com.hex.netty.cmd.IHadnler;
import io.netty.channel.epoll.Epoll;
import org.apache.commons.lang3.SystemUtils;

import java.util.List;

/**
 * @author: hs
 */
public abstract class AbstractRpc {

    protected List<IHadnler> hadnlers;

    protected boolean useEpoll() {
        return SystemUtils.IS_OS_LINUX && Epoll.isAvailable();
    }
}
