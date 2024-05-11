package pl.edu.agh.gem.external.mapper

import org.springframework.http.HttpEntity
import pl.edu.agh.gem.internal.model.GroupAttachment

interface AttachmentMapper {
    fun mapToResponseEntity(attachment: GroupAttachment): HttpEntity<ByteArray>
}
