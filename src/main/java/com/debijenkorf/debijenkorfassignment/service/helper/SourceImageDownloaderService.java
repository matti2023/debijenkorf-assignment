package com.debijenkorf.debijenkorfassignment.service.helper;

import com.debijenkorf.debijenkorfassignment.exception.GenericURLException;
import com.debijenkorf.debijenkorfassignment.exception.ImageNotFoundException;
import com.debijenkorf.debijenkorfassignment.exception.SourceURLException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SourceImageDownloaderService {

    @Value("${source-root-url}")
    private String sourceRootUrl;

    public byte[] downloadOriginalImage(String reference) {
        String sourceImageUrl = sourceRootUrl + reference;
        try {
            URL url = new URL(sourceImageUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return outputStream.toByteArray();
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                throw new ImageNotFoundException("The requested source image does not exist");
            } else {
                throw new SourceURLException("The source url is down, or responds with an error code");
            }
        } catch (Exception e) {
            throw new GenericURLException(e.getMessage());
        }
    }
}
