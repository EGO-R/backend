package org.mirea.backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@Configuration
class S3Configuration {
    @Value("\${s3.key-id}")
    private lateinit var KEY_ID: String

    @Value("\${s3.secret-key}")
    private lateinit var SECRET_KEY: String

    @Value("\${s3.region}")
    private lateinit var REGION: String

    @Value("\${s3.endpoint}")
    private lateinit var ENDPOINT: String

    private val BUCKET = "spring-boot-s3-example"

    @Bean
    fun s3Client(): S3Client {
        val credentials = AwsBasicCredentials.create(KEY_ID, SECRET_KEY)
        return S3Client.builder()
            .httpClient(ApacheHttpClient.create())
            .region(Region.of(REGION))
            .endpointOverride(URI.create(ENDPOINT))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build()
    }
}