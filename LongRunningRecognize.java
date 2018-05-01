/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.joao.getsubtitled;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.grpc.ChannelProvider;
import com.google.api.gax.rpc.OperationFuture;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.longrunning.Operation;
import java.io.FileInputStream;
import java.util.List;

/**
 *
 * @author joao
 */
public class LongRunningRecognize {

    private static String KEY_FILE_LOCATION = "key.json";

    public static void main(String[] args) {
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

            // Configure remote file request for Linear16
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(AudioEncoding.FLAC)
                    .setLanguageCode("en-US")
                    .setSampleRateHertz(44100)
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setUri("gs://ggsubtitles/file.flac")
                    .build();

            // Use non-blocking call for getting file transcription
            OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata, Operation> response
                    = speech.longRunningRecognizeAsync(config, audio);

            while (!response.isDone()) {
                System.out.println("Waiting for response...");
                Thread.sleep(10000);
            }

            List<SpeechRecognitionResult> results = response.get().getResultsList();

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s\n", alternative.getTranscript());
            }
            speech.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
