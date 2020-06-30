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
package com.exactpro.th2.common.message.impl.rabbitmq.router;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.protobuf.Message;

public abstract class AbstractRabbitBatchMessageRouter<M extends Message, B extends Message> extends AbstractRabbitMessageRouter<B> {

    @Override
    protected Map<Set<String>, B> getTargetQueueAliasesAndBatchesForSend(B batch) {

        var filter = filterFactory.createFilter(configuration);

        Map<String, B> result = new HashMap<>();

        for (var msg : crackBatch(batch)) {

            var filterAlias = filter.check(msg).iterator().next();

            result.putIfAbsent(filterAlias, createBatch());

            crackBatch(result.get(filterAlias)).add(msg);

        }

        return Collections.emptyMap();
        //FIXME: fix filters
//        return result.entrySet().stream()
//                .map(entry -> Map.entry(configuration.getQueueFilters().get(entry.getKey()), entry.getValue()))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    protected abstract List<M> crackBatch(B batch);

    protected abstract B createBatch();

}
