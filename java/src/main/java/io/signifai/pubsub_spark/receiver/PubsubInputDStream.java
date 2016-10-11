/**
 * Copyright 2016 SignifAI, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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