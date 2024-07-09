package pl.edu.agh.gem.internal.persistence

import pl.edu.agh.gem.internal.model.UserAttachment

interface UserAttachmentRepository {
    fun save(userAttachment: UserAttachment): UserAttachment
    fun getUserAttachment(attachmentId: String, userId: String): UserAttachment
}

class MissingUserAttachmentException(attachmentId: String, userId: String) : RuntimeException(
    "Attachment with id: $attachmentId for userId: $userId not found",
)
