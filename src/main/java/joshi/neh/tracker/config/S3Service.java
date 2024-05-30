package joshi.neh.tracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;

    public S3Service(
            @Value("${cloud.aws.credentials.accessKey}") String accessKey,
            @Value("${cloud.aws.credentials.secretKey}") String secretKey,
            @Value("${cloud.aws.region.static}") String region,
            @Value("${cloud.aws.s3.bucket.name}") String bucketName
    ) {
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
        this.bucketName = bucketName;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String key = Instant.now().toEpochMilli() + "_" + file.getOriginalFilename();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        return key;
    }

    public Optional<byte[]> getImageFromS3(String key) {
        byte[] imageBytes = new byte[0];
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            InputStream inputStream = s3Client.getObject(getObjectRequest);
            imageBytes = inputStream.readAllBytes();
//            inputStream.close();
            return Optional.of(imageBytes);
        }
        catch (IOException ignored) {

            return Optional.empty();
        }
    }



}
