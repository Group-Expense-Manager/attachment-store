package pl.edu.agh.gem.integration.persistence

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.internal.persistence.MissingUserAttachmentException
import pl.edu.agh.gem.internal.persistence.UserAttachmentRepository
import pl.edu.agh.gem.util.createUserAttachment

class MongoUserAttachmentRepositoryIT(
    private val userAttachmentRepository: UserAttachmentRepository,
) : BaseIntegrationSpec({
    should("save user attachment") {
        // given
        val userAttachment = createUserAttachment()

        // when
        val userAttachmentResult = userAttachmentRepository.save(userAttachment)

        // then
        userAttachmentResult shouldBe userAttachment
    }

    should("get group attachment") {
        // given
        val userAttachment = createUserAttachment()
        userAttachmentRepository.save(userAttachment)

        // when
        val userAttachmentResult = userAttachmentRepository.getUserAttachment(userAttachment.id, userAttachment.userId)

        // then
        userAttachmentResult shouldBe userAttachment
    }

    should("throw MissingGroupAttachmentException when attachment not found") {
        // given
        val userAttachment = createUserAttachment()

        // when & then
        shouldThrow<MissingUserAttachmentException> {
            userAttachmentRepository.getUserAttachment(userAttachment.id, userAttachment.userId)
        }
    }
},)
