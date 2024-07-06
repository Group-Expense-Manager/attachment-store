package pl.edu.agh.gem.integration.ability

import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec
import org.springframework.test.web.servlet.client.MockMvcWebTestClient.bindToApplicationContext
import org.springframework.web.context.WebApplicationContext
import pl.edu.agh.gem.headers.HeadersUtils.withValidatedUser
import pl.edu.agh.gem.security.GemUser
import java.net.URI

@Component
@Lazy
class ServiceTestClient(applicationContext: WebApplicationContext) {
    private val webClient = bindToApplicationContext(applicationContext)
        .configureClient()
        .build()

    fun createGroupAttachment(body: Any?, user: GemUser, groupId: String): ResponseSpec {
        return webClient.post()
            .uri(URI("/external/groups/$groupId"))
            .headers { it.withValidatedUser(user) }
            .bodyValue(body)
            .exchange()
    }

    fun getGroupAttachment(user: GemUser, groupId: String, attachmentId: String): ResponseSpec {
        return webClient.get()
            .uri(URI("/external/groups/$groupId/attachments/$attachmentId"))
            .headers { it.withValidatedUser(user) }
            .exchange()
    }

    fun generateGroupAttachment(userId: String, groupId: String): ResponseSpec {
        return webClient.post()
            .uri(URI("/internal/groups/$groupId/users/$userId/generate"))
            .exchange()
    }

    fun updateGroupAttachment(body: Any?, user: GemUser, attachmentId: String, groupId: String): ResponseSpec {
        return webClient.put()
            .uri(URI("/external/groups/$groupId/attachments/$attachmentId"))
            .headers { it.withValidatedUser(user) }
            .bodyValue(body)
            .exchange()
    }
}
