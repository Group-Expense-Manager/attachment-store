package pl.edu.agh.gem.external.mapper

import org.springframework.http.HttpEntity
import pl.edu.agh.gem.internal.model.GroupAttachment
import pl.edu.agh.gem.internal.model.UserAttachment

interface AttachmentMapper {
    fun mapToResponseEntity(attachment: GroupAttachment): HttpEntity<ByteArray>
    fun mapToResponseEntity(attachment: UserAttachment): HttpEntity<ByteArray>
}
