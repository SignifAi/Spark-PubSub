Spark Pubsub Connector
======================

# Roadmap

[] More documentation
[] Add tests
[] Python wrapper


# How to build

```bash
mvn clean install
```

It will build an uber jar in target/ as _spark_pubsub-<VERSION>.jar_


# How to use

In order to use this _receiver_, you need to attach your jar.

For instance, when using _spark-shell_:
```bash
${SPARK_HOME}/bin/spark-shell --jars ~/projects/spark-pubsub/spark-pubsub/target/spark_pubsub-1.0-SNAPSHOT.jar
```

Then from the shell:
```scala
import io.signifai.pubsub_spark.receiver.PubsubReceiver
import org.apache.spark.streaming._
import org.apache.spark.rdd._

var ssc = new StreamingContext(sc,Seconds(5))
var pubsubReceiver = new PubsubReceiver("projects/ivory-vim-123504/subscriptions/test-spark", 10)
val customReceiverStream = ssc.receiverStream(pubsubReceiver)
customReceiverStream.foreach((x: RDD[String]) => println(x.count))
customReceiverStream.foreach((x: RDD[String]) => println(x))
customReceiverStream.foreach((x: RDD[String]) => x.foreach(println(_)))

ssc.start
```

