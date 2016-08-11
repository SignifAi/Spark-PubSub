package io.signifai.pubsub_spark.receiver;

import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.dstream.PluggableInputDStream;

import scala.reflect.ClassTag;

public class PubsubInputDStream extends PluggableInputDStream<String> {
	final private static ClassTag<String> STRING_CLASS_TAG = scala.reflect.ClassTag$.MODULE$.apply(String.class);

	public PubsubInputDStream(final StreamingContext _ssc, final String _subscription, final Integer _batchSize,
			final boolean _decodeData) {
		super(_ssc, new PubsubReceiver(_subscription, _batchSize, _decodeData), STRING_CLASS_TAG);
	}

	public PubsubInputDStream(final JavaStreamingContext _jssc, final String _subscription, final Integer _batchSize,
			final boolean _decodeData) {
		super(_jssc.ssc(), new PubsubReceiver(_subscription, _batchSize, _decodeData), STRING_CLASS_TAG);
	}
}