/**
 * Copyright © 2018, Ondrej Benkovsky
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package com.github.structlogging;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ngmon.structlog.Logger;
import org.ngmon.structlog.enums.EventLevel;
import org.ngmon.structlog.injection.LogEvent;

import java.io.*;
import java.util.Map;

public class FALLogger implements Logger {
    private final PrintWriter writer;
    private JsonFactory jsonFactory = new JsonFactory(new ObjectMapper());

    public FALLogger() throws IOException {
        writer = new PrintWriter("benchmark6.log", "UTF-8");
    }

    @Override
    public void log(EventLevel level, LogEvent logEvent, String signature) {
        writer.println(getEventJson("events", signature, logEvent.getValueMap(), level));
        writer.flush();
    }

    private String getEventJson(String fqnNS, String event, Map<String, Object> payload, EventLevel level) {
        StringWriter writer = new StringWriter();
        try (JsonGenerator json = this.jsonFactory.createGenerator(writer)) {
            json.writeStartObject();
            json.writeStringField("sid", event);
            json.writeStringField("level", level.toString());
            json.writeStringField("sid_ns", fqnNS);
            json.writeObjectFieldStart("_");
            for (Map.Entry<String, Object> stringObjectEntry : payload.entrySet()) {
                json.writeObjectField(stringObjectEntry.getKey(), stringObjectEntry.getValue());
            }
            json.writeEndObject();
            json.writeEndObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
    }
}
