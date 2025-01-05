package pl.edu.agh.gem.integration.persistence

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.internal.persistence.GroupAttachmentRepository
import pl.edu.agh.gem.internal.persistence.MissingGroupAttachmentException
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

        should("get group attachment") {
            // given
            val groupAttachment = createGroupAttachment()
            groupAttachmentRepository.save(groupAttachment)

            // when
            val groupAttachmentResult = groupAttachmentRepository.getGroupAttachment(groupAttachment.id, groupAttachment.groupId)

            // then
            groupAttachmentResult shouldBe groupAttachment
        }

        should("throw MissingGroupAttachmentException when attachment not found") {
            // given
            val groupAttachment = createGroupAttachment()

            // when & then
            shouldThrow<MissingGroupAttachmentException> {
                groupAttachmentRepository.getGroupAttachment(groupAttachment.id, groupAttachment.groupId)
            }
        }
    })
