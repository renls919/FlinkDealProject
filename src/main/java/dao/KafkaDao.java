package dao;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import util.PropUtil;

import java.util.Properties;

public class KafkaDao {

    private PropUtil propUtil = new PropUtil();

    /**
     * 将指定数据发送到Kafka主题
     * @param data 要发送的数据
     * @param topic 发送数据的Kafka主题
     */
    public void toKafkaTopic(String data, String topic) {
        Properties props = new Properties();

        // 设置Kafka服务器的URL地址
        props.put("bootstrap.servers", propUtil.readString("kafka_url"));

        // 设置确认模式为全部确认
        props.put("acks", "all");

        // 设置重试次数为0
        props.put("retries", 0);

        // 设置批量发送的数据大小
        props.put("batch.size", 16384);

        // 设置键的序列化器
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // 设置值的序列化器
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = null;
        try {
            // 创建Kafka生产者
            producer = new KafkaProducer<String, String>(props);

            // 发送数据到指定的Kafka主题
            producer.send(new ProducerRecord<String, String>(topic, data));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭Kafka生产者
            assert producer != null;
            producer.close();
        }
    }
}