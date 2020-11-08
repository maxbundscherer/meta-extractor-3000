package de.maxbundscherer.metadata.extractor.services

import com.amazonaws.services.s3.model.S3ObjectSummary
import de.maxbundscherer.metadata.extractor.utils.Configuration
import org.slf4j.Logger

import scala.util.{ Failure, Success }

class AwsS3Service(fileService: FileService)(implicit log: Logger) extends Configuration {

  import de.maxbundscherer.metadata.extractor.aws.clients.AwsS3Client

  private val awsS3Client: AwsS3Client = new AwsS3Client()

  log.debug("AwsS3Service started")

  private lazy val cachedItems: Option[Vector[S3ObjectSummary]] =
    this.fileService.getCachedFileKeys match {
      case Failure(exception) =>
        log.info(s"No cached listFiles (${exception.getLocalizedMessage})")
        None
      case Success(data) =>
        log.info("Use cached listFiles")
        Some(data)
    }

  private lazy val fileKeys: Vector[S3ObjectSummary] =
    this.awsS3Client.listFiles(Config.AwsClients.S3.bucketName, cachedItems = cachedItems) match {
      case Failure(exception) =>
        log.error(s"Got exception in listFiles (${exception.getLocalizedMessage})")
        ???
      case Success(fileKeys) =>
        log.info(s"Got ${fileKeys.size} items in listFiles")
        this.fileService.writeCachedFileKeys(fileKeys) match {
          case Failure(exception) =>
            log.warn(s"Exception in cache update (${exception.getLocalizedMessage})")
            ???
          case Success(filePath) =>
            log.info(s"Cached updated ($filePath)")
            fileKeys
        }
    }

  this.awsS3Client.listBuckets match {
    case Failure(exception) =>
      log.error(s"Got exception in listBuckets (${exception.getLocalizedMessage})")
    case Success(data) =>
      data.foreach(d => log.info(s"Got item in listBuckets (${d.getName})"))
  }

  this.fileKeys.take(10).foreach(fileKey => log.debug(s"Got fileKey (${fileKey.getKey})"))

}
