package pl.edu.agh.gem.external.controller.user

import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.gem.external.dto.UserAttachmentResponse
import pl.edu.agh.gem.external.mapper.AttachmentMapper
import pl.edu.agh.gem.internal.service.UserService
import pl.edu.agh.gem.media.InternalApiMediaType.APPLICATION_JSON_INTERNAL_VER_1
import pl.edu.agh.gem.paths.Paths.EXTERNAL
import pl.edu.agh.gem.security.GemUserId

@RestController
@RequestMapping("$EXTERNAL/users/")
class ExternalUserController(
    val userService: UserService,
    val attachmentMapper: AttachmentMapper,
) {

    @PostMapping(produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(CREATED)
    fun saveAttachment(
        @RequestBody fileBytes: ByteArray,
        @GemUserId userId: String,
    ): UserAttachmentResponse {
        val attachment = userService.saveAttachment(fileBytes, userId)
        return UserAttachmentResponse.from(attachment)
    }

    @GetMapping("/{userId}/attachments/{attachmentId}", produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(OK)
    fun getAttachment(
        @PathVariable attachmentId: String,
        @PathVariable userId: String,
    ): HttpEntity<ByteArray> {
        val attachment = userService.getAttachment(userId, attachmentId)
        return attachmentMapper.mapToResponseEntity(attachment)
    }

    @PutMapping("/attachments/{attachmentId}", produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(OK)
    fun updateAttachment(
        @RequestBody fileBytes: ByteArray,
        @GemUserId userId: String,
        @PathVariable attachmentId: String,
    ): UserAttachmentResponse {
        val attachment = userService.updateAttachment(fileBytes, attachmentId, userId)
        return UserAttachmentResponse.from(attachment)
    }
}
