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
package com.exactpro.th2.common.factory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import com.exactpro.th2.common.loader.impl.DefaultLoader;
import com.exactpro.th2.common.message.MessageRouter;
import com.exactpro.th2.common.message.configuration.MessageRouterConfiguration;
import com.exactpro.th2.common.message.impl.rabbitmq.configuration.RabbitMQConfiguration;
import com.exactpro.th2.grpc.configuration.GrpcRouter;
import com.exactpro.th2.grpc.configuration.GrpcRouterConfiguration;
import com.exactpro.th2.infra.grpc.MessageBatch;
import com.exactpro.th2.infra.grpc.RawMessageBatch;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractCommonFactory {

    private static ObjectMapper mapper = new ObjectMapper();
    private RabbitMQConfiguration rabbitMQConfiguration = null;
    private MessageRouterConfiguration messageRouterConfiguration = null;
    private GrpcRouterConfiguration grpcRouterConfiguration = null;

    public MessageRouter<MessageBatch> getMessageBatchRouter() {
        MessageRouter<MessageBatch> router = DefaultLoader.INSTANCE.createInstance(MessageRouter.class, MessageBatch.class);
        router.init(getRabbitMqConfiguration(), getMessageRouterConfiguration());
        return router;
    }

    public MessageRouter<RawMessageBatch> getRawMessageBatchRouter() {
        MessageRouter<RawMessageBatch> router = DefaultLoader.INSTANCE.createInstance(MessageRouter.class, RawMessageBatch.class);
        router.init(getRabbitMqConfiguration(), getMessageRouterConfiguration());
        return router;
    }

    public GrpcRouter getGrpcRouter() {
        GrpcRouter router = DefaultLoader.INSTANCE.createInstance(GrpcRouter.class);
        router.init(getGrpcRouterConfiguration());
        return router;
    }

    public <T> T getCustomConfiguration(Class<T> confClass) {
        try (var in = new FileInputStream(getPathToCustomConfiguration().toFile())) {
            return mapper.readerFor(confClass).readValue(in);
        } catch (IOException e) {
            throw new IllegalStateException("Can not read custom configuration");
        }
    }

    protected abstract Path getPathToRabbitMQConfiguration();
    protected abstract Path getPathToMessageRouterConfiguration();
    protected abstract Path getPathToGrpcRouterConfiguration();
    protected abstract Path getPathToCustomConfiguration();

    private synchronized RabbitMQConfiguration getRabbitMqConfiguration() {
        if (rabbitMQConfiguration == null) {
            try (var in = new FileInputStream(getPathToRabbitMQConfiguration().toFile())) {
                rabbitMQConfiguration = mapper.readerFor(RabbitMQConfiguration.class).readValue(in);
            } catch (IOException e) {
                throw new IllegalStateException("Can not read rabbit mq configuration", e);
            }
        }

        return rabbitMQConfiguration;
    }

    private synchronized MessageRouterConfiguration getMessageRouterConfiguration() {
        if (messageRouterConfiguration == null) {
            try (var in = new FileInputStream(getPathToMessageRouterConfiguration().toFile())) {
                messageRouterConfiguration = mapper.readerFor(MessageRouterConfiguration.class).readValue(in);
            } catch (IOException e) {
                throw new IllegalStateException("Can not read message router configuration", e);
            }
        }

        return messageRouterConfiguration;
    }

    private synchronized GrpcRouterConfiguration getGrpcRouterConfiguration() {
        if (grpcRouterConfiguration != null) {
            try (var in = new FileInputStream(getPathToGrpcRouterConfiguration().toFile())) {
                grpcRouterConfiguration = mapper.readerFor(GrpcRouterConfiguration.class).readValue(in);
            } catch (IOException e) {
                throw new IllegalStateException("Can not read grpc router configuration", e);
            }
        }

        return grpcRouterConfiguration;
    }

}
