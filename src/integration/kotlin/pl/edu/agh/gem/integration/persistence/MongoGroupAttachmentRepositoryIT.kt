package pl.edu.agh.gem.integration.persistence

import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.internal.persistence.GroupAttachmentRepository
import pl.edu.agh.gem.util.createGroupAttachment

class MongoGroupAttachmentRepositoryIT(
    private val groupAttachmentRepository: GroupAttachmentRepository,
) : BaseIntegrationSpec({
    should("save group attachment") {
        // given
        val groupAttachment = createGroupAttachment()

        // when
        val groupAttachmentResult = groupAttachmentRepository.save(groupAttachment)

        // then
        groupAttachmentResult shouldBe groupAttachment
    }
},)
