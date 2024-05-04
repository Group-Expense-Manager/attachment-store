package pl.edu.agh.gem.external.persistence

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.external.persistence.group.toDomain
import pl.edu.agh.gem.external.persistence.group.toEntity
import pl.edu.agh.gem.util.createGroupAttachment
import pl.edu.agh.gem.util.createGroupAttachmentEntity

class GroupAttachmentEntityTest : ShouldSpec({

    should("map correct GroupAttachmentEntity to GroupAttachment") {
        // given
        val groupAttachment = createGroupAttachment()

        // when
        val groupAttachmentEntity = groupAttachment.toEntity()

        // then
        groupAttachmentEntity.also {
            it.id shouldBe groupAttachment.id
            it.groupId shouldBe groupAttachment.groupId
            it.uploadedByUser shouldBe groupAttachment.uploadedByUser
            it.contentType shouldBe groupAttachment.contentType
            it.sizeInBytes shouldBe groupAttachment.sizeInBytes
            it.file shouldBe groupAttachment.file
            it.createdAt shouldBe groupAttachment.createdAt
            it.updatedAt shouldBe groupAttachment.updatedAt
            it.attachmentHistory shouldBe groupAttachment.attachmentHistory.map { attachmentHistory -> attachmentHistory.toEntity() }
        }
    }

    should("map correct GroupAttachment to GroupAttachmentEntity") {
        // given
        val groupAttachmentEntity = createGroupAttachmentEntity()

        // when
        val groupAttachment = groupAttachmentEntity.toDomain()

        // then
        groupAttachment.also {
            it.id shouldBe groupAttachmentEntity.id
            it.groupId shouldBe groupAttachmentEntity.groupId
            it.uploadedByUser shouldBe groupAttachmentEntity.uploadedByUser
            it.contentType shouldBe groupAttachmentEntity.contentType
            it.sizeInBytes shouldBe groupAttachmentEntity.sizeInBytes
            it.file shouldBe groupAttachmentEntity.file
            it.createdAt shouldBe groupAttachmentEntity.createdAt
            it.updatedAt shouldBe groupAttachmentEntity.updatedAt
            it.attachmentHistory shouldBe groupAttachmentEntity.attachmentHistory.map { attachmentHistory -> attachmentHistory.toDomain() }
        }
    }
},)
