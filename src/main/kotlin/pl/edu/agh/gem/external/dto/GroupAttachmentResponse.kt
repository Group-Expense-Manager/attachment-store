package pl.edu.agh.gem.external.dto

import pl.edu.agh.gem.internal.model.GroupAttachment

data class GroupAttachmentResponse(
    val id: String,
) {
    companion object {
        fun from(groupAttachment: GroupAttachment): GroupAttachmentResponse {
            return GroupAttachmentResponse(
                id = groupAttachment.id,
            )
        }
    }
}
