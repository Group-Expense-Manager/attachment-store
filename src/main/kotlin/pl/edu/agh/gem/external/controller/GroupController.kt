package pl.edu.agh.gem.external.controller

import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.gem.external.dto.GroupAttachmentResponse
import pl.edu.agh.gem.internal.service.GroupService
import pl.edu.agh.gem.media.InternalApiMediaType.APPLICATION_JSON_INTERNAL_VER_1
import pl.edu.agh.gem.security.GemUserId

@RestController
@RequestMapping("/external/attachments/groups")
class GroupController(
    val groupService: GroupService,
) {

    @PostMapping("/{groupId}", consumes = [APPLICATION_OCTET_STREAM_VALUE], produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(CREATED)
    fun createProduct(
        @RequestBody fileBytes: ByteArray,
        @GemUserId userId: String,
        @PathVariable groupId: String,
    ): GroupAttachmentResponse {
        groupService.getGroupMembers(groupId).members.find { it.id == userId } ?: throw UserWithoutGroupAccessException(userId)
        val attachment = groupService.saveAttachment(fileBytes, groupId, userId)
        return GroupAttachmentResponse.from(attachment)
    }
}

class UserWithoutGroupAccessException(userId: String) : RuntimeException("User with id:$userId is not a member of the group")
