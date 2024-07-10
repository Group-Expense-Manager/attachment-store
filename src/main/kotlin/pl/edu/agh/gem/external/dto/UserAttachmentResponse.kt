package pl.edu.agh.gem.external.dto

import pl.edu.agh.gem.internal.model.UserAttachment

data class UserAttachmentResponse(
    val id: String,
) {
    companion object {
        fun from(groupAttachment: UserAttachment): UserAttachmentResponse {
            return UserAttachmentResponse(
                id = groupAttachment.id,
            )
        }
    }
}
