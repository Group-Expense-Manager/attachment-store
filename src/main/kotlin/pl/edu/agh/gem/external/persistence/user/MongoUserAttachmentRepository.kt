package pl.edu.agh.gem.external.persistence.user

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import pl.edu.agh.gem.internal.model.UserAttachment
import pl.edu.agh.gem.internal.persistence.MissingUserAttachmentException
import pl.edu.agh.gem.internal.persistence.UserAttachmentRepository

@Repository
class MongoUserAttachmentRepository(private val mongo: MongoTemplate) : UserAttachmentRepository {

    override fun save(userAttachment: UserAttachment): UserAttachment {
        return mongo.save(userAttachment.toEntity()).toDomain()
    }

    override fun getUserAttachment(attachmentId: String, userId: String): UserAttachment {
        val query = query(where(UserAttachmentEntity::id.name).isEqualTo(attachmentId).and(UserAttachmentEntity::userId.name).isEqualTo(userId))
        return mongo.findOne(query, UserAttachmentEntity::class.java)?.toDomain() ?: throw MissingUserAttachmentException(attachmentId, userId)
    }
}
