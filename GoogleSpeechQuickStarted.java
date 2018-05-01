/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.joao.getsubtitled;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.grpc.ChannelProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import java.io.FileInputStream;
import java.util.List;

/**
 *
 * @author joao
 */
public class GoogleSpeechQuickStarted {

    private static String KEY_FILE_LOCATION = "key.json";
    private static String SUBTITLE_FILE_LOCATION = "sample.flac";

    public static void main(String... args) throws Exception {
        try {
            //setting credentials (add key to request)
            CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(
                    ServiceAccountCredentials.fromStream(
                            new FileInputStream(KEY_FILE_LOCATION)));

        ChannelProvider channelProvider = SpeechSettings.defaultGrpcChannelProviderBuilder().build();


            SpeechSettings.Builder speechSettings = SpeechSettings.newBuilder();
            speechSettings.setCredentialsProvider(credentialsProvider);
            speechSettings.setTransportProvider(
                    SpeechSettings.defaultGrpcTransportProviderBuilder().setChannelProvider(channelProvider).build());

            // Instantiates a client
            SpeechClient speech = SpeechClient.create(speechSettings.build());

            // Builds the sync recognize request
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(AudioEncoding.FLAC)
                    .setLanguageCode("en-US")
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setUri("gs://ggsubtitles/Untitled.flac")
                    .build();

            // Performs speech recognition on the audio file
            RecognizeResponse response = speech.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s%n", alternative.getTranscript());
            }
            speech.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
