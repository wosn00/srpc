package com.hex.zookeeper;

import com.hex.common.exception.RegistryException;
import com.hex.common.net.HostAndPort;
import com.hex.discovery.ServiceDiscovery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author: hs
 */
public class ZkServiceDiscoveryImpl extends AbsZkService implements ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ZkServiceDiscoveryImpl.class);

    @Override
    public List<HostAndPort> discoverRpcServiceAddress(String serviceName) {
        List<HostAndPort> childrenNodes;
        try {
            CuratorFramework zkClient = ZkUtil.getZkClient(this.registryAddress);
            childrenNodes = ZkUtil.getChildrenNodes(zkClient, serviceName);
            if (CollectionUtils.isEmpty(childrenNodes)) {
                logger.error("The address of the service [{}] could not be found from zookeeper", serviceName);
                throw new RegistryException();
            }
        } catch (RegistryException e) {
            logger.error("Zookeeper discover rpcService failed");
            throw new RegistryException(e);
        }
        return childrenNodes;
    }
}
