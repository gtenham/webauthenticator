package nl.gertontenham.poc.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;

@JsonDeserialize(builder = EventDTO.Builder.class)
public class EventDTO implements Serializable {

    private final String subject;
    private final String type;
    private final Object payload;

    private EventDTO(Builder builder) {
        subject = builder.subject;
        type = builder.type;
        payload = builder.payload;
    }

    public static class Builder {
        private String subject;
        private String type;
        private Object payload;

        public Builder withSubject(String val) {
            this.subject = val;
            return this;
        }

        public Builder withType(String val) {
            this.type = val;
            return this;
        }

        public Builder withPayload(Object val) {
            this.payload = val;
            return this;
        }

        public EventDTO build() { return new EventDTO(this); }
    }

    public String getSubject() {
        return subject;
    }

    public String getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Event [type=" + type + ", subject="+ subject+"]";
    }
}
