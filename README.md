Spark Pubsub Connector
======================

# Introduction

This project enables Apache Spark streaming applications to consume messages from Google Pubsub from Java and Python.
It is released under the Apache License v2.

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
export SPARK_PUBSUB_JAR="~/projects/spark-pubsub/java/target/spark_pubsub-1.1-SNAPSHOT.jar"

${SPARK_HOME}/bin/spark-shell --jars ${SPARK_PUBSUB_JAR} --driver-class-path ${SPARK_PUBSUB_JAR}
```

Then from the shell:
```scala
import io.signifai.pubsub_spark.receiver.PubsubReceiver
import org.apache.spark.streaming._
import org.apache.spark.rdd._

val SUBSCRIPTION = "<My SUBSCRIPTION>"

var ssc = new StreamingContext(sc,Seconds(5))
var pubsubReceiver = new PubsubReceiver(SUBSCRIPTION, 10, true)
val customReceiverStream = ssc.receiverStream(pubsubReceiver)
customReceiverStream.map(x => x).foreachRDD((x: RDD[String]) => println(x.count))
customReceiverStream.map(x => x).foreachRDD((x: RDD[String]) => println(x))
customReceiverStream.map(x => x).foreachRDD((x: RDD[String]) => x.take(10).foreach(println(_)))

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
pubsubStream = PubsubUtils.createStream(ssc, SUBSCRIPTION, 5, True)
pubsubStream.flatMap(lambda x: x).pprint()
ssc.start()
```


# Copyright and License

Copyright 2016-2018 SignifAI, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
