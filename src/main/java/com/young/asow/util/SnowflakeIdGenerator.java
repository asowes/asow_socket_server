package com.young.asow.util;

public class SnowflakeIdGenerator {

    // 起始的时间戳，可以设置为项目开始的时间
    private static final long START_TIMESTAMP = 1609459200000L; // 2021-01-01 00:00:00

    // 每一部分占用的位数
    private static final long SEQUENCE_BIT = 12; // 序列号占用的位数
    private static final long MACHINE_BIT = 5;   // 机器标识占用的位数
    private static final long DATA_CENTER_BIT = 5; // 数据中心占用的位数

    // 每一部分的最大值
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);
    private static final long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private static final long MAX_DATA_CENTER_NUM = ~(-1L << DATA_CENTER_BIT);

    // 每一部分向左的位移
    private static final long MACHINE_LEFT = SEQUENCE_BIT;
    private static final long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private static final long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;

    private final long dataCenterId;  // 数据中心
    private final long machineId;     // 机器标识
    private long sequence = 0L; // 序列号
    private long lastTimestamp = -1L;  // 上一次时间戳

    private static SnowflakeIdGenerator defaultInstance; // 静态变量，用于保存默认的实例

    // 构造函数私有化，确保只能通过静态方法获取实例
    private SnowflakeIdGenerator(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("Data center ID can't be greater than " + MAX_DATA_CENTER_NUM + " or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("Machine ID can't be greater than " + MAX_MACHINE_NUM + " or less than 0");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    public static synchronized SnowflakeIdGenerator getInstance() {
        if (defaultInstance == null) {
            defaultInstance = new SnowflakeIdGenerator(1, 1); // 默认的数据中心ID和机器ID
        }
        return defaultInstance;
    }

    public synchronized long generateId() {
        long currentTimestamp = System.currentTimeMillis();

        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate ID for " + (lastTimestamp - currentTimestamp) + " milliseconds");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_LEFT)
                | (dataCenterId << DATA_CENTER_LEFT)
                | (machineId << MACHINE_LEFT)
                | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }


    public static long getId() {
        return getInstance().generateId();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            long id = SnowflakeIdGenerator.getId();
            System.out.println(id);
        }
    }
}
