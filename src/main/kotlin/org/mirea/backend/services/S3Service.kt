package org.mirea.backend.services

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CompletedPart
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.UploadPartRequest
import java.io.InputStream
import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.util.*

@Service
class S3Service(
    private val s3Client: S3Client,
) {
    @Value("\${s3.bucket}")
    private lateinit var BUCKET: String

    @Value("\${s3.temp-bucket}")
    private lateinit var TEMP_BUCKET: String

    private val random = Random()

    companion object {
        const val VIDEO_FILE_EXTENSION = ".mp4"

        const val BYTE_BUFFER_SIZE = 5 * 1024 * 1024
    }

    suspend fun uploadVideo(file: MultipartFile): String {
        val fileName = (LocalDateTime.now().nano + random.nextInt()).hashCode().toString() + VIDEO_FILE_EXTENSION
        val path = "videos/1/$fileName"
        return upload(path, file)
    }

    private suspend fun upload(key:String, file: MultipartFile): String {
        val request = PutObjectRequest.builder()
            .bucket(BUCKET)
            .key(key)
            .contentType(file.contentType)
            .build()


        s3Client.putObject(request, RequestBody.fromInputStream(file.inputStream, file.size))
        return "$BUCKET/$key"
    }

    private suspend fun upload1(key:String, file: MultipartFile): String = coroutineScope() {
        var partNumber = 1
        var position = 0
        val buffer = ByteArray(BYTE_BUFFER_SIZE)
        val inputStream = file.inputStream
        while (position < file.size) {
            val len = inputStream.read(buffer)
            position += len

            val request = UploadPartRequest.builder()
                .bucket(BUCKET)
                .key(key)
                .partNumber(partNumber)
                .build()

            async(Dispatchers.IO) { s3Client.uploadPart(request, RequestBody.fromBytes(buffer)) }
            partNumber++
        }


        return "$BUCKET/$key"
    }

//    suspend fun uploadBigFile(key: String, file: MultipartFile): String {
//        // 1) Создаём "multipart upload" и получаем uploadId
//        val createResponse = s3Client.createMultipartUpload { builder ->
//            builder.bucket(BUCKET)
//            builder.key(key)
//        }
//        val uploadId = createResponse.uploadId()
//            ?: throw RuntimeException("No uploadId returned")
//
//        // Сюда будем собирать инфо о корутинах и частях
//        val partUploadJobs = mutableListOf<Deferred<CompletedPart>>()
//
//        val inputStream = file.inputStream
//        var position = 0L
//        var partNumber = 1
//
//        val buffer = ByteArray(BYTE_BUFFER_SIZE)
//
//        while (position < file.size) {
//            val bytesRead = inputStream.read(buffer)
//            if (bytesRead < 0) break
//
//            position += bytesRead
//            if (bytesRead == 0) continue
//
//            // Копируем кусок, чтобы не перезаписать тот же массив в следующих итерациях
//            val chunkBytes = buffer.copyOf(bytesRead)
//
//            // Запускаем асинхронную корутину на applicationScope
//            val job = applicationScope.async(Dispatchers.IO) {
//                // Загружаем часть
//                val uploadPartResponse = s3Client.uploadPart { reqBuilder ->
//                    reqBuilder.bucket(BUCKET)
//                        .key(key)
//                        .uploadId(uploadId)
//                        .partNumber(partNumber)
//                }.also { requestBody ->
//                    // Передаём тело через RequestBody
//                    requestBody.requestBody(RequestBody.fromBytes(chunkBytes))
//                }
//
//                // Возвращаем инфо о загруженной части: partNumber + etag
//                CompletedPart.builder()
//                    .partNumber(partNumber)
//                    .eTag(uploadPartResponse.eTag())
//                    .build()
//            }
//
//            partUploadJobs += job
//            partNumber++
//        }
//
//        // 2) Все части запущены. Мы не ждём их окончания прямо сейчас,
//        //    но хотим «в будущем» их как-то собрать.
//        //    В данном случае — отправим в очередь задачу: «довести Upload до конца»
//
//        // Собираем «метаданные» для будущей фоновой задачи
//        // Например, Id, key, общее кол-во частей
//        val taskMeta = UploadFinalizeTask(
//            uploadId = uploadId,
//            key = key,
//            totalPartsCount = partNumber - 1
//        )
//
//        // Отправляем это в очередь (пусть какая-то фоновая служба потом примет и завершит MultipartUpload)
//        queueProducer.send(taskMeta)
//
//        // 3) Возвращаем ответ сразу
//        return "Started upload to s3://$BUCKET/$key (uploadId=$uploadId) - parts uploading asynchronously."
//    }
}