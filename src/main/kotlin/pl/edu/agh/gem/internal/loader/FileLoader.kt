package pl.edu.agh.gem.internal.loader

interface FileLoader {
    fun loadRandomGroupImage(): ByteArray
    fun loadRandomUserImage(): ByteArray
    fun loadRandomBlankImage(): ByteArray
}
