package pl.edu.agh.gem.external.mapper

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.CONTENT_LENGTH
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.stereotype.Component
import pl.edu.agh.gem.internal.model.GroupAttachment

@Component
class DefaultAttachmentMapper : AttachmentMapper {
    override fun mapToResponseEntity(attachment: GroupAttachment): HttpEntity<ByteArray> {
        val headers = HttpHeaders()
        headers.set(CONTENT_TYPE, attachment.contentType)
        headers.set(CONTENT_LENGTH, attachment.sizeInBytes.toString())
        headers.set(CREATED_AT_HEADER, attachment.createdAt.toString())
        headers.set(UPDATED_AT_HEADER, attachment.updatedAt.toString())
        return HttpEntity(attachment.file.data, headers)
    }

    companion object {
        const val CREATED_AT_HEADER = "X-Created-At"
        const val UPDATED_AT_HEADER = "X-Updated-At"
    }
}
