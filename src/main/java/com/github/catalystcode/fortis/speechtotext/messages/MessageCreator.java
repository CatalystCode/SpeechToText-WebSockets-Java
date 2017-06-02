package com.github.catalystcode.fortis.speechtotext.messages;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageHeaders.*;
import static com.github.catalystcode.fortis.speechtotext.utils.ProtocolUtils.newTimestamp;

abstract class MessageCreator {
    StringBuilder addHeaders(StringBuilder sb, String path, String requestId, String contentType) {
        sb.append(PATH).append(": ").append(path).append(HEADER_DELIM);
        sb.append(REQUEST_ID).append(": ").append(requestId).append(HEADER_DELIM);
        sb.append(TIMESTAMP).append(": ").append(newTimestamp()).append(HEADER_DELIM);
        sb.append(CONTENT_TYPE).append(": ").append(contentType).append(HEADER_DELIM);
        return sb;
    }
}
