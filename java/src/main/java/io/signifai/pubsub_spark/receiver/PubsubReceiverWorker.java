/**
 * Copyright 2016-2018 SignifAI, Inc.
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.model.AcknowledgeRequest;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.google.api.services.pubsub.model.PullRequest;
import com.google.api.services.pubsub.model.PullResponse;
import com.google.api.services.pubsub.model.ReceivedMessage;

class PubsubReceiverWorker extends Thread {
	private final PubsubReceiver pubsubReceiver;
	private final Pubsub pubsubClient;
	private final boolean decodeData;

	public PubsubReceiverWorker(final PubsubReceiver _pubsubReceiver, final String _name, final Pubsub _pubsubClient,
			final boolean _decodeData) {
		super(_name);
		this.pubsubReceiver = _pubsubReceiver;
		this.pubsubClient = _pubsubClient;
		this.decodeData = _decodeData;
	}

	@Override
	public void run() {
		try {
			while (!pubsubReceiver.isStopped()) {

				final PullRequest pullRequest = new PullRequest().setReturnImmediately(false)
						.setMaxMessages(pubsubReceiver.getBatchSize());
				PullResponse pullResponse;
				pullResponse = pubsubClient.projects().subscriptions().pull(pubsubReceiver.getSubscription(), pullRequest)
						.execute();
				final List<ReceivedMessage> receivedMessages = pullResponse.getReceivedMessages();

				if (CollectionUtils.isNotEmpty(receivedMessages)) {

					final List<String> messages = new ArrayList<>(pubsubReceiver.getBatchSize());
					final List<String> ackIds = new ArrayList<>(pubsubReceiver.getBatchSize());
					for (final ReceivedMessage receivedMessage : receivedMessages) {
						final PubsubMessage pubsubMessage = receivedMessage.getMessage();

						if (pubsubMessage != null) {
							if (decodeData) {
								messages.add(new String(pubsubMessage.decodeData(), StandardCharsets.UTF_8));
							} else {
								messages.add(pubsubMessage.getData());
							}
						}
						ackIds.add(receivedMessage.getAckId());
					}

					if (CollectionUtils.isNotEmpty(messages)) {
						pubsubReceiver.store(messages.iterator());
					}

					final AcknowledgeRequest ackRequest = new AcknowledgeRequest().setAckIds(ackIds);
					pubsubClient.projects().subscriptions().acknowledge(pubsubReceiver.getSubscription(), ackRequest)
							.execute();
				}
			}
		} catch (Throwable t) {
			pubsubReceiver.restart(
					"Error while fetching messages from pubsub for subscription " + pubsubReceiver.getSubscription(), t);
		}

	}
}