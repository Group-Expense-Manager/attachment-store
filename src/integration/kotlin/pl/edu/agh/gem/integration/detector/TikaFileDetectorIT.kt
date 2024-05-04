package pl.edu.agh.gem.integration.detector

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.springframework.http.MediaType.IMAGE_JPEG_VALUE
import pl.edu.agh.gem.external.detector.AttachmentContentTypeNotSupportedException
import pl.edu.agh.gem.external.detector.AttachmentSizeExceededException
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.internal.detector.FileDetector
import pl.edu.agh.gem.util.TestHelper.CSV_FILE
import pl.edu.agh.gem.util.TestHelper.EMPTY_FILE
import pl.edu.agh.gem.util.TestHelper.LARGE_FILE
import pl.edu.agh.gem.util.TestHelper.SMALL_FILE

class TikaFileDetectorIT(
    private val fileDetector: FileDetector,
) : BaseIntegrationSpec({
    should("detect correct size of file") {
        // given
        val data = SMALL_FILE

        // when
        val dataSize = fileDetector.getFileSize(data)

        // then
        dataSize shouldBe data.size.toLong()
    }

    should("detect correct size of empty file") {
        // given
        val data = EMPTY_FILE

        // when
        val dataSize = fileDetector.getFileSize(data)

        // then
        dataSize shouldBe 0
    }

    should("throw AttachmentSizeExceededException when size is exceeded") {
        // given
        val data = LARGE_FILE

        // when % then
        shouldThrow<AttachmentSizeExceededException> {
            fileDetector.getFileSize(data)
        }
    }

    should("detect correct size of file") {
        // given
        val data = SMALL_FILE

        // when
        val dataSize = fileDetector.getFileMediaType(data)

        // then
        dataSize shouldBe IMAGE_JPEG_VALUE
    }

    should("throw AttachmentContentTypeNotSupportedException when size is exceeded") {
        // given
        val data = CSV_FILE

        // when % then
        shouldThrow<AttachmentContentTypeNotSupportedException> {
            fileDetector.getFileMediaType(data)
        }
    }
},)
