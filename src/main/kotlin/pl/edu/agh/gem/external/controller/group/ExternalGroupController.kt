package pl.edu.agh.gem.external.controller.group

import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.gem.exception.UserWithoutGroupAccessException
import pl.edu.agh.gem.external.dto.GroupAttachmentResponse
import pl.edu.agh.gem.external.mapper.AttachmentMapper
import pl.edu.agh.gem.internal.client.GroupManagerClient
import pl.edu.agh.gem.internal.service.GroupService
import pl.edu.agh.gem.media.InternalApiMediaType.APPLICATION_JSON_INTERNAL_VER_1
import pl.edu.agh.gem.paths.Paths.EXTERNAL
import pl.edu.agh.gem.security.GemUserId

@RestController
@RequestMapping("$EXTERNAL/groups")
class ExternalGroupController(
    val groupService: GroupService,
    val attachmentMapper: AttachmentMapper,
    val groupManagerClient: GroupManagerClient,
) {
    @PostMapping("/{groupId}", produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(CREATED)
    fun saveAttachment(
        @RequestBody fileBytes: ByteArray,
        @GemUserId userId: String,
        @PathVariable groupId: String,
        @RequestParam strictAccess: Boolean = false,
    ): GroupAttachmentResponse {
        userId.checkIfUserHaveAccess(groupId)
        val attachment = groupService.saveAttachment(fileBytes, groupId, userId, strictAccess)
        return GroupAttachmentResponse.from(attachment)
    }

    @GetMapping("/{groupId}/attachments/{attachmentId}", produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(OK)
    fun getAttachment(
        @GemUserId userId: String,
        @PathVariable groupId: String,
        @PathVariable attachmentId: String,
    ): HttpEntity<ByteArray> {
        userId.checkIfUserHaveAccess(groupId)
        val attachment = groupService.getAttachment(groupId, attachmentId)
        return attachmentMapper.mapToResponseEntity(attachment)
    }

    @PutMapping("/{groupId}/attachments/{attachmentId}", produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(OK)
    fun updateAttachment(
        @RequestBody fileBytes: ByteArray,
        @GemUserId userId: String,
        @PathVariable groupId: String,
        @PathVariable attachmentId: String,
    ): GroupAttachmentResponse {
        userId.checkIfUserHaveAccess(groupId)
        val attachment = groupService.updateAttachment(fileBytes, attachmentId, groupId, userId)
        return GroupAttachmentResponse.from(attachment)
    }

    private fun String.checkIfUserHaveAccess(groupId: String) {
        groupManagerClient.getGroups(this).find { it.groupId == groupId } ?: throw UserWithoutGroupAccessException(this)
    }
}
