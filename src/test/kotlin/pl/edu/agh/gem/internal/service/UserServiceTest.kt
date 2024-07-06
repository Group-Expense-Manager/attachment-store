package pl.edu.agh.gem.internal.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.bson.types.Binary
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import pl.edu.agh.gem.external.detector.AttachmentContentTypeNotSupportedException
import pl.edu.agh.gem.external.detector.AttachmentSizeExceededException
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.internal.detector.FileDetector
import pl.edu.agh.gem.internal.loader.FileLoader
import pl.edu.agh.gem.internal.persistence.UserAttachmentRepository
import pl.edu.agh.gem.util.TestHelper.OTHER_SMALL_FILE
import pl.edu.agh.gem.util.TestHelper.SMALL_FILE
import pl.edu.agh.gem.util.createUserAttachment

class UserServiceTest : ShouldSpec({
    val userAttachmentRepository = mock<UserAttachmentRepository>()
    val fileDetector = mock<FileDetector> { }
    val fileLoader = mock<FileLoader> { }
    val userService = UserService(
        userAttachmentRepository = userAttachmentRepository,
        fileDetector = fileDetector,
        fileLoader = fileLoader,
    )

    should("save user attachment") {
        // given
        val attachment = createUserAttachment()
        val data = attachment.file.data

        whenever(fileDetector.getFileSize(data)).thenReturn(attachment.sizeInBytes)
        whenever(fileDetector.getFileMediaType(data)).thenReturn(attachment.contentType)
        whenever(userAttachmentRepository.save(any())).thenReturn(attachment)

        // when
        val groupAttachment = userService.saveAttachment(data, attachment.userId)

        // then
        verify(userAttachmentRepository, times(1)).save(any())
        groupAttachment.id.shouldNotBeNull()
        groupAttachment.userId shouldBe attachment.userId
        groupAttachment.contentType shouldBe attachment.contentType
        groupAttachment.sizeInBytes shouldBe attachment.sizeInBytes
        groupAttachment.file shouldBe attachment.file
        groupAttachment.createdAt shouldBe attachment.createdAt
        groupAttachment.updatedAt shouldBe attachment.updatedAt
        groupAttachment.attachmentHistory shouldBe attachment.attachmentHistory
    }

    should("throw AttachmentContentTypeNotSupportedException for unsupported content type") {
        // given
        val attachment = createUserAttachment()
        val data = attachment.file.data

        whenever(fileDetector.getFileSize(data)).thenReturn(attachment.sizeInBytes)
        whenever(fileDetector.getFileMediaType(data)).thenThrow(AttachmentContentTypeNotSupportedException("content-type"))
        whenever(userAttachmentRepository.save(any())).thenReturn(attachment)

        // When & Then
        shouldThrow<AttachmentContentTypeNotSupportedException> {
            userService.saveAttachment(data, attachment.userId)
        }
        verify(userAttachmentRepository, times(0)).save(any())
    }

    should("throw AttachmentSizeExceededException for exceeded file size") {
        // given
        val attachment = createUserAttachment()
        val data = attachment.file.data

        whenever(fileDetector.getFileSize(data)).thenThrow(AttachmentSizeExceededException(1))
        whenever(fileDetector.getFileMediaType(data)).thenReturn(attachment.contentType)
        whenever(userAttachmentRepository.save(any())).thenReturn(attachment)

        // When & Then
        shouldThrow<AttachmentSizeExceededException> {
            userService.saveAttachment(data, attachment.userId)
        }
        verify(userAttachmentRepository, times(0)).save(any())
    }

    should("get user attachment") {
        // given
        val groupId = GROUP_ID
        val attachment = createUserAttachment()
        whenever(userAttachmentRepository.getUserAttachment(any(), any())).thenReturn(attachment)

        // when
        val result = userService.getAttachment(groupId, attachment.id)

        // then
        result shouldBe attachment
    }

    should("generate user image") {
        // given
        val attachment = createUserAttachment()
        val data = attachment.file.data

        whenever(fileDetector.getFileSize(data)).thenReturn(attachment.sizeInBytes)
        whenever(fileDetector.getFileMediaType(data)).thenReturn(attachment.contentType)
        whenever(fileLoader.loadRandomGroupImage()).thenReturn(attachment.file.data)
        whenever(userAttachmentRepository.save(any())).thenReturn(attachment)

        // when
        val groupAttachment = userService.generateUserImage(attachment.userId)

        // then
        verify(userAttachmentRepository, times(1)).save(any())
        groupAttachment.id.shouldNotBeNull()
        groupAttachment.userId shouldBe attachment.userId
        groupAttachment.contentType shouldBe attachment.contentType
        groupAttachment.sizeInBytes shouldBe attachment.sizeInBytes
        groupAttachment.file shouldBe attachment.file
        groupAttachment.createdAt shouldBe attachment.createdAt
        groupAttachment.updatedAt shouldBe attachment.updatedAt
        groupAttachment.attachmentHistory shouldBe attachment.attachmentHistory
    }

    should("update user attachment") {
        // given
        val attachment = createUserAttachment(
            file = Binary(SMALL_FILE),
            userId = "userId",
        )
        val newAttachment = createUserAttachment(
            id = attachment.id,
            file = Binary(OTHER_SMALL_FILE),
            userId = "userId",
        )

        val data = newAttachment.file.data
        whenever(fileDetector.getFileSize(data)).thenReturn(newAttachment.sizeInBytes)
        whenever(fileDetector.getFileMediaType(data)).thenReturn(newAttachment.contentType)
        whenever(userAttachmentRepository.save(any())).thenReturn(newAttachment)
        whenever(userAttachmentRepository.getUserAttachment(attachment.id, attachment.userId)).thenReturn(attachment)

        // when
        val groupAttachment = userService.updateAttachment(data, newAttachment.id, newAttachment.userId)

        // then
        verify(userAttachmentRepository, times(1)).getUserAttachment(attachment.id, attachment.userId)
        verify(userAttachmentRepository, times(1)).save(
            argThat { a ->
                a.file == newAttachment.file &&
                    a.userId == newAttachment.userId &&
                    a.sizeInBytes == newAttachment.sizeInBytes &&
                    a.contentType == newAttachment.contentType &&
                    a.attachmentHistory.size == 2
            },
        )
        groupAttachment.id.shouldNotBeNull()
        groupAttachment.userId shouldBe newAttachment.userId
        groupAttachment.contentType shouldBe newAttachment.contentType
        groupAttachment.sizeInBytes shouldBe newAttachment.sizeInBytes
        groupAttachment.file shouldBe newAttachment.file
        groupAttachment.createdAt shouldBe attachment.createdAt
        groupAttachment.attachmentHistory shouldBe newAttachment.attachmentHistory
    }
},)
