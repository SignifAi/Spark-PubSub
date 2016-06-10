package io.signifai.pubsub_spark.receiver;

import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.dstream.PluggableInputDStream;

import scala.reflect.ClassTag;

public class PubsubInputDStream extends PluggableInputDStream<String> {
	final private static ClassTag<String> StringClassTag = scala.reflect.ClassTag$.MODULE$.apply(String.class);

	public PubsubInputDStream(final StreamingContext ssc_, final String _subscription, final Integer _batchSize) {
		super(ssc_, new PubsubReceiver(_subscription, _batchSize), StringClassTag);
	}

	public PubsubInputDStream(final JavaStreamingContext _jssc, final String _subscription, final Integer _batchSize) {
		super(_jssc.ssc(), new PubsubReceiver(_subscription, _batchSize), StringClassTag);
	}
}