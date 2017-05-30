A Java implementation of the [Bing Speech to Text API](https://azure.microsoft.com/en-ca/services/cognitive-services/speech/) [websocket protocol](docs.microsoft.com/en-us/azure/cognitive-services/speech/api-reference-rest/websocketprotocol).

[![Travis CI status](https://api.travis-ci.org/CatalystCode/SpeechToText-WebSockets-Java.svg?branch=master)](https://travis-ci.org/CatalystCode/SpeechToText-WebSockets-Java)

## Usage example ##

Run a demo via:

```sh
# set up all the requisite environment variables
export OXFORD_SPEECH_TOKEN="..."

# stream the audio and transcribe
sbt "runMain SpeechToTextWebsocketsDemo examples/data/batman.wav"
```
