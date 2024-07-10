package pl.edu.agh.gem.integration.mapper

import io.kotest.matchers.shouldBe
import org.springframework.http.HttpHeaders.CONTENT_LENGTH
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import pl.edu.agh.gem.external.mapper.AttachmentMapper
import pl.edu.agh.gem.external.mapper.DefaultAttachmentMapper.Companion.CREATED_AT_HEADER
import pl.edu.agh.gem.external.mapper.DefaultAttachmentMapper.Companion.UPDATED_AT_HEADER
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.util.createGroupAttachment
import pl.edu.agh.gem.util.createUserAttachment

class AttachmentMapperIT(
    private val attachmentMapper: AttachmentMapper,
) : BaseIntegrationSpec({
    should("map GroupAttachment to ResponseEntity") {
        // given
        val attachment = createGroupAttachment()

        // when
        val response = attachmentMapper.mapToResponseEntity(attachment)

        // then
        response.body shouldBe attachment.file.data
        response.headers.getFirst(CONTENT_TYPE) shouldBe attachment.contentType
        response.headers.getFirst(CONTENT_LENGTH) shouldBe attachment.sizeInBytes.toString()
        response.headers.getFirst(CREATED_AT_HEADER) shouldBe attachment.createdAt.toString()
        response.headers.getFirst(UPDATED_AT_HEADER) shouldBe attachment.updatedAt.toString()
    }

    should("map UserAttachment to ResponseEntity") {
        // given
        val attachment = createUserAttachment()

        // when
        val response = attachmentMapper.mapToResponseEntity(attachment)

        // then
        response.body shouldBe attachment.file.data
        response.headers.getFirst(CONTENT_TYPE) shouldBe attachment.contentType
        response.headers.getFirst(CONTENT_LENGTH) shouldBe attachment.sizeInBytes.toString()
        response.headers.getFirst(CREATED_AT_HEADER) shouldBe attachment.createdAt.toString()
        response.headers.getFirst(UPDATED_AT_HEADER) shouldBe attachment.updatedAt.toString()
    }
},)
