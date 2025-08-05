// 4. Cloud URL Processor Service
package onlinecourseplatform.service;

import onlinecourseplatform.entity.CloudProvider;
import org.springframework.stereotype.Service;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CloudUrlProcessorService {

    /**
     * Processes a Google Drive URL to convert it into a direct download link.
     */
    public String processGoogleDriveUrl(String originalUrl) {
        // Convert Google Drive share URL to direct download URL
        // Original: https://drive.google.com/file/d/FILE_ID/view?usp=sharing
        // Direct: https://drive.google.com/uc?export=download&id=FILE_ID

        Pattern pattern = Pattern.compile("https://drive\\.google\\.com/file/d/([a-zA-Z0-9_-]+)");
        Matcher matcher = pattern.matcher(originalUrl);

        if (matcher.find()) {
            String fileId = matcher.group(1);
            return "https://drive.google.com/uc?export=download&id=" + fileId;
        }
        return originalUrl;
    }

    /**
     * Processes a Dropbox URL to convert it into a direct download link.
     */
    public String processDropboxUrl(String originalUrl) {
        // Convert Dropbox share URL to direct download URL
        // Original: https://www.dropbox.com/s/FILE_ID/filename?dl=0
        // Direct: https://www.dropbox.com/s/FILE_ID/filename?dl=1

        if (originalUrl.contains("dropbox.com") && originalUrl.contains("dl=0")) {
            return originalUrl.replace("dl=0", "dl=1");
        }

        return originalUrl;
    }

    /**
     * Detects the cloud provider based on the URL.
     */
    public CloudProvider detectCloudProvider(String url) {
        if (url.contains("drive.google.com")) {
            return CloudProvider.GOOGLE_DRIVE;
        } else if (url.contains("dropbox.com")) {
            return CloudProvider.DROPBOX;
        } else {
            return CloudProvider.OTHER;
        }
    }

    /**
     * helper method get to a direct download URL.
     */
    public String getDirectDownloadUrl(String originalUrl) {
        CloudProvider provider = detectCloudProvider(originalUrl);

        return switch (provider) {
            case GOOGLE_DRIVE -> processGoogleDriveUrl(originalUrl);
            case DROPBOX -> processDropboxUrl(originalUrl);
            default -> originalUrl;
        };
    }
}