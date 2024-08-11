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
import pl.edu.agh.gem.internal.persistence.GroupAttachmentRepository
import pl.edu.agh.gem.util.TestHelper.OTHER_SMALL_FILE
import pl.edu.agh.gem.util.TestHelper.SMALL_FILE
import pl.edu.agh.gem.util.createGroupAttachment

class GroupServiceTest : ShouldSpec({
    val groupAttachmentRepository = mock<GroupAttachmentRepository>()
    val fileDetector = mock<FileDetector> { }
    val fileLoader = mock<FileLoader> { }
    val groupService = GroupService(
        groupAttachmentRepository = groupAttachmentRepository,
        fileDetector = fileDetector,
        fileLoader = fileLoader,
    )

    should("save group attachment") {
        // given
        val attachment = createGroupAttachment()
        val data = attachment.file.data

        whenever(fileDetector.getFileSize(data)).thenReturn(attachment.sizeInBytes)
        whenever(fileDetector.getFileMediaType(data)).thenReturn(attachment.contentType)
        whenever(groupAttachmentRepository.save(any())).thenReturn(attachment)

        // when
        val groupAttachment = groupService.saveAttachment(data, attachment.groupId, attachment.uploadedByUser, false)

        // then
        verify(groupAttachmentRepository, times(1)).save(any())
        groupAttachment.id.shouldNotBeNull()
        groupAttachment.groupId shouldBe attachment.groupId
        groupAttachment.uploadedByUser shouldBe attachment.uploadedByUser
        groupAttachment.contentType shouldBe attachment.contentType
        groupAttachment.sizeInBytes shouldBe attachment.sizeInBytes
        groupAttachment.file shouldBe attachment.file
        groupAttachment.strictAccess shouldBe false
        groupAttachment.createdAt shouldBe attachment.createdAt
        groupAttachment.updatedAt shouldBe attachment.updatedAt
        groupAttachment.attachmentHistory shouldBe attachment.attachmentHistory
    }

    should("throw AttachmentContentTypeNotSupportedException for unsupported content type") {
        // given
        val attachment = createGroupAttachment()
        val data = attachment.file.data

        whenever(fileDetector.getFileSize(data)).thenReturn(attachment.sizeInBytes)
        whenever(fileDetector.getFileMediaType(data)).thenThrow(AttachmentContentTypeNotSupportedException("content-type"))
        whenever(groupAttachmentRepository.save(any())).thenReturn(attachment)

        // When & Then
        shouldThrow<AttachmentContentTypeNotSupportedException> {
            groupService.saveAttachment(data, attachment.groupId, attachment.uploadedByUser, false)
        }
        verify(groupAttachmentRepository, times(0)).save(any())
    }

    should("throw AttachmentSizeExceededException for exceeded file size") {
        // given
        val attachment = createGroupAttachment()
        val data = attachment.file.data

        whenever(fileDetector.getFileSize(data)).thenThrow(AttachmentSizeExceededException(1))
        whenever(fileDetector.getFileMediaType(data)).thenReturn(attachment.contentType)
        whenever(groupAttachmentRepository.save(any())).thenReturn(attachment)

        // When & Then
        shouldThrow<AttachmentSizeExceededException> {
            groupService.saveAttachment(data, attachment.groupId, attachment.uploadedByUser, false)
        }
        verify(groupAttachmentRepository, times(0)).save(any())
    }

    should("get group attachment") {
        // given
        val groupId = GROUP_ID
        val attachment = createGroupAttachment()
        whenever(groupAttachmentRepository.getGroupAttachment(any(), any())).thenReturn(attachment)

        // when
        val result = groupService.getAttachment(groupId, attachment.id)

        // then
        result shouldBe attachment
    }

    should("generate group image") {
        // given
        val attachment = createGroupAttachment()
        val data = attachment.file.data

        whenever(fileDetector.getFileSize(data)).thenReturn(attachment.sizeInBytes)
        whenever(fileDetector.getFileMediaType(data)).thenReturn(attachment.contentType)
        whenever(fileLoader.loadRandomGroupImage()).thenReturn(attachment.file.data)
        whenever(groupAttachmentRepository.save(any())).thenReturn(attachment)

        // when
        val groupAttachment = groupService.generateGroupImage(attachment.groupId, attachment.uploadedByUser)

        // then
        verify(groupAttachmentRepository, times(1)).save(any())
        groupAttachment.id.shouldNotBeNull()
        groupAttachment.groupId shouldBe attachment.groupId
        groupAttachment.uploadedByUser shouldBe attachment.uploadedByUser
        groupAttachment.contentType shouldBe attachment.contentType
        groupAttachment.sizeInBytes shouldBe attachment.sizeInBytes
        groupAttachment.file shouldBe attachment.file
        groupAttachment.strictAccess shouldBe false
        groupAttachment.createdAt shouldBe attachment.createdAt
        groupAttachment.updatedAt shouldBe attachment.updatedAt
        groupAttachment.attachmentHistory shouldBe attachment.attachmentHistory
    }

    should("update group attachment") {
        // given
        val attachment = createGroupAttachment(
            file = Binary(SMALL_FILE),
            uploadedByUser = "uploadedByUser",
            strictAccess = false,
        )
        val newAttachment = createGroupAttachment(
            id = attachment.id,
            groupId = attachment.groupId,
            file = Binary(OTHER_SMALL_FILE),
            uploadedByUser = "otherUser",
            strictAccess = false,
        )

        val data = newAttachment.file.data
        whenever(fileDetector.getFileSize(data)).thenReturn(newAttachment.sizeInBytes)
        whenever(fileDetector.getFileMediaType(data)).thenReturn(newAttachment.contentType)
        whenever(groupAttachmentRepository.save(any())).thenReturn(newAttachment)
        whenever(groupAttachmentRepository.getGroupAttachment(attachment.id, attachment.groupId)).thenReturn(attachment)

        // when
        val groupAttachment = groupService.updateAttachment(data, newAttachment.id, newAttachment.groupId, newAttachment.uploadedByUser)

        // then
        verify(groupAttachmentRepository, times(1)).getGroupAttachment(attachment.id, attachment.groupId)
        verify(groupAttachmentRepository, times(1)).save(
            argThat { a ->
                a.file == newAttachment.file &&
                    a.uploadedByUser == newAttachment.uploadedByUser &&
                    a.sizeInBytes == newAttachment.sizeInBytes &&
                    a.contentType == newAttachment.contentType &&
                    a.attachmentHistory.size == 2
            },
        )
        groupAttachment.id.shouldNotBeNull()
        groupAttachment.groupId shouldBe newAttachment.groupId
        groupAttachment.uploadedByUser shouldBe newAttachment.uploadedByUser
        groupAttachment.contentType shouldBe newAttachment.contentType
        groupAttachment.sizeInBytes shouldBe newAttachment.sizeInBytes
        groupAttachment.file shouldBe newAttachment.file
        groupAttachment.createdAt shouldBe attachment.createdAt
        groupAttachment.attachmentHistory shouldBe newAttachment.attachmentHistory
    }

    should("update group attachment with strict access") {
        // given
        val attachment = createGroupAttachment(
            file = Binary(SMALL_FILE),
            uploadedByUser = "uploadedByUser",
            strictAccess = true,
        )
        val newAttachment = createGroupAttachment(
            id = attachment.id,
            groupId = attachment.groupId,
            file = Binary(OTHER_SMALL_FILE),
            uploadedByUser = "uploadedByUser",
            strictAccess = true,
        )

        val data = newAttachment.file.data
        whenever(fileDetector.getFileSize(data)).thenReturn(newAttachment.sizeInBytes)
        whenever(fileDetector.getFileMediaType(data)).thenReturn(newAttachment.contentType)
        whenever(groupAttachmentRepository.save(any())).thenReturn(newAttachment)
        whenever(groupAttachmentRepository.getGroupAttachment(attachment.id, attachment.groupId)).thenReturn(attachment)

        // when
        val groupAttachment = groupService.updateAttachment(data, newAttachment.id, newAttachment.groupId, newAttachment.uploadedByUser)

        // then
        verify(groupAttachmentRepository, times(1)).getGroupAttachment(attachment.id, attachment.groupId)
        verify(groupAttachmentRepository, times(1)).save(
            argThat { a ->
                a.file == newAttachment.file &&
                    a.uploadedByUser == newAttachment.uploadedByUser &&
                    a.sizeInBytes == newAttachment.sizeInBytes &&
                    a.contentType == newAttachment.contentType &&
                    a.attachmentHistory.size == 2
            },
        )
        groupAttachment.id.shouldNotBeNull()
        groupAttachment.groupId shouldBe newAttachment.groupId
        groupAttachment.uploadedByUser shouldBe newAttachment.uploadedByUser
        groupAttachment.contentType shouldBe newAttachment.contentType
        groupAttachment.sizeInBytes shouldBe newAttachment.sizeInBytes
        groupAttachment.file shouldBe newAttachment.file
        groupAttachment.createdAt shouldBe attachment.createdAt
        groupAttachment.attachmentHistory shouldBe newAttachment.attachmentHistory
    }

    should("throw exception when user are not allowed to update attachment") {
        // given
        val attachment = createGroupAttachment(
            file = Binary(SMALL_FILE),
            uploadedByUser = "uploadedByUser",
            strictAccess = true,
        )
        val newAttachment = createGroupAttachment(
            id = attachment.id,
            groupId = attachment.groupId,
            file = Binary(OTHER_SMALL_FILE),
            uploadedByUser = "otherUser",
            strictAccess = true,
        )

        val data = newAttachment.file.data
        whenever(fileDetector.getFileSize(data)).thenReturn(newAttachment.sizeInBytes)
        whenever(fileDetector.getFileMediaType(data)).thenReturn(newAttachment.contentType)
        whenever(groupAttachmentRepository.save(any())).thenReturn(newAttachment)
        whenever(groupAttachmentRepository.getGroupAttachment(attachment.id, attachment.groupId)).thenReturn(attachment)

        // when & then
        shouldThrow<GroupAttachmentUpdateException> {
            groupService.updateAttachment(data, newAttachment.id, newAttachment.groupId, newAttachment.uploadedByUser)
        }
    }

    should("generate group blank image") {
        // given
        val attachment = createGroupAttachment()
        val data = attachment.file.data

        whenever(fileDetector.getFileSize(data)).thenReturn(attachment.sizeInBytes)
        whenever(fileDetector.getFileMediaType(data)).thenReturn(attachment.contentType)
        whenever(fileLoader.loadRandomBlankImage()).thenReturn(attachment.file.data)
        whenever(groupAttachmentRepository.save(any())).thenReturn(attachment)

        // when
        val groupAttachment = groupService.generateBlankImage(attachment.groupId, attachment.uploadedByUser)

        // then
        verify(groupAttachmentRepository, times(1)).save(any())
        groupAttachment.id.shouldNotBeNull()
        groupAttachment.groupId shouldBe attachment.groupId
        groupAttachment.uploadedByUser shouldBe attachment.uploadedByUser
        groupAttachment.contentType shouldBe attachment.contentType
        groupAttachment.sizeInBytes shouldBe attachment.sizeInBytes
        groupAttachment.file shouldBe attachment.file
        groupAttachment.strictAccess shouldBe false
        groupAttachment.createdAt shouldBe attachment.createdAt
        groupAttachment.updatedAt shouldBe attachment.updatedAt
        groupAttachment.attachmentHistory shouldBe attachment.attachmentHistory
    }
},)
