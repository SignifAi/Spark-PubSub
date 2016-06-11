Spark Pubsub Connector
======================

# Roadmap

[] More documentation
[] Add tests
[X] Python wrapper


# How to build

This connector has 2 parts:
* Java component doing the actual work
* Python binding

Therefore both modules will need to be built if you wish to use it with python.

## Java connector

```bash
mvn clean install
```

It will build an uber jar in target/ as _spark_pubsub-<VERSION>.jar_


## Python connector

```bash
python setup.py bdist_egg
```

It will build the egg file in _dist/spark_pubsub-<VERISON>-py<PYTHON_VERSION>.egg_. This file will be passed to _pyspark_ as a way to import the module in the python path.
Alternatively one could install the python module on the Apache Spark nodes themselves.


# How to use

## Java connector
In order to use this _receiver_, you need to attach your jar.

For instance, when using _spark-shell_:
```bash
export SPARK_PUBSUB_JAR="~/projects/spark-pubsub/java/target/spark_pubsub-1.0-SNAPSHOT.jar"

${SPARK_HOME}/bin/spark-shell --jars ${SPARK_PUBSUB_JAR} --driver-class-path ${SPARK_PUBSUB_JAR}
```

Then from the shell:
```scala
import io.signifai.pubsub_spark.receiver.PubsubReceiver
import org.apache.spark.streaming._
import org.apache.spark.rdd._

val SUBSCRIPTION = "<My SUBSCRIPTION>"

var ssc = new StreamingContext(sc,Seconds(5))
var pubsubReceiver = new PubsubReceiver(SUBSCRIPTION, 10)
val customReceiverStream = ssc.receiverStream(pubsubReceiver)
customReceiverStream.foreach((x: RDD[String]) => println(x.count))
customReceiverStream.foreach((x: RDD[String]) => println(x))
customReceiverStream.foreach((x: RDD[String]) => x.foreach(println(_)))

ssc.start
```


## Python connector

In order to use this _receiver_, you need to build and attach both your jar and egg file.

For instance, when using _pyspark_:
```bash
export SPARK_PUBSUB_JAR="~/projects/spark-pubsub/java/target/spark_pubsub-1.0-SNAPSHOT.jar"
export SPARK_PUBSUB_PYTHON_EGG="~/projects/spark-pubsub/python/dist/spark_pubsub-1.0.0-py2.7.egg"

${SPARK_HOME}/bin/pyspark --jars ${SPARK_PUBSUB_JAR} --driver-class-path ${SPARK_PUBSUB_JAR} --py-files ${SPARK_PUBSUB_PYTHON_EGG} 
```

Then from the python shell:

```python
from pyspark.streaming import StreamingContext
from signifai.pubsub import PubsubUtils

SUBSCRIPTION = "<MY SUBSCRIPTION>"

ssc = StreamingContext(sc, 1)
pubsubStream = PubsubUtils.createStream(ssc, SUBSCRIPTION, 5)
pubsubStream.flatMap(lambda x: x).pprint()
ssc.start()
```
