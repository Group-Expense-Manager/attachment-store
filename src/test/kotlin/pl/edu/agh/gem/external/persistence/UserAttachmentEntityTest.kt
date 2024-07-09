package pl.edu.agh.gem.external.persistence

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.external.persistence.user.toDomain
import pl.edu.agh.gem.external.persistence.user.toEntity
import pl.edu.agh.gem.util.createUserAttachment
import pl.edu.agh.gem.util.createUserAttachmentEntity

class UserAttachmentEntityTest : ShouldSpec({

    should("map correct UserAttachment to UserAttachmentEntity") {
        // given
        val groupAttachment = createUserAttachment()

        // when
        val groupAttachmentEntity = groupAttachment.toEntity()

        // then
        groupAttachmentEntity.also {
            it.id shouldBe groupAttachment.id
            it.userId shouldBe groupAttachment.userId
            it.contentType shouldBe groupAttachment.contentType
            it.sizeInBytes shouldBe groupAttachment.sizeInBytes
            it.file shouldBe groupAttachment.file
            it.createdAt shouldBe groupAttachment.createdAt
            it.updatedAt shouldBe groupAttachment.updatedAt
            it.attachmentHistory shouldBe groupAttachment.attachmentHistory.map { attachmentHistory -> attachmentHistory.toEntity() }
        }
    }

    should("map correct UserAttachmentEntity to UserAttachment") {
        // given
        val groupAttachmentEntity = createUserAttachmentEntity()

        // when
        val groupAttachment = groupAttachmentEntity.toDomain()

        // then
        groupAttachment.id shouldBe groupAttachmentEntity.id
        groupAttachment.userId shouldBe groupAttachmentEntity.userId
        groupAttachment.contentType shouldBe groupAttachmentEntity.contentType
        groupAttachment.sizeInBytes shouldBe groupAttachmentEntity.sizeInBytes
        groupAttachment.file shouldBe groupAttachmentEntity.file
        groupAttachment.createdAt shouldBe groupAttachmentEntity.createdAt
        groupAttachment.updatedAt shouldBe groupAttachmentEntity.updatedAt
        groupAttachment.attachmentHistory shouldBe groupAttachmentEntity.attachmentHistory.map { attachmentHistory -> attachmentHistory.toDomain() }
    }
},)
