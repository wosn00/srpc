package com.hex.zookeeper;

import com.hex.common.exception.RegistryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author guohs
 * @date 2021/9/8
 */
public abstract class AbsZkService {
    private static final Logger logger = LoggerFactory.getLogger(AbsZkService.class);

    protected String registryAddress = null;

    public void initRegistry(List<String> registryAddresses) {
        this.registryAddress = String.join(",", registryAddresses);
        try {
            ZkUtil.getZkClient(this.registryAddress);
        } catch (RegistryException e) {
            logger.error("Zookeeper init failed");
            throw new RegistryException(e);
        }
    }
}
