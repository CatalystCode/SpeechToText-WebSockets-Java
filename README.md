**There is now an official [Java SDK for the Bing Speech to Text API](https://docs.microsoft.com/en-us/azure/cognitive-services/speech/getstarted/getstartedjavaandroid) so this repository is deprecated.**

---

A Java implementation of the [Bing Speech to Text API](https://azure.microsoft.com/en-ca/services/cognitive-services/speech/) [websocket protocol](https://docs.microsoft.com/en-us/azure/cognitive-services/speech/api-reference-rest/websocketprotocol) ([supporting article](https://www.microsoft.com/developerblog/2017/11/01/building-a-custom-spark-connector-for-near-real-time-speech-to-text-transcription/)).

[![Travis CI status](https://api.travis-ci.org/CatalystCode/SpeechToText-WebSockets-Java.svg?branch=master)](https://travis-ci.org/CatalystCode/SpeechToText-WebSockets-Java)

## Usage example ##

Run a demo via:

```sh
# set up all the requisite environment variables
export OXFORD_SPEECH_TOKEN="..."

# stream the audio and transcribe
sbt "runMain SpeechToTextWebsocketsDemo examples/data/batman.wav"
sbt "runMain SpeechToTextWebsocketsDemo examples/data/hall.mp3"
sbt "runMain SpeechToTextWebsocketsDemo http://bbcwssc.ic.llnwd.net/stream/bbcwssc_mp1_ws-einws en-US .mp3"
```

If you're consuming the library via Maven, make sure to also add the Tritonus (PCM audio conversion) jars to the classpath:
- [tritonus_remaining-0.3.6.jar](https://github.com/CatalystCode/SpeechToText-WebSockets-Java/raw/master/lib/tritonus_remaining-0.3.6.jar)
- [tritonus_share-0.3.6.jar](https://github.com/CatalystCode/SpeechToText-WebSockets-Java/raw/master/lib/tritonus_share-0.3.6.jar)

## Release process ##

1. Configure your credentials via the `SONATYPE_USER` and `SONATYPE_PASSWORD` environment variables.
2. Update `version.sbt`
3. Enter the SBT shell: `sbt`
4. Run `sonatypeOpen "enter staging description here"`
5. Run `publishSigned`
6. Run `sonatypeRelease`

## Other implementations ##

- [NodeJS](https://github.com/noopkat/ms-bing-speech-service)
