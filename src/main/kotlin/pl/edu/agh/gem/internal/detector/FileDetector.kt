package pl.edu.agh.gem.internal.detector

interface FileDetector {
    fun getFileMediaType(content: ByteArray): String
    fun getFileSize(content: ByteArray): Long
}
