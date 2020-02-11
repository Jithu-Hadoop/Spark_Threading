import java.io.File
import java.util.HashMap

import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.sql._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.{KafkaUtils, OffsetRange}
 object Spark_Threading {
  def main(args: Array[String]) {
    val spark = SparkSession.builder().master("local[2]").appName("dd").getOrCreate()
    val sc=spark.sparkContext
    implicit val confFile = ConfigFactory.parseFile(new File("F:/Scala_Producer/src/main/scala/Kafka_Tutorial/Config.txt"))
    val brokers = confFile.getString("kafka.metadata_broker_list")
    val props = new HashMap[String, Object]()
    val ssc = new StreamingContext(spark.sparkContext,Seconds(60))

    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringDeserializer")
    val topics = List("CGTUTORIAL")
    import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

    val offsetRangess=Array(OffsetRange("CGTUTORIAL",0,0,4),OffsetRange("CGTUTORIAL",1,0,4))
    import org.apache.spark.streaming.kafka010.{KafkaUtils, OffsetRange}
    import org.apache.spark.streaming.kafka010._
    import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
    import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
    import org.apache.kafka.clients.consumer.ConsumerRecord
    import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
    import org.apache.kafka.clients.consumer.ConsumerRecord
    val kafkaParams = Map[String, Object](

      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
      ConsumerConfig.GROUP_ID_CONFIG -> "1",
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG-> "earliest",
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG->"false",
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer])
     //
   // val consumerStrategy=ConsumerStrategies.Subscribe[String,String](topics,props,offsetRangess)
   // val consumerStrategy = ConsumerStrategies.Subscribe[String, String](topicsSet, kafkaParams,offsets.toLocalIterator.toMap)
    val rdd1 = KafkaUtils.createDirectStream[String, String](
      ssc, LocationStrategies.PreferConsistent,
     Subscribe[String,String](topics,kafkaParams)
    )
    var totalTweets:Long = 0
   // val rdd1=KafkaUtils.createRDD(sc,props,offsetRangess,PreferConsistent)

    rdd1.foreachRDD((rdd, time) => {
      totalTweets+=rdd.count()})
    ssc.start()
    ssc.awaitTermination()
  }
}
