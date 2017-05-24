A Java implementation of the [Bing Speech to Text API](https://azure.microsoft.com/en-ca/services/cognitive-services/speech/) [websocket protocol](docs.microsoft.com/en-us/azure/cognitive-services/speech/api-reference-rest/websocketprotocol).

[![Travis CI status](https://api.travis-ci.org/CatalystCode/SpeechToText-WebSockets-Java.svg?branch=master)](https://travis-ci.org/CatalystCode/SpeechToText-WebSockets-Java)

## Usage example ##

Run a demo via:

```sh
# set up all the requisite environment variables
export OXFORD_SPEECH_TOKEN="..."

# fetch a sample audio file
curl -O 'https://raw.githubusercontent.com/Azure-Samples/Cognitive-Speech-STT-Windows/master/samples/SpeechRecognitionServiceExample/batman.wav'

# stream the audio and transcribe
mvn exec:java -Dexec.args='batman.wav' -Dexec.mainClass='SpeechToTextWebsocketsDemo'
```
