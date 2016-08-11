package io.signifai.pubsub_spark.receiver;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;

import com.google.api.services.pubsub.Pubsub;

import io.signifai.pubsub_spark.utils.PubsubHelper;

public class PubsubReceiver extends Receiver<String> {

	private static final long serialVersionUID = -4941310332870659308L;

	private static final int DEFAULT_BATCH_SIZE = 500;

	private final String subscription;
	private final int batchSize;
	private final boolean decodeData;

	public PubsubReceiver(final String _subscription, final Integer _batchSize, final boolean _decodeData) {
		super(StorageLevel.MEMORY_AND_DISK_2());
		this.subscription = _subscription;
		this.batchSize = _batchSize != null ? _batchSize : DEFAULT_BATCH_SIZE;
		this.decodeData = _decodeData;
	}

	@Override
	public void onStart() {
		try {
			final Pubsub pubsubClient = PubsubHelper.createPubsubClient();

			final Thread thread = new PubsubReceiverWorker(this, "Pubsub - " + subscription, pubsubClient, decodeData);
			thread.start();
		} catch (Exception e) {
			this.stop("Could not start pubsub listener for subscription " + subscription, e);
		}
	}

	@Override
	public void onStop() {

	}

	public String getSubscription() {
		return subscription;
	}

	public int getBatchSize() {
		return batchSize;
	}
}