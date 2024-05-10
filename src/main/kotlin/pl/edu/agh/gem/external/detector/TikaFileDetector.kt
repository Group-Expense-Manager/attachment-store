package pl.edu.agh.gem.external.detector

import mu.KotlinLogging
import org.apache.tika.Tika
import org.springframework.stereotype.Component
import pl.edu.agh.gem.external.config.AttachmentProperties
import pl.edu.agh.gem.external.config.AttachmentsLimits
import pl.edu.agh.gem.internal.detector.FileDetector

@Component
class TikaFileDetector(
    private val maxAttachmentSize: AttachmentsLimits,
    private val attachmentProperties: AttachmentProperties,
) : FileDetector {

    private val tika = Tika()

    override fun getFileMediaType(content: ByteArray): String {
        val contentType = tika.detect(content.inputStream())
        logger.info { "Detected content type: $contentType" }
        if (!attachmentProperties.allowedType.contains(contentType)) {
            throw AttachmentContentTypeNotSupportedException(contentType)
        }
        return contentType
    }

    override fun getFileSize(content: ByteArray): Long {
        val size = content.size.toLong()
        logger.info { "Detected file size: $size" }
        if (size > maxAttachmentSize.maxSizeInBytes) {
            throw AttachmentSizeExceededException(size)
        }
        return size
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}

class AttachmentSizeExceededException(size: Long) : RuntimeException("Attachment size:$size exceeds the limit")

class AttachmentContentTypeNotSupportedException(contentType: String) : RuntimeException("Attachment content type:$contentType is not supported")
