package pl.edu.agh.gem.internal.persistence

import pl.edu.agh.gem.internal.model.GroupAttachment

interface GroupAttachmentRepository {
    fun save(groupAttachment: GroupAttachment): GroupAttachment
    fun getGroupAttachment(attachmentId: String, groupId: String): GroupAttachment
}

class MissingGroupAttachmentException(attachmentId: String, groupId: String) : RuntimeException(
    "Attachment with id:$attachmentId in group:$groupId not found",
)
