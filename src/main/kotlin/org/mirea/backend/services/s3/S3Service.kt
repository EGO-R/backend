package org.mirea.backend.services.s3

import kotlinx.coroutines.*
import org.mirea.backend.utils.ids.UserID
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

@Service
class S3Service(
    private val s3Client: S3Client,
    private val presigner: S3Presigner,
) {
    @Value("\${s3.bucket}")
    private lateinit var BUCKET: String

    @Value("\${s3.temp-bucket}")
    private lateinit var TEMP_BUCKET: String

    @Value("\${s3.endpoint}")
    private lateinit var ENDPOINT: String

    private val random = Random()

    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        const val VIDEO_FILE_EXTENSION = ".mp4"

        const val BYTE_BUFFER_SIZE = 50 * 1024 * 1024

        private val presignedUrlLife = Duration.ofMinutes(5)
    }

    suspend fun generatePreSignedVideoUrl(): String = withContext(Dispatchers.IO) {
        val fileName = (LocalDateTime.now().nano + random.nextInt()).hashCode().toString() + VIDEO_FILE_EXTENSION
        val path = "videos/1/$fileName"

        val request = PutObjectPresignRequest.builder()
            .signatureDuration(presignedUrlLife)
            .putObjectRequest { putObjectRequest ->
                putObjectRequest
                    .bucket(BUCKET)
                    .key(path)
                    .contentType("video/mp4")
                    .build()
            }
            .build()

        presigner.presignPutObject(request).url().toString()
    }

    suspend fun uploadPreview(file: MultipartFile): String = withContext(Dispatchers.IO) {
        val userId = UserID(1)
        logger.info("File name: ${file.originalFilename}")
        val fileExtension = file.originalFilename?.substringAfterLast('.') ?: throw RuntimeException("no original filename")
        val fileName = (LocalDateTime.now().nano + random.nextInt()).hashCode().toString() + ".$fileExtension"
        val path = "previews/${userId.value}/$fileName"

        val request = PutObjectRequest.builder()
            .bucket(BUCKET)
            .key(path)
            .build()

        s3Client.putObject(request, RequestBody.fromInputStream(file.inputStream, file.size))
        path
    }

    suspend fun deleteFile(key: String) = withContext(Dispatchers.IO) {
        val query = DeleteObjectRequest.builder()
            .bucket(BUCKET)
            .key(key)
            .build()

        s3Client.deleteObject(query)
    }

    suspend fun uploadVideo(file: MultipartFile): String {
        val fileName = (LocalDateTime.now().nano + random.nextInt()).hashCode().toString() + VIDEO_FILE_EXTENSION
        val path = "videos/1/$fileName"
        readAndSendFile(
            bucket = BUCKET,
            key = path,
            file = file,
        )
        logger.info("Exiting s3 service")
        return path
    }

    private suspend fun readAndSendFile(bucket: String, key: String, file: MultipartFile): Unit =
        withContext(Dispatchers.IO) {

            try {
                val uploadId = startUpload(
                    bucket = bucket,
                    key = key,
                )
                val uploadResponses = uploadParts(
                    uploadId = uploadId,
                    bucket = bucket,
                    key = key,
                    file = file,
                )
                finishUploading(
                    bucket = bucket,
                    key = key,
                    uploadId = uploadId,
                    uploadResponses = uploadResponses,
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private suspend fun startUpload(bucket: String, key: String) = withContext(Dispatchers.IO) {
        val createRequest = CreateMultipartUploadRequest.builder()
            .bucket(bucket)
            .key(key)
            .build()

        s3Client.createMultipartUpload(createRequest).uploadId()
    }

    private suspend fun uploadParts(uploadId: String, bucket: String, key: String, file: MultipartFile) =
        withContext(Dispatchers.IO) {
            var position = 0L
            val inputStream = file.inputStream

            var partNumber = 1
            val uploadResponses = mutableListOf<Deferred<UploadPartResponse>>()

            while (position < file.size) {
                val buffer = ByteArray(BYTE_BUFFER_SIZE)
                val bytesRead = inputStream.read(buffer)

                logger.info("Bytes read: $bytesRead / ${file.size}")
                logger.info("Position: $position / ${file.size}")

                val uploadRequest = UploadPartRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .uploadId(uploadId)
                    .partNumber(partNumber)
                    .contentLength(bytesRead.toLong())
                    .build()

                uploadResponses.add(
                    async {
                        s3Client.uploadPart(uploadRequest, RequestBody.fromBytes(buffer))
                    }
                )
                logger.info("Uploaded $partNumber")
                partNumber++
                position += bytesRead
            }

            uploadResponses
        }

    private suspend fun finishUploading(
        bucket: String,
        key: String,
        uploadId: String,
        uploadResponses: List<Deferred<UploadPartResponse>>
    ): Unit = withContext(Dispatchers.IO) {
        var partNumber = 1

        val completedParts = uploadResponses.map { it.await() }.map { response ->
            CompletedPart.builder()
                .partNumber(partNumber++)
                .eTag(response.eTag())
                .build()
        }

        val completedMultipartUploads = CompletedMultipartUpload.builder()
            .parts(completedParts)
            .build()

        val completeRequest = CompleteMultipartUploadRequest.builder()
            .bucket(bucket)
            .key(key)
            .uploadId(uploadId)
            .multipartUpload(completedMultipartUploads)
            .build()

        s3Client.completeMultipartUpload(completeRequest)
    }

    private suspend fun uploadParts1(bucket: String, key: String, chunks: List<ByteArray>): Unit = supervisorScope {
        val createRequest = CreateMultipartUploadRequest.builder()
            .bucket(bucket)
            .key(key)
            .build()

        val createResponse = s3Client.createMultipartUpload(createRequest)

        val uploadId = createResponse.uploadId()
        var partNumber = 1
        val uploadResponses = mutableListOf<Deferred<UploadPartResponse>>()

        for (chunk in chunks) {
            val uploadRequest = UploadPartRequest.builder()
                .bucket(bucket)
                .key(key)
                .uploadId(uploadId)
                .partNumber(partNumber)
                .contentLength(chunk.size.toLong())
                .build()

            uploadResponses.add(
                async {
                    s3Client.uploadPart(uploadRequest, RequestBody.fromBytes(chunk))
                }
            )
            logger.info("Uploaded $partNumber")
            partNumber++
        }
        partNumber = 1
        delay(10000)

        val completedParts = uploadResponses.map { it.await() }.map { response ->
            CompletedPart.builder()
                .partNumber(partNumber++)
                .eTag(response.eTag())
                .build()
        }

        val completedMultipartUploads = CompletedMultipartUpload.builder()
            .parts(completedParts)
            .build()

        val completeRequest = CompleteMultipartUploadRequest.builder()
            .bucket(bucket)
            .key(key)
            .uploadId(uploadId)
            .multipartUpload(completedMultipartUploads)
            .build()

        s3Client.completeMultipartUpload(completeRequest)

        logger.info("Uploaded file to $key")
    }

    private suspend fun readAndSendFile1(bucket: String, key: String, file: MultipartFile): Unit =
        withContext(Dispatchers.IO) {
            logger.info("Reading $key started")

            val chunks = mutableListOf<ByteArray>()
            var position = 0
            val inputStream = file.inputStream

            while (position < file.size) {
                val buffer = ByteArray(BYTE_BUFFER_SIZE)
                val len = inputStream.read(buffer)
                chunks.add(buffer)
                position += len
            }

            logger.info("Reading $key ended")

            try {
//                launch { uploadParts(bucket, key, chunks) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
}