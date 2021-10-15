package com.hex.zookeeper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author: hs
 */
public class ZkServiceDiscoverImpl extends AbsZkService implements ServiceDiscover {
    private static final Logger logger = LoggerFactory.getLogger(ZkServiceDiscoverImpl.class);

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
