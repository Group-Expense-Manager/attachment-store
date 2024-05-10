package pl.edu.agh.gem.internal.persistence

import pl.edu.agh.gem.internal.model.GroupAttachment

interface GroupAttachmentRepository {
    fun save(groupAttachment: GroupAttachment): GroupAttachment
}
