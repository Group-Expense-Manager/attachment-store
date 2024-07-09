package pl.edu.agh.gem.integration.controler.user

import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.http.HttpStatus.CREATED
import pl.edu.agh.gem.assertion.shouldHaveHttpStatus
import pl.edu.agh.gem.external.dto.UserAttachmentResponse
import pl.edu.agh.gem.helper.user.createGemUser
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient

class InternalUserControllerIT(
    private val service: ServiceTestClient,
) : BaseIntegrationSpec({
    should("generate user attachment") {
        // given
        val user = createGemUser()

        // when
        val response = service.generateUserAttachment(user.id)

        // then
        response shouldHaveHttpStatus CREATED
        response.expectBody(UserAttachmentResponse::class.java).returnResult().responseBody?.also {
            it.id.shouldNotBeNull()
        }
    }
},)
