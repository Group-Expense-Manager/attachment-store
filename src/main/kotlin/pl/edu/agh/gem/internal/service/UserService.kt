package pl.edu.agh.gem.internal.service

import org.bson.types.Binary
import org.springframework.stereotype.Service
import pl.edu.agh.gem.internal.detector.FileDetector
import pl.edu.agh.gem.internal.loader.FileLoader
import pl.edu.agh.gem.internal.model.UserAttachment
import pl.edu.agh.gem.internal.model.createNewAttachmentHistory
import pl.edu.agh.gem.internal.persistence.UserAttachmentRepository
import java.time.Instant.now

@Service
class UserService(
    private val userAttachmentRepository: UserAttachmentRepository,
    private val fileDetector: FileDetector,
    private val fileLoader: FileLoader,
) {
    fun saveAttachment(
        data: ByteArray,
        userId: String,
    ): UserAttachment {
        val size = fileDetector.getFileSize(data)
        val contentType = fileDetector.getFileMediaType(data)
        val groupAttachment =
            UserAttachment(
                userId = userId,
                contentType = contentType,
                sizeInBytes = size,
                file = Binary(data),
                attachmentHistory = listOf(createNewAttachmentHistory(userId, size, contentType)),
            )
        return userAttachmentRepository.save(groupAttachment)
    }

    fun updateAttachment(
        data: ByteArray,
        attachmentId: String,
        userId: String,
    ): UserAttachment {
        val attachment = getAttachment(userId, attachmentId)
        val size = fileDetector.getFileSize(data)
        val contentType = fileDetector.getFileMediaType(data)

        val updatedAttachment =
            attachment.copy(
                file = Binary(data),
                sizeInBytes = size,
                contentType = contentType,
                updatedAt = now(),
                attachmentHistory = attachment.attachmentHistory + createNewAttachmentHistory(userId, size, contentType),
            )

        return userAttachmentRepository.save(updatedAttachment)
    }

    fun getAttachment(
        userId: String,
        attachmentId: String,
    ): UserAttachment {
        return userAttachmentRepository.getUserAttachment(attachmentId, userId)
    }

    fun generateUserImage(userId: String): UserAttachment {
        val generateImage = fileLoader.loadRandomUserImage()
        return saveAttachment(generateImage, userId)
    }
}
