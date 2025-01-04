package pl.edu.agh.gem.internal.detector

interface FileDetector {
    fun getFileMediaType(
        content: ByteArray,
        restrictionCheck: Boolean = true,
    ): String

    fun getFileSize(
        content: ByteArray,
        restrictionCheck: Boolean = true,
    ): Long
}
