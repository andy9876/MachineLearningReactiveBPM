package com.decisioning;

import com.decisioning.KafkaInstance;
import com.decisioning.KafkaProducer;
import com.decisioning.RunModelMS;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConsumerGroupRunModelMS {
    private final ConsumerConnector consumer;
    private final String topic;
    private ExecutorService executor;
    private String line;
    private String key;
    private static final Logger LOG = LoggerFactory.getLogger(ConsumerGroupRunModelMS.class);
    public static Configuration configuration = null;

    public ConsumerGroupRunModelMS(String a_zookeeper, String a_groupId, String a_topic) {
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig(a_zookeeper, a_groupId));
        topic = a_topic;
    }

    public void shutdown() {
        if (consumer != null) {
            consumer.shutdown();
        }
        if (executor != null) {
            executor.shutdown();
        }
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                System.out.println("Timed out waiting for consumer threads to shut down, exiting uncleanly");
            }
        }
        catch (InterruptedException e) {
            System.out.println("Interrupted during shutdown, exiting uncleanly");
        }
    }

    public void run(int a_numThreads) {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(a_numThreads));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
        executor = Executors.newFixedThreadPool(a_numThreads);
        int threadNumber = 0;
        for (final KafkaStream stream : streams) {
            executor.submit(new ConsumerTest(stream, threadNumber));
            ++threadNumber;
        }
    }

    private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("group.id", a_groupId);
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        return new ConsumerConfig(props);
    }

    public static void main(String[] args) {
        String zooKeeper = args[0];
        String groupId = args[1];
        String topic = args[2];
        int threads = Integer.parseInt(args[3]);
        ConsumerGroupRunModelMS example = new ConsumerGroupRunModelMS(zooKeeper, groupId, topic);
        example.run(threads);
    }

    public class ConsumerTest
    implements Runnable {
        private KafkaStream m_stream;
        private int m_threadNumber;

        public ConsumerTest(KafkaStream<byte[], byte[]> a_stream, int a_threadNumber) {
            m_threadNumber = a_threadNumber;
            m_stream = a_stream;
        }

       
        public void run() {
            String stateToCheck = null;
            
            ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
            //ConsumerIterator<String, String> it = m_stream.iterator();
            LOG.info("Reading configuration from YAML file present under conf directory");
            try {                
                configuration = new PropertiesConfiguration("/home/conf/Decision.yaml");
            }
            catch (ConfigurationException e) {
                e.printStackTrace();
                LOG.info("Error Reading Configuration File");
            }
            KafkaInstance kafkaInstance = new KafkaInstance(configuration);
            
            while (it.hasNext()) {
            	LOG.info("reading produced event");
            	
            	key = new String(it.peek().key());            	                       	
            	line = new String (it.next().message());            	            	            	            	

                LOG.info("--------------------------------------------------------");
                LOG.info("Thread " + m_threadNumber + ": " + line);
                stateToCheck = "Run Fraud Model";
                if (line.contains(stateToCheck))                
                {	                	                	
                	LOG.info("in the RunModelMS service");
                	RunModelMS RunModelMS = new RunModelMS();
                	line = RunModelMS.execute(line, stateToCheck);
                	LOG.info("going to write out " + line);
                	KafkaProducer kafkaProducer = new KafkaProducer(line, configuration, key);
                	kafkaInstance.getProducer().send(kafkaProducer.execute());
                }
           }
        }
    }

}