package pl.edu.agh.gem.internal.service

import org.bson.types.Binary
import org.springframework.stereotype.Service
import pl.edu.agh.gem.internal.client.GroupManagerClient
import pl.edu.agh.gem.internal.detector.FileDetector
import pl.edu.agh.gem.internal.model.GroupAttachment
import pl.edu.agh.gem.internal.model.createNewAttachmentHistory
import pl.edu.agh.gem.internal.persistence.GroupAttachmentRepository
import pl.edu.agh.gem.model.GroupMembers

@Service
class GroupService(
    private val groupManagerClient: GroupManagerClient,
    private val groupAttachmentRepository: GroupAttachmentRepository,
    private val fileDetector: FileDetector,
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
            attachmentHistory = listOf(createNewAttachmentHistory(userId, size)),
        )
        return groupAttachmentRepository.save(groupAttachment)
    }
}
