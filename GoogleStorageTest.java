/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.joao.getsubtitled;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author joao
 */
public class GoogleStorageTest {

  private static String KEY_FILE_LOCATION = "key.json";
  private static String SUBTITLE_FILE_LOCATION = "sample.flac";

    public static void main(String[] args) {
        try {

            StorageOptions.Builder storageOptions = StorageOptions.newBuilder();
            storageOptions.setCredentials(
                    GoogleCredentials.fromStream(new FileInputStream(KEY_FILE_LOCATION)));

            Storage storage = storageOptions.build().getService();

            Path fileLocation = Paths.get(TEST_FILE_LOCATION);
            BlobId blobId = BlobId.of("ggsubtitles", "japanese-audio.flac");
            BlobInfo.Builder blobInfo = BlobInfo.newBuilder(blobId);

            if (Files.size(fileLocation) > 1_000_000) {
                // When content is not available or large (1MB or more) it is recommended
                // to write it in chunks via the blob's channel writer.

                try (WriteChannel writer = storage.writer(blobInfo.build())) {
                    byte[] buffer = new byte[1024];
                    try (InputStream input = Files.newInputStream(fileLocation)) {
                        int limit;
                        while ((limit = input.read(buffer)) >= 0) {
                            try {
                                writer.write(ByteBuffer.wrap(buffer, 0, limit));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                byte[] bytes = Files.readAllBytes(fileLocation);
                // create the blob in one request.
                storage.create(blobInfo.build(), bytes);
            }
            System.out.println("Blob was created");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
