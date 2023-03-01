package study.java2.practice.kafka.core.producer.ex1;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.internals.StickyPartitionCache;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class CustomPartitioner implements Partitioner {
  public static final Logger logger = LoggerFactory.getLogger(CustomPartitioner.class.getName());

  private String specialKeyName;
  private final StickyPartitionCache stickyPartitionCache = new StickyPartitionCache();
  //  private DefaultPartitioner defaultPartitioner;

  @Override
  public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
    List<PartitionInfo> partitionInfos = cluster.partitionsForTopic(topic);
    int numPartitions = partitionInfos.size();
    int numSpectialPartitions = (int) (numPartitions * 0.5);
    int partitionIndex = 0;

    if (keyBytes == null) {
      return stickyPartitionCache.partition(topic, cluster);
    }

    if (((String) key).equals(specialKeyName)) {
      partitionIndex = Utils.toPositive(Utils.murmur2(valueBytes)) % numPartitions;
    } else {
      partitionIndex = Utils.toPositive(Utils.murmur2(keyBytes)) % (numPartitions - numSpectialPartitions) + numSpectialPartitions;
    }

    logger.info("key:{}, is sent to partition:{}", key, partitionIndex);

    return partitionIndex;
  }

  @Override
  public void close() {

  }

  @Override
  public void configure(Map<String, ?> configs) {
    specialKeyName = configs.get("custom.specialKey").toString();
  }

}
