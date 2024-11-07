package pl.edu.agh.gem.integration.controler.group

import io.kotest.matchers.nulls.shouldNotBeNull
import org.bson.types.Binary
import org.springframework.http.HttpHeaders.CONTENT_LENGTH
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import pl.edu.agh.gem.assertion.shouldHaveBody
import pl.edu.agh.gem.assertion.shouldHaveHttpStatus
import pl.edu.agh.gem.external.dto.GroupAttachmentResponse
import pl.edu.agh.gem.external.mapper.DefaultAttachmentMapper.Companion.CREATED_AT_HEADER
import pl.edu.agh.gem.external.mapper.DefaultAttachmentMapper.Companion.UPDATED_AT_HEADER
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.group.createGroupMembersResponse
import pl.edu.agh.gem.helper.user.createGemUser
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient
import pl.edu.agh.gem.integration.ability.stubGroupManagerMembers
import pl.edu.agh.gem.internal.persistence.GroupAttachmentRepository
import pl.edu.agh.gem.util.TestHelper.CSV_FILE
import pl.edu.agh.gem.util.TestHelper.EMPTY_FILE
import pl.edu.agh.gem.util.TestHelper.LARGE_FILE
import pl.edu.agh.gem.util.TestHelper.LITTLE_FILE
import pl.edu.agh.gem.util.TestHelper.SMALL_FILE
import pl.edu.agh.gem.util.createGroupAttachment

class InternalGroupControllerIT(
    private val service: ServiceTestClient,
    private val repository: GroupAttachmentRepository,
) : BaseIntegrationSpec({
    should("generate group attachment") {
        // given
        val user = createGemUser()
        val groupId = GROUP_ID

        // when
        val response = service.generateGroupAttachment(user.id, groupId)

        // then
        response shouldHaveHttpStatus CREATED
        response.expectBody(GroupAttachmentResponse::class.java).returnResult().responseBody?.also {
            it.id.shouldNotBeNull()
        }
    }

    should("generate group blank attachment") {
        // given
        val user = createGemUser()
        val groupId = GROUP_ID

        // when
        val response = service.generateGroupBlankAttachment(user.id, groupId)

        // then
        response shouldHaveHttpStatus CREATED
        response.expectBody(GroupAttachmentResponse::class.java).returnResult().responseBody?.also {
            it.id.shouldNotBeNull()
        }
    }

    should("save group attachment") {
        // given
        val user = createGemUser()
        val data = SMALL_FILE
        val groupMembers = createGroupMembersResponse(user.id)
        val groupId = GROUP_ID
        stubGroupManagerMembers(groupMembers, groupId, OK)

        // when
        val response = service.createInternalGroupAttachment(data, user.id, groupId)

        // then
        response shouldHaveHttpStatus CREATED
        response.expectBody(GroupAttachmentResponse::class.java).returnResult().responseBody?.also {
            it.id.shouldNotBeNull()
        }
    }

    should("save group attachment when file is too large") {
        // given
        val user = createGemUser()
        val data = LARGE_FILE
        val groupMembers = createGroupMembersResponse(user.id)
        val groupId = GROUP_ID
        stubGroupManagerMembers(groupMembers, groupId, OK)

        // when
        val response = service.createInternalGroupAttachment(data, user.id, groupId)

        // then
        response shouldHaveHttpStatus CREATED
        response.expectBody(GroupAttachmentResponse::class.java).returnResult().responseBody?.also {
            it.id.shouldNotBeNull()
        }
    }

    should("save group attachment when media is not supported") {
        // given
        val user = createGemUser()
        val data = CSV_FILE
        val groupMembers = createGroupMembersResponse(user.id)
        val groupId = GROUP_ID
        stubGroupManagerMembers(groupMembers, groupId, OK)

        // when
        val response = service.createInternalGroupAttachment(data, user.id, groupId)

        // then
        response shouldHaveHttpStatus CREATED
        response.expectBody(GroupAttachmentResponse::class.java).returnResult().responseBody?.also {
            it.id.shouldNotBeNull()
        }
    }

    should("not save group attachment when data is empty") {
        // given
        val user = createGemUser()
        val data = EMPTY_FILE
        val groupMembers = createGroupMembersResponse(user.id)
        val groupId = GROUP_ID
        stubGroupManagerMembers(groupMembers, groupId, OK)

        // when
        val response = service.createInternalGroupAttachment(data, user.id, groupId)

        // then
        response shouldHaveHttpStatus BAD_REQUEST
    }

    should("get attachment") {
        // given
        val groupAttachment = createGroupAttachment(
            file = Binary(LITTLE_FILE),
        )
        repository.save(groupAttachment)

        // when
        val response = service.getInternalGroupAttachment(groupAttachment.groupId, groupAttachment.id)

        // then
        response shouldHaveHttpStatus OK
        response.shouldHaveBody(groupAttachment.file.data)
        response.expectHeader().also {
            it.valueEquals(CONTENT_LENGTH, groupAttachment.sizeInBytes.toString())
            it.valueEquals(CONTENT_TYPE, groupAttachment.contentType)
            it.valueEquals(CREATED_AT_HEADER, groupAttachment.createdAt.toString())
            it.valueEquals(UPDATED_AT_HEADER, groupAttachment.updatedAt.toString())
        }
    }

    should("return not found when attachment not exists") {
        // given & when
        val response = service.getInternalGroupAttachment(GROUP_ID, "id")

        // then
        response shouldHaveHttpStatus NOT_FOUND
    }
},)
