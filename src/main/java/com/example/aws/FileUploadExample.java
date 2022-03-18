package com.example.aws;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import java.io.File;
import java.io.IOException;

public class FileUploadExample {

  public static void main(String[] args) throws IOException {

    String bucketName = "korzinka-008";
    String folderName = "photos";
    Region region = Region.US_WEST_2;

    String fileName = "logo.png";
    String filePath = "/Users/zhursind/Desktop/mylogo.png";
    String key = folderName + "/" + fileName;

    S3Client client = S3Client.builder().region(region).build();

    client.createBucket(CreateBucketRequest
        .builder()
        .bucket(bucketName)
        .createBucketConfiguration(
            CreateBucketConfiguration.builder()
                .locationConstraint(region.id())
                .build())
        .build());

    PutObjectRequest request = PutObjectRequest.builder()
                                                .bucket(bucketName)
                                                .key(key)
                                                .acl("public-read")
                                                .build();

    client.putObject(request, RequestBody.fromFile(new File(filePath)));

    S3Waiter waiter = client.waiter();
    HeadObjectRequest requestWait = HeadObjectRequest.builder().bucket(bucketName).key(key).build();

    WaiterResponse<HeadObjectResponse> waiterResponse =waiter.waitUntilObjectExists(requestWait);

    waiterResponse.matched().response().ifPresent(System.out::println);

    System.out.println("File " + fileName + " was uploaded.");

  }

}
