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

package com.exactpro.th2.common.schema.message.impl.rabbitmq;

import com.exactpro.th2.common.grpc.Direction;
import com.exactpro.th2.common.schema.filter.strategy.FilterStrategy;
import com.exactpro.th2.common.schema.filter.strategy.impl.DefaultFilterStrategy;
import com.exactpro.th2.common.schema.message.configuration.RouterFilter;
import com.google.protobuf.Message;
import lombok.Builder;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractRabbitBatchSubscriber<M extends Message, MB> extends AbstractRabbitSubscriber<MB> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRabbitBatchSubscriber.class);


    private FilterStrategy filterStrategy;

    private List<? extends RouterFilter> filters;


    public AbstractRabbitBatchSubscriber(List<? extends RouterFilter> filters) {
        this(filters, new DefaultFilterStrategy());
    }

    public AbstractRabbitBatchSubscriber(List<? extends RouterFilter> filters, FilterStrategy filterStrategy) {
        this.filters = filters;
        this.filterStrategy = filterStrategy;
    }


    @Override
    protected MB filter(MB batch) {
        if (filters.isEmpty()) {
            return batch;
        }

        var messages = new ArrayList<>(getMessages(batch));

        var each = messages.iterator();

        while (each.hasNext()) {
            var msg = each.next();
            if (!filterStrategy.verify(msg, filters)) {
                each.remove();
                LOGGER.debug("Message skipped because it did not satisfy filters: " + extractMetadata(msg));
            }
        }

        return messages.isEmpty() ? null : createBatch(messages);
    }


    protected abstract List<M> getMessages(MB batch);

    protected abstract MB createBatch(List<M> messages);

    protected abstract Metadata extractMetadata(M message);


    @Getter
    @Builder
    protected static class Metadata {
        private long sequence;
        private String messageType;
        private String sessionAlias;
        private Direction direction;

        @Override
        public String toString() {
            return "Message{ " +
                    "messageType='" + messageType + '\'' +
                    ", sessionAlias='" + sessionAlias + '\'' +
                    ", direction=" + direction +
                    ", sequence=" + sequence +
                    " }";
        }
    }

}
