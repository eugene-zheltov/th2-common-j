/*
 * Copyright 2020-2020 Exactpro (Exactpro Systems Limited)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.exactpro.th2.common.schema.message.impl.rabbitmq.parsed;

import com.exactpro.th2.common.grpc.Message;
import com.exactpro.th2.common.grpc.MessageBatch;
import com.exactpro.th2.common.schema.filter.strategy.FilterStrategy;
import com.exactpro.th2.common.schema.message.configuration.RouterFilter;
import com.exactpro.th2.common.schema.message.impl.rabbitmq.AbstractRabbitBatchSubscriber;
import com.google.protobuf.TextFormat;

import java.util.List;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;

public class RabbitParsedBatchSubscriber extends AbstractRabbitBatchSubscriber<Message, MessageBatch> {

    private static final Counter INCOMING_PARSED_MSG_BATCH_QUANTITY = Counter.build("th2_mq_incoming_parsed_msg_batch_quantity", "Quantity of incoming parsed message batches").register();
    private static final Counter INCOMING_PARSED_MSG_QUANTITY = Counter.build("th2_mq_incoming_parsed_msg_quantity", "Quantity of incoming parsed messages").register();
    private static final Gauge PARSED_MSG_PROCESSING_TIME = Gauge.build("th2_mq_parsed_msg_processing_time", "Time of processing parsed messages").register();

    @Override
    protected Counter getDeliveryCounter() {
        return INCOMING_PARSED_MSG_BATCH_QUANTITY;
    }

    @Override
    protected Counter getContentCounter() {
        return INCOMING_PARSED_MSG_QUANTITY;
    }

    @Override
    protected Gauge getProcessingTimer() {
        return PARSED_MSG_PROCESSING_TIME;
    }

    @Override
    protected int extractCountFrom(MessageBatch message) {
        return message.getMessagesCount();
    }

    public RabbitParsedBatchSubscriber(List<? extends RouterFilter> filters) {
        super(filters);
    }

    public RabbitParsedBatchSubscriber(List<? extends RouterFilter> filters, FilterStrategy filterStrategy) {
        super(filters, filterStrategy);
    }


    @Override
    protected MessageBatch valueFromBytes(byte[] body) throws Exception {
        return MessageBatch.parseFrom(body);
    }

    @Override
    protected List<Message> getMessages(MessageBatch batch) {
        return batch.getMessagesList();
    }

    @Override
    protected MessageBatch createBatch(List<Message> messages) {
        return MessageBatch.newBuilder().addAllMessages(messages).build();
    }

    @Override
    protected String toShortDebugString(MessageBatch value) {
        return TextFormat.shortDebugString(value);
    }

    @Override
    protected Metadata extractMetadata(Message message) {
        var metadata = message.getMetadata();
        var messageID = metadata.getId();
        return Metadata.builder()
                .messageType(metadata.getMessageType())
                .direction(messageID.getDirection())
                .sequence(messageID.getSequence())
                .sessionAlias(messageID.getConnectionId().getSessionAlias())
                .build();
    }

}
