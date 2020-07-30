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
package com.exactpro.th2.common.event.bean.builder;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import com.exactpro.th2.common.event.bean.Message;

public class MessageBuilder {
    public final static String MESSAGE_TYPE = "message";

    private String text;

    public MessageBuilder text(String text) {
        this.text = requireNonNull(text, "Text can't be null");
        return this;
    }

    public Message build() {
        Message message = new Message();
        message.setType(MESSAGE_TYPE);
        message.setData(text);
        return message;
    }
}