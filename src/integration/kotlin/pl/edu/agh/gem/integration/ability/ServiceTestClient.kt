package pl.edu.agh.gem.integration.ability

import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec
import org.springframework.test.web.servlet.client.MockMvcWebTestClient.bindToApplicationContext
import org.springframework.web.context.WebApplicationContext
import pl.edu.agh.gem.headers.HeadersUtils.withAppAcceptType
import pl.edu.agh.gem.headers.HeadersUtils.withValidatedUser
import pl.edu.agh.gem.paths.Paths.EXTERNAL
import pl.edu.agh.gem.paths.Paths.INTERNAL
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
            .uri(URI("$EXTERNAL/groups/$groupId"))
            .headers { it.withValidatedUser(user) }
            .bodyValue(body)
            .exchange()
    }

    fun getGroupAttachment(user: GemUser, groupId: String, attachmentId: String): ResponseSpec {
        return webClient.get()
            .uri(URI("$EXTERNAL/groups/$groupId/attachments/$attachmentId"))
            .headers { it.withValidatedUser(user) }
            .exchange()
    }

    fun generateGroupAttachment(userId: String, groupId: String): ResponseSpec {
        return webClient.post()
            .uri(URI("$INTERNAL/groups/$groupId/users/$userId/generate"))
            .headers { it.withAppAcceptType() }
            .exchange()
    }

    fun updateGroupAttachment(body: Any?, user: GemUser, attachmentId: String, groupId: String): ResponseSpec {
        return webClient.put()
            .uri(URI("$EXTERNAL/groups/$groupId/attachments/$attachmentId"))
            .headers { it.withValidatedUser(user) }
            .bodyValue(body)
            .exchange()
    }

    fun createUserAttachment(body: Any?, user: GemUser): ResponseSpec {
        return webClient.post()
            .uri(URI("$EXTERNAL/users/"))
            .headers { it.withValidatedUser(user) }
            .bodyValue(body)
            .exchange()
    }

    fun getUserAttachment(userId: String, attachmentId: String): ResponseSpec {
        return webClient.get()
            .uri(URI("$EXTERNAL/users/$userId/attachments/$attachmentId"))
            .exchange()
    }

    fun generateUserAttachment(userId: String): ResponseSpec {
        return webClient.post()
            .uri(URI("$INTERNAL/users/$userId/generate"))
            .exchange()
    }

    fun updateUserAttachment(body: Any?, user: GemUser, attachmentId: String): ResponseSpec {
        return webClient.put()
            .uri(URI("$EXTERNAL/users/attachments/$attachmentId"))
            .headers { it.withValidatedUser(user) }
            .bodyValue(body)
            .exchange()
    }

    fun generateGroupBlankAttachment(userId: String, groupId: String): ResponseSpec {
        return webClient.post()
            .uri(URI("$INTERNAL/groups/$groupId/users/$userId/generate/blank"))
            .headers { it.withAppAcceptType() }
            .exchange()
    }

    fun createInternalGroupAttachment(body: Any?, userId: String, groupId: String): ResponseSpec {
        return webClient.post()
            .uri(URI("$INTERNAL/groups/$groupId?userId=$userId"))
            .bodyValue(body)
            .exchange()
    }

    fun getInternalGroupAttachment(groupId: String, attachmentId: String): ResponseSpec {
        return webClient.get()
            .uri(URI("$INTERNAL/groups/$groupId/attachments/$attachmentId"))
            .exchange()
    }
}
