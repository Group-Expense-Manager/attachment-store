package pl.edu.agh.gem.internal.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import pl.edu.agh.gem.external.detector.AttachmentContentTypeNotSupportedException
import pl.edu.agh.gem.external.detector.AttachmentSizeExceededException
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.group.createGroupMembers
import pl.edu.agh.gem.internal.client.GroupManagerClient
import pl.edu.agh.gem.internal.detector.FileDetector
import pl.edu.agh.gem.internal.persistence.GroupAttachmentRepository
import pl.edu.agh.gem.util.createGroupAttachment

class GroupServiceTest : ShouldSpec({
    val groupAttachmentRepository = mock<GroupAttachmentRepository>()
    val groupManagerClient = mock<GroupManagerClient> { }
    val fileDetector = mock<FileDetector> { }
    val groupService = GroupService(
        groupAttachmentRepository = groupAttachmentRepository,
        groupManagerClient = groupManagerClient,
        fileDetector = fileDetector,
    )

    should("save group attachment") {
        // given
        val attachment = createGroupAttachment()
        val data = attachment.file.data

        whenever(fileDetector.getFileSize(data)).thenReturn(attachment.sizeInBytes)
        whenever(fileDetector.getFileMediaType(data)).thenReturn(attachment.contentType)
        whenever(groupAttachmentRepository.save(any())).thenReturn(attachment)

        // when
        val groupAttachment = groupService.saveAttachment(data, attachment.groupId, attachment.uploadedByUser)

        // then
        verify(groupAttachmentRepository, times(1)).save(any())
        groupAttachment.also {
            it.id.shouldNotBeNull()
            it.groupId shouldBe attachment.groupId
            it.uploadedByUser shouldBe attachment.uploadedByUser
            it.contentType shouldBe attachment.contentType
            it.sizeInBytes shouldBe attachment.sizeInBytes
            it.file shouldBe attachment.file
            it.createdAt shouldBe attachment.createdAt
            it.updatedAt shouldBe attachment.updatedAt
            it.attachmentHistory shouldBe attachment.attachmentHistory
        }
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
            groupService.saveAttachment(data, attachment.groupId, attachment.uploadedByUser)
        }
        verify(groupAttachmentRepository, times(0)).save(any())
    }

    should("throw an exception for unsupported content type") {
        // given
        val attachment = createGroupAttachment()
        val data = attachment.file.data

        whenever(fileDetector.getFileSize(data)).thenThrow(AttachmentSizeExceededException(1))
        whenever(fileDetector.getFileMediaType(data)).thenReturn(attachment.contentType)
        whenever(groupAttachmentRepository.save(any())).thenReturn(attachment)

        // When & Then
        shouldThrow<AttachmentSizeExceededException> {
            groupService.saveAttachment(data, attachment.groupId, attachment.uploadedByUser)
        }
        verify(groupAttachmentRepository, times(0)).save(any())
    }

    should("get group members from client") {
        // given
        val groupId = GROUP_ID
        val groupMembers = createGroupMembers()
        whenever(groupManagerClient.getGroupMembers(groupId)).thenReturn(groupMembers)

        // when
        val result = groupService.getGroupMembers(groupId)

        // then
        verify(groupManagerClient, atLeastOnce()).getGroupMembers(groupId)
        result shouldBe groupMembers
    }
},)
