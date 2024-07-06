package pl.edu.agh.gem.internal.service

import org.bson.types.Binary
import org.springframework.stereotype.Service
import pl.edu.agh.gem.internal.client.GroupManagerClient
import pl.edu.agh.gem.internal.detector.FileDetector
import pl.edu.agh.gem.internal.loader.FileLoader
import pl.edu.agh.gem.internal.model.GroupAttachment
import pl.edu.agh.gem.internal.model.createNewAttachmentHistory
import pl.edu.agh.gem.internal.persistence.GroupAttachmentRepository
import pl.edu.agh.gem.model.GroupMembers
import java.time.Instant.now

@Service
class GroupService(
    private val groupManagerClient: GroupManagerClient,
    private val groupAttachmentRepository: GroupAttachmentRepository,
    private val fileDetector: FileDetector,
    private val fileLoader: FileLoader,
) {
    fun getGroupMembers(groupId: String): GroupMembers {
        return groupManagerClient.getGroupMembers(groupId)
    }
    fun saveAttachment(data: ByteArray, groupId: String, userId: String): GroupAttachment {
        val size = fileDetector.getFileSize(data)
        val contentType = fileDetector.getFileMediaType(data)
        val groupAttachment = GroupAttachment(
            groupId = groupId,
            uploadedByUser = userId,
            contentType = contentType,
            sizeInBytes = size,
            file = Binary(data),
            attachmentHistory = listOf(createNewAttachmentHistory(userId, size, contentType)),
        )
        return groupAttachmentRepository.save(groupAttachment)
    }

    fun updateAttachment(data: ByteArray, attachmentId: String, groupId: String, userId: String): GroupAttachment {
        val attachment = getAttachment(groupId, attachmentId)
        val size = fileDetector.getFileSize(data)
        val contentType = fileDetector.getFileMediaType(data)

        val updatedAttachment = attachment.copy(
            file = Binary(data),
            uploadedByUser = userId,
            sizeInBytes = size,
            contentType = contentType,
            updatedAt = now(),
            attachmentHistory = attachment.attachmentHistory + createNewAttachmentHistory(userId, size, contentType),
        )

        return groupAttachmentRepository.save(updatedAttachment)
    }

    fun getAttachment(groupId: String, attachmentId: String): GroupAttachment {
        return groupAttachmentRepository.getGroupAttachment(attachmentId, groupId)
    }

    fun generateGroupImage(groupId: String, userId: String): GroupAttachment {
        val generateImage = fileLoader.loadRandomGroupImage()
        return saveAttachment(generateImage, groupId, userId)
    }
}
