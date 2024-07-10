package pl.edu.agh.gem.integration.controler

import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.http.HttpStatus.CREATED
import pl.edu.agh.gem.assertion.shouldHaveHttpStatus
import pl.edu.agh.gem.external.dto.GroupAttachmentResponse
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.user.createGemUser
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient

class InternalGroupControllerIT(
    private val service: ServiceTestClient,
) : BaseIntegrationSpec({
    should("generate group attachment") {
        // given
        val user = createGemUser()
        val groupId = GROUP_ID

        // when
        val response = service.generateGroupAttachment(user.id, groupId)

        // then
        response shouldHaveHttpStatus CREATED
        response.expectBody(GroupAttachmentResponse::class.java).returnResult().responseBody?.also {
            it.id.shouldNotBeNull()
        }
    }
},)
