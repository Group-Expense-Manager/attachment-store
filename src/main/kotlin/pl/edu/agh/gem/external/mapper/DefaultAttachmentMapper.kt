package pl.edu.agh.gem.external.mapper

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.CONTENT_LENGTH
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.stereotype.Component
import pl.edu.agh.gem.internal.model.GroupAttachment
import pl.edu.agh.gem.internal.model.UserAttachment

@Component
class DefaultAttachmentMapper : AttachmentMapper {
    override fun mapToResponseEntity(attachment: GroupAttachment): HttpEntity<ByteArray> {
        val headers = prepareHeaders(
            contentType = attachment.contentType,
            sizeInBytes = attachment.sizeInBytes.toString(),
            createdAt = attachment.createdAt.toString(),
            updatedAt = attachment.updatedAt.toString(),
        )
        return HttpEntity(attachment.file.data, headers)
    }

    override fun mapToResponseEntity(attachment: UserAttachment): HttpEntity<ByteArray> {
        val headers = prepareHeaders(
            contentType = attachment.contentType,
            sizeInBytes = attachment.sizeInBytes.toString(),
            createdAt = attachment.createdAt.toString(),
            updatedAt = attachment.updatedAt.toString(),
        )
        return HttpEntity(attachment.file.data, headers)
    }

    private fun prepareHeaders(contentType: String, sizeInBytes: String, createdAt: String, updatedAt: String): HttpHeaders {
        val headers = HttpHeaders()
        headers.set(CONTENT_TYPE, contentType)
        headers.set(CONTENT_LENGTH, sizeInBytes)
        headers.set(CREATED_AT_HEADER, createdAt)
        headers.set(UPDATED_AT_HEADER, updatedAt)
        return headers
    }

    companion object {
        const val CREATED_AT_HEADER = "X-Created-At"
        const val UPDATED_AT_HEADER = "X-Updated-At"
    }
}
