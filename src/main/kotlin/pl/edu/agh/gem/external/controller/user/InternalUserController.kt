package pl.edu.agh.gem.external.controller.user

import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.gem.external.dto.UserAttachmentResponse
import pl.edu.agh.gem.internal.service.UserService
import pl.edu.agh.gem.media.InternalApiMediaType.APPLICATION_JSON_INTERNAL_VER_1
import pl.edu.agh.gem.paths.Paths.INTERNAL

@RestController
@RequestMapping("$INTERNAL/users")
class InternalUserController(
    val userService: UserService,
) {
    @PostMapping("/{userId}/generate", produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(CREATED)
    fun saveAttachment(
        @PathVariable userId: String,
    ): UserAttachmentResponse {
        return UserAttachmentResponse.from(userService.generateUserImage(userId))
    }
}
