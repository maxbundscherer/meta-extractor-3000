package de.maxbundscherer.metadata.extractor.runners

import de.maxbundscherer.metadata.extractor.services.{ AwsS3Service, LocalFileService }

class DebugRunner(awsS3Service: AwsS3Service, localFileService: LocalFileService)
    extends AbstractRunner(awsS3Service = awsS3Service) {

  import de.maxbundscherer.metadata.extractor.aggregates.AwsS3Aggregate
  import de.maxbundscherer.metadata.extractor.aggregates.LocalAggregate

  import scala.util.{ Failure, Success }

  override def run: Unit = {
    log.info("# DebugRunner init")
    log.info("# Get buckets from s3")
    this.awsS3Service.getBuckets match {
      case Failure(exception) =>
        log.error(s"Error in getBuckets (${exception.getLocalizedMessage}) from aws")
      case Success(buckets) => buckets.foreach(b => log.info(s"Get bucket '${b.name}' from aws"))
    }
    log.info("# Get fileInfos from s3")
    val awsFileInfos: Vector[AwsS3Aggregate.FileInfo] = this.awsS3Service
      .getFileInfos(useCache = true, bucketName = Config.AwsClients.S3.bucketName) match {
      case Failure(exception) =>
        log.error(s"Error in getFileInfos (${exception.getLocalizedMessage}) from aws")
        throw exception
      case Success(fileInfos) =>
        log.info(s"Loaded ${fileInfos.length} fileInfos from aws")
        fileInfos
    }
    log.info("# Get fileInfos from local dir")
    val localFileInfos: Vector[LocalAggregate.FileInfo] = this.localFileService
      .getFileInfos(useCache = true) match {
      case Failure(exception) =>
        log.error(s"Error in getFileInfos (${exception.getLocalizedMessage}) from local dir")
        throw exception
      case Success(fileInfos) =>
        log.info(s"Loaded ${fileInfos.length} fileInfos from local dir")
        fileInfos
    }
    log.info("# Start query executor")
    queryExecutor(awsFileInfos, localFileInfos)
  }

  private def queryExecutor(
      awsFileInfos: Vector[AwsS3Aggregate.FileInfo],
      localFileInfos: Vector[LocalAggregate.FileInfo]
  ): Unit = {
    val localFileKeys = localFileInfos.map(_.fileKey)
    val awsFileKeys   = awsFileInfos.map(_.fileKey)

    log.info("# -- Count only local files")
    val onlyLocal = localFileKeys.diff(awsFileKeys)
    log.info("Only local: " + onlyLocal.length)

    log.info("# -- Count only files on aws")
    val onlyAws = awsFileKeys.diff(localFileKeys)
    log.info("Only on aws: " + onlyAws.length)

    log.info("# -- Count both")
    val both = awsFileKeys.intersect(localFileKeys)
    log.info("Only on aws: " + both.length)

  }

  //TODO: Add query
  /*
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
   */

}
