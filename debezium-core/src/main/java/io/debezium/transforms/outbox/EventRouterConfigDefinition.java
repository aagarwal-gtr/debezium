/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.transforms.outbox;

import io.debezium.config.Configuration;
import io.debezium.config.EnumeratedValue;
import io.debezium.config.Field;
import org.apache.kafka.common.config.ConfigDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Debezium Outbox Transform configuration definition
 *
 * @author Renato mefi (gh@mefi.in)
 */
public class EventRouterConfigDefinition {

    public enum InvalidOperationBehavior implements EnumeratedValue {
        SKIP_AND_WARN("warn"),
        SKIP_AND_ERROR("error"),
        FATAL("fatal");

        private final String value;

        InvalidOperationBehavior(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

        /**
         * Determine if the supplied value is one of the predefined options.
         *
         * @param value the configuration property value; may not be null
         * @return the matching option, or null if no match is found
         */
        public static InvalidOperationBehavior parse(String value) {
            if (value == null) return null;
            value = value.trim();
            for (InvalidOperationBehavior option : InvalidOperationBehavior.values()) {
                if (option.getValue().equalsIgnoreCase(value)) return option;
            }
            return null;
        }

        /**
         * Determine if the supplied value is one of the predefined options.
         *
         * @param value        the configuration property value; may not be null
         * @param defaultValue the default value; may be null
         * @return the matching option, or null if no match is found and the non-null default is invalid
         */
        public static InvalidOperationBehavior parse(String value, String defaultValue) {
            InvalidOperationBehavior mode = parse(value);
            if (mode == null && defaultValue != null) mode = parse(defaultValue);
            return mode;
        }
    }

    public enum AdditionalFieldPlacement implements EnumeratedValue {
        HEADER("header"),
        ENVELOPE("envelope");

        private final String value;

        AdditionalFieldPlacement(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

        /**
         * Determine if the supplied value is one of the predefined options.
         *
         * @param value the configuration property value; may not be null
         * @return the matching option, or null if no match is found
         */
        public static AdditionalFieldPlacement parse(String value) {
            if (value == null) return null;
            value = value.trim();
            for (AdditionalFieldPlacement option : AdditionalFieldPlacement.values()) {
                if (option.getValue().equalsIgnoreCase(value)) return option;
            }
            return null;
        }

        /**
         * Determine if the supplied value is one of the predefined options.
         *
         * @param value        the configuration property value; may not be null
         * @param defaultValue the default value; may be null
         * @return the matching option, or null if no match is found and the non-null default is invalid
         */
        public static AdditionalFieldPlacement parse(String value, String defaultValue) {
            AdditionalFieldPlacement mode = parse(value);
            if (mode == null && defaultValue != null) mode = parse(defaultValue);
            return mode;
        }
    }

    public static class AdditionalField {
        private final AdditionalFieldPlacement placement;
        private final String field;
        private final String alias;

        AdditionalField(AdditionalFieldPlacement placement, String field, String alias) {
            this.placement = placement;
            this.field = field;
            this.alias = alias;
        }

        public AdditionalFieldPlacement getPlacement() {
            return placement;
        }

        public String getField() {
            return field;
        }

        public String getAlias() {
            return alias;
        }
    }

    static final Field FIELD_EVENT_ID = Field.create("table.field.event.id")
            .withDisplayName("Event ID Field")
            .withType(ConfigDef.Type.STRING)
            .withWidth(ConfigDef.Width.MEDIUM)
            .withImportance(ConfigDef.Importance.LOW)
            .withDefault("id")
            .withDescription("The column which contains the Event ID within the outbox table");

    static final Field FIELD_EVENT_KEY = Field.create("table.field.event.key")
            .withDisplayName("Event Key Field")
            .withType(ConfigDef.Type.STRING)
            .withWidth(ConfigDef.Width.MEDIUM)
            .withImportance(ConfigDef.Importance.LOW)
            .withDescription("The column which contains the Event Key within the outbox table");

    static final Field FIELD_EVENT_TYPE = Field.create("table.field.event.type")
            .withDisplayName("Event Type Field")
            .withType(ConfigDef.Type.STRING)
            .withWidth(ConfigDef.Width.MEDIUM)
            .withImportance(ConfigDef.Importance.LOW)
            .withDefault("type")
            .withDescription("The column which contains the Event Type within the outbox table");

    static final Field FIELD_EVENT_TIMESTAMP = Field.create("table.field.event.timestamp")
            .withDisplayName("Event Timestamp Field")
            .withType(ConfigDef.Type.STRING)
            .withWidth(ConfigDef.Width.MEDIUM)
            .withImportance(ConfigDef.Importance.MEDIUM)
            .withDescription("Optionally you can override the Kafka message timestamp with a value from a chosen" +
                    " field, otherwise it'll be the debezium event processed timestamp.");

    static final Field FIELD_PAYLOAD = Field.create("table.field.payload")
            .withDisplayName("Event Payload Field")
            .withType(ConfigDef.Type.STRING)
            .withWidth(ConfigDef.Width.MEDIUM)
            .withImportance(ConfigDef.Importance.LOW)
            .withDefault("payload")
            .withDescription("The column which contains the Event Type within the outbox table");

    static final Field FIELD_PAYLOAD_ID = Field.create("table.field.payload.id")
            .withDisplayName("Event Payload ID Field")
            .withType(ConfigDef.Type.STRING)
            .withWidth(ConfigDef.Width.MEDIUM)
            .withImportance(ConfigDef.Importance.LOW)
            .withDefault("aggregateid")
            .withDescription("The column which contains the Payload ID within the outbox table");

    static final Field FIELDS_ADDITIONAL_PLACEMENT = Field.create("table.fields.additional.placement")
            .withDisplayName("Settings for each additional column in the outbox table")
            .withType(ConfigDef.Type.LIST)
            .withValidation(EventRouterConfigDefinition::isListOfStringPairs)
            .withWidth(ConfigDef.Width.MEDIUM)
            .withImportance(ConfigDef.Importance.HIGH)
            .withDescription("Extra fields can be added as part of the event envelope or a message header, format" +
                    " is a list of colon-delimited pairs or trios when you desire to have aliases," +
                    " e.g. <code>id:header,field_name:envelope:alias</code> ");

    static final Field FIELD_SCHEMA_VERSION = Field.create("table.field.schema.version")
            .withDisplayName("Event Schema Version Field")
            .withType(ConfigDef.Type.STRING)
            .withWidth(ConfigDef.Width.MEDIUM)
            .withImportance(ConfigDef.Importance.LOW)
            .withDescription("The column which contains the Schema version within the outbox table");

    static final Field ROUTE_BY_FIELD = Field.create("route.by.field")
            .withDisplayName("Field to route events by")
            .withType(ConfigDef.Type.STRING)
            .withDefault("aggregatetype")
            .withWidth(ConfigDef.Width.MEDIUM)
            .withImportance(ConfigDef.Importance.HIGH)
            .withDescription("The column which determines how the events will be routed, the value will become part of" +
                    " the topic name");

    static final Field ROUTE_TOPIC_REGEX = Field.create("route.topic.regex")
            .withDisplayName("The name of the routed topic")
            .withType(ConfigDef.Type.STRING)
            .withValidation(Field::isRegex)
            .withDefault("(?<routedByValue>.*)")
            .withWidth(ConfigDef.Width.MEDIUM)
            .withImportance(ConfigDef.Importance.LOW)
            .withDescription("The default regex to use within the RegexRouter, the default capture will allow" +
                    " to replace the routed field into a new topic name defined in 'route.topic.replacement'");

    static final Field ROUTE_TOPIC_REPLACEMENT = Field.create("route.topic.replacement")
            .withDisplayName("The name of the routed topic")
            .withType(ConfigDef.Type.STRING)
            .withDefault("outbox.event.${routedByValue}")
            .withWidth(ConfigDef.Width.MEDIUM)
            .withImportance(ConfigDef.Importance.HIGH)
            .withDescription("The name of the topic in which the events will be routed, a replacement" +
                    " '${routedByValue}' is available which is the value of The column configured" +
                    " via 'route.by.field'");

    static final Field OPERATION_INVALID_BEHAVIOR = Field.create("debezium.op.invalid.behavior")
            .withDisplayName("Behavior when the route fails to apply")
            .withEnum(InvalidOperationBehavior.class, InvalidOperationBehavior.SKIP_AND_WARN)
            .withWidth(ConfigDef.Width.MEDIUM)
            .withImportance(ConfigDef.Importance.MEDIUM)
            .withDescription("While Debezium is monitoring the table, it's expecting only to see 'create' row events," +
                    " in case something else is processed this transform can log it as warning, error or stop the" +
                    " process");

    /**
     * There are 3 configuration groups available:
     * - Table: Allows you to customize each of The column names in the outbox table for your convenience
     * - Router: The behavior behind the events routing
     * - Debezium: Specific to Debezium behavior which might impact the transform
     *
     * @return ConfigDef
     */
    public static ConfigDef configDef() {
        ConfigDef config = new ConfigDef();
        Field.group(
                config,
                "Table",
                FIELD_EVENT_ID, FIELD_EVENT_KEY, FIELD_EVENT_TYPE, FIELD_PAYLOAD, FIELD_PAYLOAD_ID, FIELD_EVENT_TIMESTAMP, FIELDS_ADDITIONAL_PLACEMENT, FIELD_SCHEMA_VERSION
        );
        Field.group(
                config,
                "Router",
                ROUTE_BY_FIELD, ROUTE_TOPIC_REPLACEMENT
        );
        Field.group(
                config,
                "Debezium",
                OPERATION_INVALID_BEHAVIOR
        );
        return config;
    }

    public static List<AdditionalField> parseAdditionalFieldsConfig(Configuration config) {
        String extraFieldsMapping = config.getString(EventRouterConfigDefinition.FIELDS_ADDITIONAL_PLACEMENT);

        List<AdditionalField> additionalFields = new ArrayList<>();

        if (extraFieldsMapping == null) {
            return additionalFields;
        }

        for (String field: extraFieldsMapping.split(",")) {
            final String[] parts = field.split(":");
            AdditionalFieldPlacement placement = AdditionalFieldPlacement.parse(parts[1]);
            additionalFields.add(
                    new AdditionalField(placement, parts[0], parts.length == 3 ? parts[2] : parts[0])
            );
        }

        return additionalFields;
    }

    private static int isListOfStringPairs(Configuration config, Field field, Field.ValidationOutput problems) {
        List<String> value = config.getStrings(field, ",");
        int errors = 0;
        for (String mapping : value) {
            final String[] parts = mapping.split(":");
            if (parts.length != 2 && parts.length != 3) {
                problems.accept(field, value, "A comma-separated list of valid String pairs or trios " +
                        "is expected but got: " + value);
                ++errors;
            }
        }
        return errors;
    }
}
