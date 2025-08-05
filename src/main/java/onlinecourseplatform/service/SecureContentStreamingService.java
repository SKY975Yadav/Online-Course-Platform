package onlinecourseplatform.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

@Service
@Slf4j
public class SecureContentStreamingService {

    @Autowired
    private CloudUrlProcessorService cloudUrlProcessor;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Streams video content from a cloud URL, ensuring the content is served with appropriate headers
     */
    public ResponseEntity<Resource> streamVideoContent(String cloudUrl, String filename) {
        try {
            String directUrl = cloudUrlProcessor.getDirectDownloadUrl(cloudUrl);
            URL url = URI.create(directUrl).toURL();
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            InputStream inputStream = connection.getInputStream();
            InputStreamResource resource = new InputStreamResource(inputStream);

            log.info("Streaming video from URL: {}", directUrl);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .cacheControl(CacheControl.noCache().mustRevalidate())
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .body(resource);

        } catch (Exception e) {
            log.error("Failed to stream video: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Streams document content from a cloud URL, ensuring the content is served with appropriate headers
     */
    public ResponseEntity<Resource> streamDocumentContent(String cloudUrl, String filename) {
        try {
            String directUrl = cloudUrlProcessor.getDirectDownloadUrl(cloudUrl);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    directUrl,
                    HttpMethod.GET,
                    createHttpEntity(),
                    byte[].class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(response.getBody());
                InputStreamResource resource = new InputStreamResource(inputStream);

                log.info("Streaming document from URL: {}", directUrl);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .cacheControl(CacheControl.noCache().mustRevalidate())
                        .header(HttpHeaders.PRAGMA, "no-cache")
                        .header(HttpHeaders.EXPIRES, "0")
                        .body(resource);
            }

            log.warn("Document not found or could not be downloaded: {}", directUrl);
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("Failed to stream document: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Creates a HttpEntity with default headers for REST requests
     */
    private HttpEntity<String> createHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0");
        return new HttpEntity<>(headers);
    }
}
