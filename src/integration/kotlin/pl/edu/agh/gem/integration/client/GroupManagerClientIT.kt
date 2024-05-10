package pl.edu.agh.gem.integration.client

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.OK
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.group.createGroupMembersResponse
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.stubGroupManagerMembers
import pl.edu.agh.gem.internal.client.GroupManagerClient
import pl.edu.agh.gem.internal.client.GroupManagerClientException
import pl.edu.agh.gem.internal.client.RetryableGroupManagerClientException

class GroupManagerClientIT(
    private val groupManagerClient: GroupManagerClient,
) : BaseIntegrationSpec({
    should("get group members") {
        // given
        val groupId = GROUP_ID
        val userId = USER_ID
        val groupMembers = createGroupMembersResponse(userId)
        stubGroupManagerMembers(groupMembers, groupId, OK)

        // when
        val result = groupManagerClient.getGroupMembers(groupId)

        // then
        result.members.find { it.id == userId }.shouldNotBeNull()
    }

    should("throw GroupManagerClientException when we send bad request") {
        // given
        val groupId = GROUP_ID
        val userId = USER_ID
        val groupMembers = createGroupMembersResponse(userId)
        stubGroupManagerMembers(groupMembers, groupId, BAD_REQUEST)

        // when & then
        shouldThrow<GroupManagerClientException> {
            groupManagerClient.getGroupMembers(groupId)
        }
    }

    should("throw RetryableGroupManagerClientException when client has internal error") {
        // given
        val groupId = GROUP_ID
        val userId = USER_ID
        val groupMembers = createGroupMembersResponse(userId)
        stubGroupManagerMembers(groupMembers, groupId, INTERNAL_SERVER_ERROR)

        // when & then
        shouldThrow<RetryableGroupManagerClientException> {
            groupManagerClient.getGroupMembers(groupId)
        }
    }
},)
