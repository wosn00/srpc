package com.hex.common.id;


import com.hex.common.exception.SeqGeneratorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: hs
 */
public class SnowflakeId implements ISnowFlakeId {
    private static final Logger logger = LoggerFactory.getLogger(SnowflakeId.class);
    /**
     * 开始时间截（2021-01-01 00:00:00）
     */
    private static final long START_TIME = 1609430400000L;

    /**
     * 中心id所占的位数
     */
    private static final long dataCenterIdBits = 4L;

    /**
     * 支持的最大中心id，结果是15
     */
    private static final long maxDataCenterId = ~(-1L << dataCenterIdBits);

    /**
     * 机器id所占的位数
     */
    private static final long workerIdBits = 5L;

    /**
     * 支持的最大机器id，结果是15,之所以减1，为了调整时间进行切换 + 16 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private static final long maxWorkerId = ~(-1L << (workerIdBits - 1));

    /**
     * 真正支持的最大的机器id.
     */
    private static final long realMaxWorkerId = ~(-1L << workerIdBits);

    /**
     * 序列在id中占的位数
     */
    private static final long sequenceBits = 12L;

    /**
     * 机器ID向左移12位
     */
    private static final long workerIdShift = sequenceBits;

    /**
     * 数据标识id向左移17位(12+5)
     */
    private static final long dataCenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间截向左移21位(12+5+4)
     */
    private static final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private static final long sequenceMask = ~(-1L << sequenceBits);

    /**
     * 数据中心ID(0~15)
     */
    private long dataCenterId;

    /**
     * 工作机器ID(0~31)
     */
    private transient long workerId;

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    /**
     * 构造函数
     *
     * @param dataCenterId 数据中心ID (0~15)
     * @param workerId     机器ID (0~31)
     */
    public SnowflakeId(int dataCenterId, int workerId) {
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("dataCenter Id can't be greater than %d or less than 0", maxDataCenterId));
        }
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }


    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    @Override
    public synchronized long nextId() {

        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            workerId = (workerId + maxWorkerId + 1) & realMaxWorkerId;
            logger.error("Clock moved backwards. current workerId is {}", workerId);
            throw new SeqGeneratorException();
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - START_TIME) << timestampLeftShift) //
                | (dataCenterId << dataCenterIdShift) //
                | (workerId << workerIdShift) //
                | sequence;
    }

    /**
     * 基于时间戳生成最小ID， sequence 为0
     *
     * @param timestamp
     * @return
     */
    @Override
    public long getIdWithTime(long timestamp) {
        return ((timestamp - START_TIME) << timestampLeftShift) //
                | (dataCenterId << dataCenterIdShift) //
                | (workerId << workerIdShift);
    }

    public long getDataCenterId() {
        return dataCenterId;
    }

    public long getWorkerId() {
        return workerId;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }


    /*###################### parse 雪花id反解析静态接口 #########################*/
    public static SnowflakeId parse(long id) {
        long sequence = ~(-1L << sequenceBits) & id;
        long workerId = parseWorkerId(id);
        long dataCenterId = parseDataCenterId(id);
        long lastTimestamp = (id >> timestampLeftShift) + START_TIME;
        SnowflakeId generator = new SnowflakeId((int) dataCenterId, (int) workerId);
        generator.lastTimestamp = lastTimestamp;
        generator.sequence = sequence;
        return generator;
    }

    public static long parseDataCenterId(long id) {
        return ~(-1L << dataCenterIdBits) & (id >> dataCenterIdShift);
    }

    public static long parseWorkerId(long id) {
        return maxWorkerId & (id >> workerIdShift);
    }

    public static long parseSequence(long id) {
        return ~(-1L << sequenceBits) & id;
    }

    public static long parseLastTimestamp(long id) {
        return (id >> timestampLeftShift) + START_TIME;
    }
}
