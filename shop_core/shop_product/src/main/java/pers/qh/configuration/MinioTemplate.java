package pers.qh.configuration;

import io.minio.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Configuration
@EnableConfigurationProperties(pers.qh.configuration.MinioProperties.class)
public class MinioTemplate {
    @Autowired
    private pers.qh.configuration.MinioProperties minioProperties;
    @Autowired
    private MinioClient minioClient;

    @Bean
    public MinioClient makeBucket() throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
        boolean flag = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .build()
        );
        if (!flag) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .build()
            );
        }
        return minioClient;
    }

    public String upload(MultipartFile file) throws Exception {
        String filePath = new DateTime().toString("yyyy/MM/dd/");
        String fileName = UUID.randomUUID().toString().replace("-", "");
        String fileSuffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        String fileDir = filePath + fileName + "." + fileSuffix;
        InputStream inputStream = file.getInputStream();
        String contentType = file.getContentType();

        String url = "%s/%s/%s";
        url = String.format(url, minioProperties.getEndpoint(), minioProperties.getBucketName(), fileDir);

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(fileDir)
                        .stream(inputStream, inputStream.available(), -1)
                        .contentType(contentType)
                        .build()
        );
        return url;
    }

    public void delete(String bucketName, String fileDir) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileDir)
                        .build()
        );
    }

}

