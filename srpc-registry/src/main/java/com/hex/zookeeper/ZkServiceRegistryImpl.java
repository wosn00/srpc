package com.hex.zookeeper;

import com.hex.common.exception.RegistryException;
import com.hex.common.net.HostAndPort;
import com.hex.registry.ServiceRegistry;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author: hs
 */
public class ZkServiceRegistryImpl implements ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ZkServiceRegistryImpl.class);

    @Override
    public void registerRpcService(List<String> registryAddresses, String serviceName, HostAndPort address) {
        String servicePath = ZkUtil.ZK_REGISTER_ROOT_PATH + ZkUtil.SEPERATOR + serviceName + address;
        String clusterAddress = String.join(",", registryAddresses);
        try {
            CuratorFramework zkClient = ZkUtil.getZkClient(clusterAddress);
            ZkUtil.createPersistentNode(zkClient, servicePath);
        } catch (Exception e) {
            logger.error("Zookeeper register rpcService [{}] failed", serviceName);
            throw new RegistryException();
        }
    }
}
