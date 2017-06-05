package com.github.catalystcode.fortis.speechtotext.websocket;

import com.github.catalystcode.fortis.speechtotext.messages.BinaryMessageCreator;
import com.github.catalystcode.fortis.speechtotext.telemetry.AudioTelemetry;
import com.github.catalystcode.fortis.speechtotext.telemetry.CallsTelemetry;
import com.github.catalystcode.fortis.speechtotext.telemetry.ConnectionTelemetry;
import com.github.catalystcode.fortis.speechtotext.utils.RiffHeader;
import org.apache.log4j.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceContentTypes.JSON;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceContentTypes.WAV;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceLimitations.*;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServicePaths.*;
import static com.github.catalystcode.fortis.speechtotext.messages.AudioEndMessageCreator.createAudioEndMessage;
import static com.github.catalystcode.fortis.speechtotext.messages.TextMessageCreator.createTextMessage;
import static com.github.catalystcode.fortis.speechtotext.utils.ProtocolUtils.newGuid;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public abstract class MessageSender {
    private static final Logger log = Logger.getLogger(MessageSender.class);

    private final String connectionId;
    private final String requestId;
    private final BinaryMessageCreator binaryMessageCreator;

    protected MessageSender(String connectionId) {
        this.connectionId = connectionId;
        this.requestId = newGuid();
        this.binaryMessageCreator = new BinaryMessageCreator();
    }

    public final void sendConfiguration() {
        String config = new PlatformInfo().toJson();
        String configMessage = createTextMessage(SPEECH_CONFIG, requestId, JSON, config);
        sendTextMessage(configMessage);
        log.info("Sent speech config: " + config);
    }

    public final void sendAudio(InputStream wavStream) throws IOException, UnsupportedAudioFileException {
        AudioInputStream pcmStream = adjustAudioEncoding(wavStream);
        send16khzMonoPcmAudio(pcmStream);
    }

    private static AudioInputStream adjustAudioEncoding(InputStream sourceWavStream) throws UnsupportedAudioFileException, IOException {
        AudioInputStream audioPcm = getAudioInputStream(sourceWavStream);
        AudioInputStream audio16khz = to16khz(audioPcm);
        AudioInputStream audio16khzMono = toMono(audio16khz);
        AudioInputStream audio16khzMonoPcm = toPcm(audio16khzMono);
        skipRiffHeader(audio16khzMonoPcm);
        return audio16khzMonoPcm;
    }

    private static AudioInputStream toPcm(AudioInputStream sourceAudioStream) {
        AudioFormat sourceFormat = sourceAudioStream.getFormat();
        return getAudioInputStream(new AudioFormat(
            PCM_SIGNED,
            sourceFormat.getSampleRate(),
            sourceFormat.getSampleSizeInBits(),
            sourceFormat.getChannels(),
            sourceFormat.getFrameSize(),
            sourceFormat.getFrameRate(),
            sourceFormat.isBigEndian()), sourceAudioStream);
    }

    private static AudioInputStream toMono(AudioInputStream sourceAudioStream) {
        AudioFormat sourceFormat = sourceAudioStream.getFormat();
        return getAudioInputStream(new AudioFormat(
            sourceFormat.getEncoding(),
            sourceFormat.getSampleRate(),
            sourceFormat.getSampleSizeInBits(),
            NUM_CHANNELS,
            sourceFormat.getFrameSize(),
            sourceFormat.getFrameRate(),
            sourceFormat.isBigEndian()), sourceAudioStream);
    }

    private static AudioInputStream to16khz(AudioInputStream sourceAudioStream) {
        AudioFormat sourceFormat = sourceAudioStream.getFormat();
        return getAudioInputStream(new AudioFormat(
            sourceFormat.getEncoding(),
            SAMPLE_RATE,
            sourceFormat.getSampleSizeInBits(),
            sourceFormat.getChannels(),
            sourceFormat.getFrameSize(),
            sourceFormat.getFrameRate(),
            sourceFormat.isBigEndian()), sourceAudioStream);
    }

    private static void skipRiffHeader(InputStream wavStream) throws IOException, UnsupportedAudioFileException {
        RiffHeader.fromStream(wavStream);
    }

    private void send16khzMonoPcmAudio(InputStream wavStream) {
        AudioTelemetry audioTelemetry = AudioTelemetry.forId(requestId);
        audioTelemetry.recordAudioStarted();
        try {
            byte[] buf = new byte[MAX_BYTES_PER_AUDIO_CHUNK];
            int chunksSent = 0;
            int read;
            while ((read = wavStream.read(buf)) != -1) {
                ByteBuffer audioChunkMessage = binaryMessageCreator.createBinaryMessage(AUDIO, requestId, WAV, buf, read);
                sendBinaryMessage(audioChunkMessage);
                chunksSent++;
            }
            log.info("Sent " + chunksSent + " audio chunks");
        } catch (Exception ex) {
            audioTelemetry.recordAudioFailed(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public final void sendAudioEnd() {
        AudioTelemetry audioTelemetry = AudioTelemetry.forId(requestId);
        ByteBuffer audioEndMessage = createAudioEndMessage(requestId);
        sendBinaryMessage(audioEndMessage);
        log.debug("Sent explicit end-of-audio marker");
        audioTelemetry.recordAudioEnded();
    }

    public final void sendTelemetry() {
        CallsTelemetry callsTelemetry = CallsTelemetry.forId(requestId);
        ConnectionTelemetry connectionTelemetry = ConnectionTelemetry.forId(connectionId);
        AudioTelemetry audioTelemetry = AudioTelemetry.forId(requestId);
        String telemetry = new TelemetryInfo(connectionId, callsTelemetry, connectionTelemetry, audioTelemetry).toJson();
        String telemetryMessage = createTextMessage(TELEMETRY, requestId, JSON, telemetry);
        sendTextMessage(telemetryMessage);
        log.info("Sent telemetry: " + telemetry);
    }

    protected abstract void sendBinaryMessage(ByteBuffer message);
    protected abstract void sendTextMessage(String message);
}
