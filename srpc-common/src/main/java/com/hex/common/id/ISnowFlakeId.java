package com.hex.common.id;

/**
 * @author: hs
 * <p>
 * 雪花id
 */
public interface ISnowFlakeId {

    /**
     * 生成下一个command id
     *
     * @return 雪花id
     */
    long nextId();

    /**
     * 基于时间生成 内序为0 的雪花ID
     *
     * @param timestamp
     * @return
     */
    long getIdWithTime(long timestamp);

}
