package team3.cashvault.services;

import java.io.IOException;
import java.io.InputStream;
import com.google.cloud.storage.Blob;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;

@Service
public class FileUploadService {

    public String uploadFile(String folderName, String fileName, MultipartFile file) throws IOException {
        // Get the content type (MIME type) of the file
        String contentType = file.getContentType();
        BlobInfo blobInfo = BlobInfo.newBuilder(getBucket("cashvault-bucket"), folderName + "/" + fileName)
                                    .setContentType(contentType)
                                    .setCacheControl("public, max-age=0")
                                    .build();
        
        // Get the byte array of the file content
        byte[] fileBytes = file.getBytes();
        uploadBlob(blobInfo, fileBytes);
        // Return the public URL of the uploaded blob
        return getPublicUrl(blobInfo.getBucket(), blobInfo.getName());
    }

    private Blob uploadBlob(BlobInfo blobInfo, byte[] fileBytes) throws IOException {
        Storage storage = StorageOptions.newBuilder().setCredentials(getCredentials()).build().getService();
        return storage.create(blobInfo, fileBytes);
    }

   private Bucket getBucket(String bucketName) throws IOException {
    Storage storage = StorageOptions.newBuilder().setCredentials(getCredentials()).build().getService();
    Bucket bucket = storage.get(bucketName);
    if (bucket == null) {
        throw new IOException("Bucket not found: " + bucketName);
    }
    return bucket;
}
private String getPublicUrl(String bucketName, String objectName) {
    return String.format("https://storage.googleapis.com/%s/%s", bucketName, objectName);
}

private Credentials getCredentials() throws IOException {
    InputStream credentialsStream = getClass().getClassLoader().getResourceAsStream("storage-key.json");
    if (credentialsStream == null) {
        throw new IOException("Credentials file not found");
    }
    return GoogleCredentials.fromStream(credentialsStream)
            .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
}

}
