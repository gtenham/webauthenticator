package nl.gertontenham.poc.eventbus;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;
import nl.gertontenham.poc.data.EventDTO;

public class EventCodec implements MessageCodec<EventDTO, EventDTO> {
    @Override
    public void encodeToWire(Buffer buffer, EventDTO eventDTO) {
        // Encode object to string
        Buffer jsonEncodedBuffer = Json.encodeToBuffer(eventDTO);

        // Length of JSON: is NOT characters count
        int length = jsonEncodedBuffer.length();

        // Write data into given buffer
        buffer.appendInt(length);
        buffer.appendBuffer(jsonEncodedBuffer);
    }

    @Override
    public EventDTO decodeFromWire(int position, Buffer buffer) {
        // My custom message starting from this *position* of buffer
        int _pos = position;

        // Length of JSON
        int length = buffer.getInt(_pos);

        // Get JSON string by it`s length
        // Jump 4 because getInt() == 4 bytes
        String jsonStr = buffer.getString(_pos+=4, _pos+=length);

        return Json.decodeValue(jsonStr, EventDTO.class);
    }

    @Override
    public EventDTO transform(EventDTO eventDTO) {
        return eventDTO;
    }

    @Override
    public String name() {
        // Each codec must have a unique name.
        // This is used to identify a codec when sending a message and for unregistering codecs.
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
