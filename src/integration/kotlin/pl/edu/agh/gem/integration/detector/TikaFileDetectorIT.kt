package pl.edu.agh.gem.integration.detector

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import org.springframework.http.MediaType.APPLICATION_PDF_VALUE
import org.springframework.http.MediaType.IMAGE_JPEG_VALUE
import pl.edu.agh.gem.external.detector.AttachmentContentTypeNotSupportedException
import pl.edu.agh.gem.external.detector.AttachmentSizeExceededException
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.internal.detector.FileDetector
import pl.edu.agh.gem.util.TestHelper.CSV_FILE
import pl.edu.agh.gem.util.TestHelper.EMPTY_FILE
import pl.edu.agh.gem.util.TestHelper.LARGE_FILE
import pl.edu.agh.gem.util.TestHelper.PDF_FILE
import pl.edu.agh.gem.util.TestHelper.SMALL_FILE
import pl.edu.agh.gem.util.TestHelper.XLSX_CONTENT_TYPE_VALUE
import pl.edu.agh.gem.util.TestHelper.XLSX_FILE

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

        context("detect correct media type of file") {
            withData(
                nameFn = { (file, expectedMediaType) -> "File: $file, Expected: $expectedMediaType" },
                Pair(SMALL_FILE, IMAGE_JPEG_VALUE),
                Pair(PDF_FILE, APPLICATION_PDF_VALUE),
                Pair(XLSX_FILE, XLSX_CONTENT_TYPE_VALUE),
            ) { (file, expectedMediaType) ->
                // when
                val mediaType = fileDetector.getFileMediaType(file, false)

                // then
                mediaType shouldBe expectedMediaType
            }
        }

        should("throw AttachmentContentTypeNotSupportedException when content type is not supported") {
            // given
            val data = CSV_FILE

            // when % then
            shouldThrow<AttachmentContentTypeNotSupportedException> {
                fileDetector.getFileMediaType(data)
            }
        }
    })
