package com.github.catalystcode.fortis.speechtotext;

import com.github.catalystcode.fortis.speechtotext.utils.Func;

import java.io.InputStream;

public interface Transcriber {
    void transcribe(InputStream audioStream, Func<String> onResult, Func<String> onHypothesis) throws Exception;
}
