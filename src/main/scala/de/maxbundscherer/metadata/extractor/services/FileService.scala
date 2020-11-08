package de.maxbundscherer.metadata.extractor.services

import de.maxbundscherer.metadata.extractor.utils.Configuration

import com.amazonaws.services.s3.model.S3ObjectSummary
import org.slf4j.Logger

class FileService()(implicit log: Logger) extends Configuration {

  import scala.util.Try
  import better.files._
  import java.io.{ File => JFile }

  log.debug("FileService started")

  private val fileKeysFilename: String = "fileKeysCache.json"

  def getCachedFileKeys: Try[Vector[S3ObjectSummary]] = Try(???)
  def writeCachedFileKeys(data: Vector[S3ObjectSummary]): Try[Unit] =
    Try {

      //TODO: Use data
      val content = "testContent"

      File(s"${Config.Global.cacheDirectory}")
        .createDirectoryIfNotExists()

      File(s"${Config.Global.cacheDirectory}$fileKeysFilename")
        .createFileIfNotExists()
        .write(content)
        .pathAsString

    }

}
