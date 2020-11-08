package de.maxbundscherer.metadata.extractor.services

import de.maxbundscherer.metadata.extractor.utils.Configuration

import com.amazonaws.services.s3.model.S3ObjectSummary
import org.slf4j.Logger
import scala.util.Try

class FileService()(implicit log: Logger) extends Configuration {

  log.debug("FileService started")

  def getCachedFileKeys: Try[Vector[S3ObjectSummary]]               = Try(???)
  def writeCachedFileKeys(data: Vector[S3ObjectSummary]): Try[Unit] = Try(???)

}
