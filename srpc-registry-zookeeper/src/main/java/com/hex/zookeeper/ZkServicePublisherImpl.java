package com.hex.zookeeper;

import com.hex.common.exception.RegistryException;
import com.hex.common.net.HostAndPort;
import com.hex.registry.ServicePublisher;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: hs
 */
public class ZkServicePublisherImpl extends AbsZkService implements ServicePublisher {
    private static final Logger logger = LoggerFactory.getLogger(ZkServicePublisherImpl.class);

    @Override
    public void publishRpcService(String serviceName, HostAndPort address) {
        try {
            CuratorFramework zkClient = ZkUtil.getZkClient(this.registryAddress);
            ZkUtil.createPersistentNode(zkClient, buildServicePath(serviceName, address));
        } catch (Exception e) {
            logger.error("Zookeeper register rpcService [{}] failed", serviceName);
            throw new RegistryException(e);
        }
    }

    @Override
    public void clearRpcService(String serviceName, HostAndPort node) {
        try {
            CuratorFramework zkClient = ZkUtil.getZkClient(this.registryAddress);
            ZkUtil.clearRegistry(zkClient, node);
        } catch (Exception e) {
            logger.error("Zookeeper clear rpcService [{}] failed", serviceName);
            throw new RegistryException(e);
        }
    }

    private String buildServicePath(String serviceName, HostAndPort address) {
        return ZkUtil.ZK_REGISTER_ROOT_PATH + ZkUtil.SEPERATOR + serviceName + ZkUtil.SEPERATOR + address;
    }

}
