package pl.edu.agh.gem.integration.controler.group

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.bson.types.Binary
import org.springframework.http.HttpHeaders.CONTENT_LENGTH
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE
import org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE
import pl.edu.agh.gem.assertion.shouldBody
import pl.edu.agh.gem.assertion.shouldHaveBody
import pl.edu.agh.gem.assertion.shouldHaveHttpStatus
import pl.edu.agh.gem.external.dto.GroupAttachmentResponse
import pl.edu.agh.gem.external.mapper.DefaultAttachmentMapper.Companion.CREATED_AT_HEADER
import pl.edu.agh.gem.external.mapper.DefaultAttachmentMapper.Companion.UPDATED_AT_HEADER
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.group.DummyGroup.OTHER_GROUP_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.helper.user.createGemUser
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient
import pl.edu.agh.gem.integration.ability.stubGroupManagerUserGroups
import pl.edu.agh.gem.internal.persistence.GroupAttachmentRepository
import pl.edu.agh.gem.util.TestHelper.CSV_FILE
import pl.edu.agh.gem.util.TestHelper.EMPTY_FILE
import pl.edu.agh.gem.util.TestHelper.LARGE_FILE
import pl.edu.agh.gem.util.TestHelper.LITTLE_FILE
import pl.edu.agh.gem.util.TestHelper.OTHER_SMALL_FILE
import pl.edu.agh.gem.util.TestHelper.SMALL_FILE
import pl.edu.agh.gem.util.createGroupAttachment
import pl.edu.agh.gem.util.createUserGroupsResponse

class ExternalGroupControllerIT(
    private val service: ServiceTestClient,
    private val repository: GroupAttachmentRepository,
) : BaseIntegrationSpec({
        should("save group attachment") {
            // given
            val user = createGemUser()
            val data = SMALL_FILE
            val groupId = GROUP_ID
            stubGroupManagerUserGroups(createUserGroupsResponse(groupId, OTHER_GROUP_ID), USER_ID)

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
            val groupId = GROUP_ID
            stubGroupManagerUserGroups(createUserGroupsResponse(groupId, OTHER_GROUP_ID), USER_ID)

            // when
            val response = service.createGroupAttachment(data, user, groupId)

            // then
            response shouldHaveHttpStatus PAYLOAD_TOO_LARGE
        }

        should("not save group attachment when media is not supported") {
            // given
            val user = createGemUser()
            val data = CSV_FILE
            val groupId = GROUP_ID
            stubGroupManagerUserGroups(createUserGroupsResponse(groupId, OTHER_GROUP_ID), USER_ID)

            // when
            val response = service.createGroupAttachment(data, user, groupId)

            // then
            response shouldHaveHttpStatus UNSUPPORTED_MEDIA_TYPE
        }

        should("not save group attachment when user dont have access") {
            // given
            val user = createGemUser()
            val data = SMALL_FILE
            val groupId = GROUP_ID
            stubGroupManagerUserGroups(createUserGroupsResponse(OTHER_GROUP_ID), USER_ID)

            // when
            val response = service.createGroupAttachment(data, user, groupId)

            // then
            response shouldHaveHttpStatus FORBIDDEN
        }

        should("not save group attachment when data is empty") {
            // given
            val user = createGemUser()
            val data = EMPTY_FILE
            val groupId = GROUP_ID
            stubGroupManagerUserGroups(createUserGroupsResponse(groupId, OTHER_GROUP_ID), USER_ID)

            // when
            val response = service.createGroupAttachment(data, user, groupId)

            // then
            response shouldHaveHttpStatus BAD_REQUEST
        }

        should("get attachment") {
            // given
            val user = createGemUser()
            val groupAttachment =
                createGroupAttachment(
                    file = Binary(LITTLE_FILE),
                )
            repository.save(groupAttachment)
            stubGroupManagerUserGroups(createUserGroupsResponse(GROUP_ID, OTHER_GROUP_ID), USER_ID)

            // when
            val response = service.getGroupAttachment(user, groupAttachment.groupId, groupAttachment.id)

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

        should("forbid access when user without permission try to get attachment") {
            // given
            val user = createGemUser()
            val groupAttachment =
                createGroupAttachment(
                    file = Binary(LITTLE_FILE),
                )
            repository.save(groupAttachment)
            stubGroupManagerUserGroups(createUserGroupsResponse(OTHER_GROUP_ID), USER_ID)

            // when
            val response = service.getGroupAttachment(user, groupAttachment.groupId, groupAttachment.id)

            // then
            response shouldHaveHttpStatus FORBIDDEN
        }

        should("return not found when attachment not exists") {
            // given
            val user = createGemUser()
            val groupId = GROUP_ID
            val groupAttachment = createGroupAttachment()
            stubGroupManagerUserGroups(createUserGroupsResponse(groupId, OTHER_GROUP_ID), USER_ID)

            // when
            val response = service.getGroupAttachment(user, groupId, groupAttachment.id)

            // then
            response shouldHaveHttpStatus NOT_FOUND
        }

        should("update group attachment") {
            // given
            val user = createGemUser()
            val groupId = GROUP_ID
            val attachment =
                createGroupAttachment(
                    groupId = groupId,
                    file = Binary(SMALL_FILE),
                    uploadedByUser = "uploadedByUser",
                )
            val newAttachment =
                createGroupAttachment(
                    id = attachment.id,
                    groupId = attachment.groupId,
                    file = Binary(OTHER_SMALL_FILE),
                    uploadedByUser = user.id,
                )
            repository.save(attachment)
            stubGroupManagerUserGroups(createUserGroupsResponse(groupId, OTHER_GROUP_ID), USER_ID)

            // when
            val response = service.updateGroupAttachment(newAttachment.file.data, user, newAttachment.id, newAttachment.groupId)

            // then
            response shouldHaveHttpStatus OK
            response.shouldBody<GroupAttachmentResponse> {
                this.id shouldBe newAttachment.id
            }
            repository.getGroupAttachment(newAttachment.id, newAttachment.groupId).file.data shouldBe newAttachment.file.data
        }

        should("return not found when attachment not exists while trying to update attachment") {
            // given
            val user = createGemUser()
            val groupId = GROUP_ID
            val newAttachment =
                createGroupAttachment(
                    groupId = groupId,
                    file = Binary(OTHER_SMALL_FILE),
                    uploadedByUser = "otherUser",
                )
            stubGroupManagerUserGroups(createUserGroupsResponse(groupId, OTHER_GROUP_ID), USER_ID)

            // when
            val response = service.updateGroupAttachment(newAttachment.file.data, user, newAttachment.id, groupId)

            // then
            response shouldHaveHttpStatus NOT_FOUND
        }

        should("not update group attachment when file is too large") {
            // given
            val user = createGemUser()
            val groupId = GROUP_ID
            val attachment =
                createGroupAttachment(
                    groupId = groupId,
                    file = Binary(SMALL_FILE),
                    uploadedByUser = "uploadedByUser",
                )
            val newAttachment =
                createGroupAttachment(
                    id = attachment.id,
                    groupId = attachment.groupId,
                    file = Binary(LARGE_FILE),
                    uploadedByUser = user.id,
                )
            repository.save(attachment)
            stubGroupManagerUserGroups(createUserGroupsResponse(groupId, OTHER_GROUP_ID), USER_ID)

            // when
            val response = service.updateGroupAttachment(newAttachment.file.data, user, newAttachment.id, newAttachment.groupId)

            // then
            response shouldHaveHttpStatus PAYLOAD_TOO_LARGE
            repository.getGroupAttachment(newAttachment.id, newAttachment.groupId).file.data shouldBe attachment.file.data
        }

        should("not update group attachment when media is not supported") {
            // given
            val user = createGemUser()
            val groupId = GROUP_ID
            val attachment =
                createGroupAttachment(
                    groupId = groupId,
                    file = Binary(SMALL_FILE),
                    uploadedByUser = "uploadedByUser",
                )
            val newAttachment =
                createGroupAttachment(
                    id = attachment.id,
                    groupId = attachment.groupId,
                    file = Binary(CSV_FILE),
                    uploadedByUser = user.id,
                )
            repository.save(attachment)
            stubGroupManagerUserGroups(createUserGroupsResponse(groupId, OTHER_GROUP_ID), USER_ID)

            // when
            val response = service.updateGroupAttachment(newAttachment.file.data, user, newAttachment.id, newAttachment.groupId)

            // then
            response shouldHaveHttpStatus UNSUPPORTED_MEDIA_TYPE
            repository.getGroupAttachment(newAttachment.id, newAttachment.groupId).file.data shouldBe attachment.file.data
        }

        should("not update group attachment when user dont have access") {
            // given
            val user = createGemUser()
            val groupId = GROUP_ID
            val attachment =
                createGroupAttachment(
                    groupId = groupId,
                    file = Binary(SMALL_FILE),
                    uploadedByUser = "uploadedByUser",
                )
            val newAttachment =
                createGroupAttachment(
                    id = attachment.id,
                    groupId = attachment.groupId,
                    file = Binary(OTHER_SMALL_FILE),
                    uploadedByUser = user.id,
                )
            stubGroupManagerUserGroups(createUserGroupsResponse(OTHER_GROUP_ID), USER_ID)
            repository.save(attachment)

            // when
            val response = service.updateGroupAttachment(newAttachment.file.data, user, newAttachment.id, newAttachment.groupId)

            // then
            response shouldHaveHttpStatus FORBIDDEN
            repository.getGroupAttachment(newAttachment.id, newAttachment.groupId).file.data shouldBe attachment.file.data
        }

        should("not update group attachment with strict access when user is not creator") {
            // given
            val user = createGemUser()
            val groupId = GROUP_ID
            val attachment =
                createGroupAttachment(
                    groupId = groupId,
                    file = Binary(SMALL_FILE),
                    uploadedByUser = "uploadedByUser",
                    strictAccess = true,
                )
            val newAttachment =
                createGroupAttachment(
                    id = attachment.id,
                    groupId = attachment.groupId,
                    file = Binary(OTHER_SMALL_FILE),
                    uploadedByUser = user.id,
                    strictAccess = true,
                )
            stubGroupManagerUserGroups(createUserGroupsResponse(groupId, OTHER_GROUP_ID), USER_ID)
            repository.save(attachment)

            // when
            val response = service.updateGroupAttachment(newAttachment.file.data, user, newAttachment.id, newAttachment.groupId)

            // then
            response shouldHaveHttpStatus FORBIDDEN
            repository.getGroupAttachment(newAttachment.id, newAttachment.groupId).file.data shouldBe attachment.file.data
        }
    })
