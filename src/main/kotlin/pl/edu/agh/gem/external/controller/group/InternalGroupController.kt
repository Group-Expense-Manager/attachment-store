package pl.edu.agh.gem.external.controller.group

import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.gem.external.dto.GroupAttachmentResponse
import pl.edu.agh.gem.internal.service.GroupService
import pl.edu.agh.gem.media.InternalApiMediaType.APPLICATION_JSON_INTERNAL_VER_1
import pl.edu.agh.gem.paths.Paths.INTERNAL

@RestController
@RequestMapping("$INTERNAL/groups")
class InternalGroupController(
    val groupService: GroupService,
) {
    @PostMapping("/{groupId}/users/{userId}/generate", produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(CREATED)
    fun generateAttachment(
        @PathVariable groupId: String,
        @PathVariable userId: String,
    ): GroupAttachmentResponse {
        return GroupAttachmentResponse.from(groupService.generateGroupImage(groupId, userId))
    }

    @PostMapping("/{groupId}/users/{userId}/generate/blank", produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(CREATED)
    fun generateBlankAttachment(
        @PathVariable groupId: String,
        @PathVariable userId: String,
    ): GroupAttachmentResponse {
        return GroupAttachmentResponse.from(groupService.generateBlankImage(groupId, userId))
    }

    @PostMapping("/{groupId}", produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(CREATED)
    fun saveAttachment(
        @RequestBody fileBytes: ByteArray,
        @PathVariable groupId: String,
        @RequestParam strictAccess: Boolean = false,
        @RequestParam userId: String,
    ): GroupAttachmentResponse {
        val attachment = groupService.saveAttachment(
                data = fileBytes,
                groupId = groupId,
                userId = userId,
                strictAccess = strictAccess,
                restriction = false
        )
        return GroupAttachmentResponse.from(attachment)
    }
}
