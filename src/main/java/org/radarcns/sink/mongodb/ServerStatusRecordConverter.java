/*
 * Copyright 2017 Kings College London and The Hyve
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.radarcns.sink.mongodb;

import java.util.Collection;
import java.util.Collections;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.bson.Document;
import org.radarcns.application.ApplicationServerStatus;
import org.radarcns.key.MeasurementKey;
import org.radarcns.serialization.RecordConverter;
import org.radarcns.sink.mongodb.util.Converter;

/**
 * RecordConverter to convert a ServerStatus record to a MongoDB Document.
 */
public class ServerStatusRecordConverter implements RecordConverter {

    /**
     * Returns the list of supported schemas, which behaves as the id to select suitable
     * RecordConverter for a SinkRecord.
     *
     * @return a list of supported Schemas
     */
    @Override
    public Collection<String> supportedSchemaNames() {
        return Collections.singleton(MeasurementKey.class.getCanonicalName() + "-"
                + ApplicationServerStatus.class.getCanonicalName());
    }

    /**
     * Converts a ServerStatus SinkRecord into a MongoDB Document.
     *
     * @param sinkRecord record to be converted
     * @return converted MongoDB Document to write
     */
    @Override
    public Document convert(SinkRecord sinkRecord) throws DataException {

        Struct key = (Struct) sinkRecord.key();
        Struct value = (Struct) sinkRecord.value();

        return new Document("_id", Converter.measurementKeyToMongoDbKey(key))
                .append("user", key.getString("userId"))
                .append("source", key.getString("sourceId"))
                .append("serverStatus", value.getString("serverStatus"))
                .append("clientIP", value.getString("ipAddress"))
                .append("timestamp", Converter.toDateTime(value.get("timeReceived")));
    }
}
