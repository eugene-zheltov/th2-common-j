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

package com.exactpro.th2.common.schema.event;

import org.jetbrains.annotations.Nullable;

import com.exactpro.th2.common.grpc.EventBatch;
import com.exactpro.th2.common.schema.message.impl.rabbitmq.AbstractRabbitSubscriber;
import com.google.protobuf.TextFormat;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;

public class EventBatchSubscriber extends AbstractRabbitSubscriber<EventBatch> {

    private static final Counter INCOMING_EVENT_BATCH_QUANTITY = Counter.build("th2_mq_incoming_event_batch_quantity", "Quantity of incoming event batches").register();
    private static final Counter INCOMING_EVENT_QUANTITY = Counter.build("th2_mq_incoming_event_quantity", "Quantity of incoming events").register();
    private static final Gauge EVENT_PROCESSING_TIME = Gauge.build("th2_mq_event_processing_time", "Time of processing events").register();

    @Override
    protected Counter getDeliveryCounter() {
        return INCOMING_EVENT_BATCH_QUANTITY;
    }

    @Override
    protected Counter getContentCounter() {
        return INCOMING_EVENT_QUANTITY;
    }

    @Override
    protected Gauge getProcessingTimer() {
        return EVENT_PROCESSING_TIME;
    }

    @Override
    protected int extractCountFrom(EventBatch message) {
        return message.getEventsCount();
    }

    @Override
    protected EventBatch valueFromBytes(byte[] bytes) throws Exception {
        return EventBatch.parseFrom(bytes);
    }

    @Override
    protected String toShortDebugString(EventBatch value) {
        return TextFormat.shortDebugString(value);
    }

    @Nullable
    @Override
    protected EventBatch filter(EventBatch eventBatch) throws Exception {
        return eventBatch;
    }
}
