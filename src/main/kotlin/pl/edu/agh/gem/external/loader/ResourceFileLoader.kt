package pl.edu.agh.gem.external.loader

import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import pl.edu.agh.gem.external.config.DefaultImagesProperties
import pl.edu.agh.gem.internal.loader.FileLoader
import java.io.IOException

@Component
class ResourceFileLoader(
    private val defaultImagesProperties: DefaultImagesProperties,
) : FileLoader {
    override fun loadRandomGroupImage(): ByteArray {
        return defaultImagesProperties.group.random().let {
            loadFile("images/group/$it")
        }
    }

    override fun loadRandomUserImage(): ByteArray {
        return defaultImagesProperties.user.random().let {
            loadFile("images/user/$it")
        }
    }

    override fun loadRandomBlankImage(): ByteArray {
        return defaultImagesProperties.blank.random().let {
            loadFile("images/blank/$it")
        }
    }

    private fun loadFile(path: String): ByteArray {
        return try {
            ClassPathResource(path).contentAsByteArray
        } catch (e: IOException) {
            throw ResourceFileLoaderException(path)
        }
    }
}

class ResourceFileLoaderException(path: String) : RuntimeException("Resource IOException occurred while loading file $path")
