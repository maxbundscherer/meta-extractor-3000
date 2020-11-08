package de.maxbundscherer.metadata.extractor.services

import de.maxbundscherer.metadata.extractor.utils.Configuration

import org.slf4j.Logger
import scala.util.{ Failure, Success }

class AwsS3Service(fileService: FileService)(implicit log: Logger) extends Configuration {

  import de.maxbundscherer.metadata.extractor.aggregates.AwsAggregate
  import de.maxbundscherer.metadata.extractor.aws.clients.AwsS3Client

  private val awsS3Client: AwsS3Client = new AwsS3Client()

  log.debug("AwsS3Service started")

  private lazy val cachedItems: Option[Vector[AwsAggregate.FileKey]] =
    this.fileService.getCachedFileKeys match {
      case Failure(exception) =>
        log.info(s"No cached listFiles (${exception.getLocalizedMessage})")
        None
      case Success(data) =>
        log.info("Use cached listFiles")
        Some(data)
    }

  private lazy val fileKeys: Vector[AwsAggregate.FileKey] =
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

  log.info(
    "##########################################################################################"
  )
  log.info(s"Video files: ${this.fileKeys.count(_.fileKey.contains("video_files/"))}")
  log.info(s"Photo files: ${this.fileKeys.count(_.fileKey.contains("photos/"))}")
  log.info(
    s"Round Video Message files: ${this.fileKeys.count(_.fileKey.contains("round_video_messages/"))}"
  )
  log.info(s"File files: ${this.fileKeys.count(_.fileKey.contains("files/"))}")
  log.info(s"Sticker files: ${this.fileKeys.count(_.fileKey.contains("stickers/"))}")
  log.info(s"Voice Message files: ${this.fileKeys.count(_.fileKey.contains("voice_messages/"))}")

  log.info(
    "##########################################################################################"
  )
  log.info(s"In 08-10-2020: ${this.fileKeys.count(_.fileKey.contains("DS-08-10-2020/"))}")
  log.info(s"In 22-10-2020: ${this.fileKeys.count(_.fileKey.contains("DS-22-10-2020/"))}")
  log.info(s"Result JSON files: ${this.fileKeys.count(_.fileKey.contains("result.json"))}")
  log.info(
    s"Huge files: ${this.fileKeys.sortBy(_.sizeInByte).reverse.take(3).map(f => s"${f.fileKey} (${f.sizeInByte / 1000000} MB)").mkString(", ")}"
  )
  log.info(
    "##########################################################################################"
  )

}
