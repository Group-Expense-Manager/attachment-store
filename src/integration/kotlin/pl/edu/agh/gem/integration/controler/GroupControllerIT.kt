package pl.edu.agh.gem.integration.controler

import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE
import org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE
import pl.edu.agh.gem.assertion.shouldHaveHttpStatus
import pl.edu.agh.gem.external.dto.GroupAttachmentResponse
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.group.createGroupMembersResponse
import pl.edu.agh.gem.helper.user.DummyUser.OTHER_USER_ID
import pl.edu.agh.gem.helper.user.createGemUser
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient
import pl.edu.agh.gem.integration.ability.stubGroupManagerMembers
import pl.edu.agh.gem.util.TestHelper.CSV_FILE
import pl.edu.agh.gem.util.TestHelper.EMPTY_FILE
import pl.edu.agh.gem.util.TestHelper.LARGE_FILE
import pl.edu.agh.gem.util.TestHelper.SMALL_FILE

class GroupControllerIT(
    private val service: ServiceTestClient,
) : BaseIntegrationSpec({
    should("save group attachment") {
        // given
        val user = createGemUser()
        val data = SMALL_FILE
        val groupMembers = createGroupMembersResponse(user.id)
        val groupId = GROUP_ID
        stubGroupManagerMembers(groupMembers, groupId, OK)

        // when
        val response = service.createGroupAttachment(data, user, groupId)

        // then
        response shouldHaveHttpStatus CREATED
        response.expectBody(GroupAttachmentResponse::class.java).returnResult().responseBody?.also {
            it.id.shouldNotBeNull()
        }
    }

    should("not save group attachment when file is too large") {
        // given
        val user = createGemUser()
        val data = LARGE_FILE
        val groupMembers = createGroupMembersResponse(user.id)
        val groupId = GROUP_ID
        stubGroupManagerMembers(groupMembers, groupId, OK)

        // when
        val response = service.createGroupAttachment(data, user, groupId)

        // then
        response shouldHaveHttpStatus PAYLOAD_TOO_LARGE
    }

    should("not save group attachment when media is not supported") {
        // given
        val user = createGemUser()
        val data = CSV_FILE
        val groupMembers = createGroupMembersResponse(user.id)
        val groupId = GROUP_ID
        stubGroupManagerMembers(groupMembers, groupId, OK)

        // when
        val response = service.createGroupAttachment(data, user, groupId)

        // then
        response shouldHaveHttpStatus UNSUPPORTED_MEDIA_TYPE
    }

    should("not save group attachment when user dont have access") {
        // given
        val user = createGemUser()
        val data = SMALL_FILE
        val groupMembers = createGroupMembersResponse(OTHER_USER_ID)
        val groupId = GROUP_ID
        stubGroupManagerMembers(groupMembers, groupId, OK)

        // when
        val response = service.createGroupAttachment(data, user, groupId)

        // then
        response shouldHaveHttpStatus FORBIDDEN
    }

    should("not save group attachment when data is empty") {
        // given
        val user = createGemUser()
        val data = EMPTY_FILE
        val groupMembers = createGroupMembersResponse(user.id)
        val groupId = GROUP_ID
        stubGroupManagerMembers(groupMembers, groupId, OK)

        // when
        val response = service.createGroupAttachment(data, user, groupId)

        // then
        response shouldHaveHttpStatus BAD_REQUEST
    }
},)
