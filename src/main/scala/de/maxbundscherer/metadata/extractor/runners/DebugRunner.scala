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

    /**
      * Single query
      * @param queryName
      * @param fileKeyFilterIsLike
      * @param rawAwsFileKeys
      * @param rawLocalFileKeys
      * @return intersect
      */
    def singleQuery(
        queryName: String,
        fileKeyFilterIsLike: Option[String],
        rawAwsFileKeys: Vector[String],
        rawLocalFileKeys: Vector[String]
    ): Vector[String] = {

      val dAws = fileKeyFilterIsLike match {
        case Some(filterC) => rawAwsFileKeys.filter(_.contains(filterC))
        case None          => rawAwsFileKeys
      }
      val dLocal = fileKeyFilterIsLike match {
        case Some(filterC) => rawLocalFileKeys.filter(_.contains(filterC))
        case None          => rawLocalFileKeys
      }

      val onlyLocal = dLocal.diff(dAws)
      log.info(s"($queryName) Only local files: " + onlyLocal.length)

      val onlyAws = dAws.diff(dLocal)
      log.info(s"($queryName) Only files on aws: " + onlyAws.length)

      val both = dAws.intersect(dLocal)
      log.info(s"($queryName) Files on both systems " + both.length)
      both
    }

    log.info("## Query")
    val both = singleQuery(
      queryName = "query-all-files",
      fileKeyFilterIsLike = None,
      rawAwsFileKeys = awsFileKeys,
      rawLocalFileKeys = localFileKeys
    )

    log.info("## Query")
    singleQuery(
      queryName = "query-json-files",
      fileKeyFilterIsLike = Some("json"),
      rawAwsFileKeys = awsFileKeys,
      rawLocalFileKeys = localFileKeys
    )

    log.info("## Get FileEndings")
    val fileEndings = both
      .map { i =>
        val d: Array[String] = i.split('.')
        if (d.isEmpty) "error" else if (d.length != 2) "noEnding" else d.last //TODO: Improve filter
      }

    log.info(s"Got ${fileEndings.count(_.eq("error"))} errors in fileEndings")
    log.info(s"Got ${fileEndings.count(!_.eq("error"))} success in fileEndings")

    val t: Map[String, Vector[String]] = fileEndings.filter(!_.eq("error")).groupBy(i => i)

    t.keys
      .map(key => (key, t(key).size))
      .toVector
      .sortBy(_._2 * -1)
      .foreach(fileEnding => log.info(s"Got fileEnding (${fileEnding._1}) ${fileEnding._2} times"))

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
