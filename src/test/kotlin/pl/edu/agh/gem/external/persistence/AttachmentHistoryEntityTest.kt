package pl.edu.agh.gem.external.persistence

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.util.createAttachmentHistory
import pl.edu.agh.gem.util.createAttachmentHistoryEntity

class AttachmentHistoryEntityTest : ShouldSpec({

    should("map correct GroupAttachmentEntity to GroupAttachment") {
        // given
        val attachmentHistory = createAttachmentHistory()

        // when
        val attachmentHistoryEntity = attachmentHistory.toEntity()

        // then
        attachmentHistoryEntity.also {
            it.updatedBy shouldBe attachmentHistory.updatedBy
            it.updatedAt shouldBe attachmentHistory.updatedAt
            it.sizeInBytes shouldBe attachmentHistory.sizeInBytes
        }
    }

    should("map correct GroupAttachment to GroupAttachmentEntity") {
        // given
        val attachmentHistoryEntity = createAttachmentHistoryEntity()

        // when
        val attachmentHistory = attachmentHistoryEntity.toDomain()

        // then
        attachmentHistory.also {
            it.updatedBy shouldBe attachmentHistoryEntity.updatedBy
            it.updatedAt shouldBe attachmentHistoryEntity.updatedAt
            it.sizeInBytes shouldBe attachmentHistoryEntity.sizeInBytes
        }
    }
})
