package pl.edu.agh.gem.external.persistence.group

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository
import pl.edu.agh.gem.internal.model.GroupAttachment
import pl.edu.agh.gem.internal.persistence.GroupAttachmentRepository

@Repository
class MongoGroupAttachmentRepository(private val mongo: MongoTemplate) : GroupAttachmentRepository {

    override fun save(groupAttachment: GroupAttachment): GroupAttachment {
        return mongo.save(groupAttachment.toEntity()).toDomain()
    }
}
