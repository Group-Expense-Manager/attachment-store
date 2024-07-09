package pl.edu.agh.gem.integration.controler.user

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.bson.types.Binary
import org.springframework.http.HttpHeaders.CONTENT_LENGTH
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE
import org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE
import pl.edu.agh.gem.assertion.shouldBody
import pl.edu.agh.gem.assertion.shouldHaveBody
import pl.edu.agh.gem.assertion.shouldHaveHttpStatus
import pl.edu.agh.gem.external.dto.GroupAttachmentResponse
import pl.edu.agh.gem.external.dto.UserAttachmentResponse
import pl.edu.agh.gem.external.mapper.DefaultAttachmentMapper.Companion.CREATED_AT_HEADER
import pl.edu.agh.gem.external.mapper.DefaultAttachmentMapper.Companion.UPDATED_AT_HEADER
import pl.edu.agh.gem.helper.group.createGroupMembersResponse
import pl.edu.agh.gem.helper.user.createGemUser
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient
import pl.edu.agh.gem.internal.persistence.UserAttachmentRepository
import pl.edu.agh.gem.util.TestHelper.CSV_FILE
import pl.edu.agh.gem.util.TestHelper.EMPTY_FILE
import pl.edu.agh.gem.util.TestHelper.LARGE_FILE
import pl.edu.agh.gem.util.TestHelper.LITTLE_FILE
import pl.edu.agh.gem.util.TestHelper.OTHER_SMALL_FILE
import pl.edu.agh.gem.util.TestHelper.SMALL_FILE
import pl.edu.agh.gem.util.createUserAttachment

class ExternalUserControllerIT(
    private val service: ServiceTestClient,
    private val repository: UserAttachmentRepository,
) : BaseIntegrationSpec({
    should("save user attachment") {
        // given
        val user = createGemUser()
        val data = SMALL_FILE

        // when
        val response = service.createUserAttachment(data, user)

        // then
        response shouldHaveHttpStatus CREATED
        response.expectBody(UserAttachmentResponse::class.java).returnResult().responseBody?.also {
            it.id.shouldNotBeNull()
        }
    }

    should("not save user attachment when file is too large") {
        // given
        val user = createGemUser()
        val data = LARGE_FILE

        // when
        val response = service.createUserAttachment(data, user)

        // then
        response shouldHaveHttpStatus PAYLOAD_TOO_LARGE
    }

    should("not save user attachment when media is not supported") {
        // given
        val user = createGemUser()
        val data = CSV_FILE

        // when
        val response = service.createUserAttachment(data, user)

        // then
        response shouldHaveHttpStatus UNSUPPORTED_MEDIA_TYPE
    }

    should("not save user attachment when data is empty") {
        // given
        val user = createGemUser()
        val data = EMPTY_FILE

        // when
        val response = service.createUserAttachment(data, user)

        // then
        response shouldHaveHttpStatus BAD_REQUEST
    }

    should("get user attachment") {
        // given
        val user = createGemUser()
        val userAttachment = createUserAttachment(
            file = Binary(LITTLE_FILE),
        )
        repository.save(userAttachment)

        // when
        val response = service.getUserAttachment(user.id, userAttachment.id)

        // then
        response shouldHaveHttpStatus OK
        response.shouldHaveBody(userAttachment.file.data)
        response.expectHeader().also {
            it.valueEquals(CONTENT_LENGTH, userAttachment.sizeInBytes.toString())
            it.valueEquals(CONTENT_TYPE, userAttachment.contentType)
            it.valueEquals(CREATED_AT_HEADER, userAttachment.createdAt.toString())
            it.valueEquals(UPDATED_AT_HEADER, userAttachment.updatedAt.toString())
        }
    }

    should("return not found when user attachment not exists") {
        // given
        val user = createGemUser()
        val userAttachment = createUserAttachment()

        // when
        val response = service.getUserAttachment(user.id, userAttachment.id)

        // then
        response shouldHaveHttpStatus NOT_FOUND
    }

    should("update user attachment") {
        // given
        val user = createGemUser()
        val attachment = createUserAttachment(
            userId = user.id,
            file = Binary(SMALL_FILE),
        )
        val newAttachment = createUserAttachment(
            id = attachment.id,
            userId = user.id,
            file = Binary(OTHER_SMALL_FILE),
        )
        repository.save(attachment)

        // when
        val response = service.updateUserAttachment(newAttachment.file.data, user, newAttachment.id)

        // then
        response shouldHaveHttpStatus OK
        response.shouldBody<GroupAttachmentResponse> {
            this.id shouldBe newAttachment.id
        }
    }

    should("return not found when user attachment not exists while trying to update attachment") {
        // given
        val user = createGemUser()
        val newAttachment = createUserAttachment(
            file = Binary(OTHER_SMALL_FILE),
            userId = "otherUser",
        )

        // when
        val response = service.updateUserAttachment(newAttachment.file.data, user, newAttachment.id)

        // then
        response shouldHaveHttpStatus NOT_FOUND
    }

    should("not update user attachment when file is too large") {
        // given
        val user = createGemUser()
        val attachment = createUserAttachment(
            file = Binary(SMALL_FILE),
            userId = user.id,
        )
        val newAttachment = createUserAttachment(
            id = attachment.id,
            file = Binary(LARGE_FILE),
            userId = user.id,
        )
        repository.save(attachment)

        // when
        val response = service.updateUserAttachment(newAttachment.file.data, user, newAttachment.id)

        // then
        response shouldHaveHttpStatus PAYLOAD_TOO_LARGE
    }

    should("not update user attachment when media is not supported") {
        // given
        val user = createGemUser()
        val attachment = createUserAttachment(
            file = Binary(SMALL_FILE),
            userId = user.id,
        )
        val newAttachment = createUserAttachment(
            id = attachment.id,
            file = Binary(CSV_FILE),
            userId = user.id,
        )
        repository.save(attachment)

        // when
        val response = service.updateUserAttachment(newAttachment.file.data, user, newAttachment.id)

        // then
        response shouldHaveHttpStatus UNSUPPORTED_MEDIA_TYPE
    }
},)
