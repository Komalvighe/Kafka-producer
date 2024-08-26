import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class SampleProducer {
  public SampleProducer()
  {
	
	Properties properties= new Properties();
	properties.put("bootstrap.servers","my-cluster-kafka-listerner1-bootstrap-amq-streams-kafka.apps.komaltestcluster.lab.psi.pnq2.redhat.com:443");
	//properties.put("bootstrap.servers","localhost:9092");
	properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
	properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
	properties.put("security.protocol", "SSL");
	properties.put("ssl.truststore.password", "password");
	properties.put("ssl.truststore.location", "src/main/java/client.truststore.jks");
	
	ProducerRecord producerRecord=new ProducerRecord("channel", "name","Hellow World from another world");
	
	KafkaProducer kafkaproducer=new KafkaProducer(properties);
	
	kafkaproducer.send(producerRecord);
	kafkaproducer.close();
	
  }
	 public static void main(String[] args) {
			
		 SampleProducer sampleProducer=new SampleProducer();
		 
}

  }
