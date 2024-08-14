package pl.edu.agh.gem.integration.loader

import io.kotest.assertions.throwables.shouldThrow
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

    should("load random user image") {
        // given
        whenever(defaultImagesProperties.user).thenReturn(setOf("user-default-0.jpeg"))
        val expectedAttachment = loadResourceAsByteArray("images/user/${defaultImagesProperties.user.first()}")

        // when
        val result = fileLoader.loadRandomUserImage()

        // then
        result.shouldBe(expectedAttachment)
    }

    should("throw ResourceFileLoaderException on trying to load random user image when image dont exist") {
        // given
        whenever(defaultImagesProperties.user).thenReturn(setOf("user-no-default-0.jpeg"))

        // when && then
        shouldThrow<ResourceFileLoaderException> { fileLoader.loadRandomUserImage() }
    }

    should("load random group blank image") {
        // given
        whenever(defaultImagesProperties.blank).thenReturn(setOf("blank-default-0.jpeg"))
        val expectedAttachment = loadResourceAsByteArray("images/blank/${defaultImagesProperties.blank.first()}")

        // when
        val result = fileLoader.loadRandomBlankImage()

        // then
        result.shouldBe(expectedAttachment)
    }

    should("throw ResourceFileLoaderException on trying to load random user image when image dont exist") {
        // given
        whenever(defaultImagesProperties.blank).thenReturn(setOf("user-no-default-0.jpeg"))

        // when && then
        shouldThrow<ResourceFileLoaderException> { fileLoader.loadRandomBlankImage() }
    }
},)
