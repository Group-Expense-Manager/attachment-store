package pl.edu.agh.gem.integration.loader

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeOneOf
import io.kotest.matchers.shouldBe
import org.mockito.kotlin.whenever
import org.springframework.boot.test.mock.mockito.SpyBean
import pl.edu.agh.gem.external.config.DefaultImagesProperties
import pl.edu.agh.gem.external.loader.ResourceFileLoaderException
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.internal.loader.FileLoader
import pl.edu.agh.gem.util.ResourceLoader.loadResourceAsByteArray

class ResourceFileLoaderIT(
    private val fileLoader: FileLoader,
    @SpyBean
    private val defaultImagesProperties: DefaultImagesProperties,
) : BaseIntegrationSpec({
    should("load random group image") {
        // given
        whenever(defaultImagesProperties.group).thenReturn(setOf("group-default-0.jpeg"))
        val expectedAttachment = loadResourceAsByteArray("images/group/${defaultImagesProperties.group.first()}")

        // when
        val result = fileLoader.loadRandomGroupImage()

        // then
        result.shouldBe(expectedAttachment)
    }

    should("throw ResourceFileLoaderException on trying to load random group image when image dont exist") {
        // given
        whenever(defaultImagesProperties.group).thenReturn(setOf("group-no-default-0.jpeg"))

        // when && then
        shouldThrow<ResourceFileLoaderException> { fileLoader.loadRandomGroupImage() }
    }
},)
