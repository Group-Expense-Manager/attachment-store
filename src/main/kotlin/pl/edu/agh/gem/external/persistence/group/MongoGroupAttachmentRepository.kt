package pl.edu.agh.gem.external.persistence.group

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import pl.edu.agh.gem.internal.model.GroupAttachment
import pl.edu.agh.gem.internal.persistence.GroupAttachmentRepository
import pl.edu.agh.gem.internal.persistence.MissingGroupAttachmentException

@Repository
class MongoGroupAttachmentRepository(private val mongo: MongoTemplate) : GroupAttachmentRepository {

    override fun save(groupAttachment: GroupAttachment): GroupAttachment {
        return mongo.save(groupAttachment.toEntity()).toDomain()
    }

    override fun getGroupAttachment(attachmentId: String, groupId: String): GroupAttachment {
        val query = query(where(GroupAttachmentEntity::id.name).isEqualTo(attachmentId).and(GroupAttachmentEntity::groupId.name).isEqualTo(groupId))
        return mongo.findOne(query, GroupAttachmentEntity::class.java)?.toDomain() ?: throw MissingGroupAttachmentException(attachmentId, groupId)
    }
}
