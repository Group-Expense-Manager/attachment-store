package pl.edu.agh.gem.external.controller

import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE
import org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import pl.edu.agh.gem.error.SimpleErrorsHolder
import pl.edu.agh.gem.error.handleError
import pl.edu.agh.gem.external.detector.AttachmentContentTypeNotSupportedException
import pl.edu.agh.gem.external.detector.AttachmentSizeExceededException

@ControllerAdvice
@Order(LOWEST_PRECEDENCE)
class ApiExceptionHandler {

    @ExceptionHandler(UserWithoutGroupAccessException::class)
    fun handleUserWithoutGroupAccessException(
        exception: UserWithoutGroupAccessException,
    ): ResponseEntity<SimpleErrorsHolder> {
        return ResponseEntity(handleError(exception), FORBIDDEN)
    }

    @ExceptionHandler(AttachmentSizeExceededException::class)
    fun handleAttachmentSizeExceededException(
        exception: AttachmentSizeExceededException,
    ): ResponseEntity<SimpleErrorsHolder> {
        return ResponseEntity(handleError(exception), PAYLOAD_TOO_LARGE)
    }

    @ExceptionHandler(AttachmentContentTypeNotSupportedException::class)
    fun handleAttachmentContentTypeNotSupportedException(
        exception: AttachmentContentTypeNotSupportedException,
    ): ResponseEntity<SimpleErrorsHolder> {
        return ResponseEntity(handleError(exception), UNSUPPORTED_MEDIA_TYPE)
    }
}
