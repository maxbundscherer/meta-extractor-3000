package de.maxbundscherer.metadata.extractor.services

import de.maxbundscherer.metadata.extractor.utils.Configuration
import org.slf4j.Logger

class AwsS3Service(fileService: FileService)(implicit log: Logger) extends Configuration {

  import de.maxbundscherer.metadata.extractor.aggregates.AwsS3Aggregate

  import scala.util.{ Failure, Success, Try }
  import com.amazonaws.auth.{ AWSStaticCredentialsProvider, BasicAWSCredentials }
  import com.amazonaws.services.s3.{ AmazonS3, AmazonS3ClientBuilder }
  import com.amazonaws.services.s3.model.Bucket
  import com.amazonaws.services.s3.model.{ ObjectListing, S3ObjectSummary }
  import scala.jdk.CollectionConverters._

  /**
    * Aws S3 Client
    */
  private lazy val awsS3Client: Try[AmazonS3] = Try {

    log.debug("Login to aws")
    AmazonS3ClientBuilder
      .standard()
      .withCredentials(
        new AWSStaticCredentialsProvider(
          new BasicAWSCredentials(Config.AwsClients.S3.accessKey, Config.AwsClients.S3.secretKey)
        )
      )
      .build()
  }

  log.debug("AwsS3Service started")

  /**
    * Get Buckets from s3
    * @return Buckets
    */
  def getBuckets: Try[Vector[AwsS3Aggregate.Bucket]] =
    this.awsS3Client match {
      case Failure(exception) => throw exception
      case Success(client) =>
        Try {
          client
            .listBuckets()
            .asScala
            .toVector
            .map(b => AwsS3Aggregate.Bucket(name = b.getName))
        }
    }

  /**
    * Get FileInfos from s3 (updates cache too)
    * @param useCache Boolean
    * @param bucketName String
    * @return FileInfos
    */
  def getFileInfos(useCache: Boolean, bucketName: String): Try[Vector[AwsS3Aggregate.FileInfo]] = {

    val cache: Option[Vector[AwsS3Aggregate.FileInfo]] =
      if (!useCache) None
      else
        this.fileService.getCachedAwsFileInfos match {
          case Failure(exception) =>
            log.warn(s"Cache read getFileInfos exception (${exception.getLocalizedMessage})")
            None
          case Success(value) => Some(value)
        }

    cache match {
      case Some(value) =>
        log.info(s"Use cache for getFileInfos ${value.length} items found")
        Try(value)
      case None =>
        log.info("No cache for getFileInfos. Download data from s3")
        this.awsS3Client match {
          case Failure(exception) => throw exception
          case Success(client) =>
            Try {

              var listing: ObjectListing             = client.listObjects(bucketName)
              var summaries: Vector[S3ObjectSummary] = listing.getObjectSummaries.asScala.toVector

              while (listing.isTruncated) {
                listing = client.listNextBatchOfObjects(listing)
                summaries = summaries ++ listing.getObjectSummaries.asScala.toVector
                log.debug(s"Pagination in progress... (${summaries.size} items already loaded)")
              }

              val ans = summaries.map(s => AwsS3Aggregate.FileInfo(s.getKey, s.getSize))

              this.fileService.writeCachedAwsFileInfos(ans) match {
                case Failure(exception) =>
                  log.error(s"Error in writeCachedAwsFileInfos (${exception.getLocalizedMessage})")
                case Success(filePath) => log.info(s"WriteCachedAwsFileInfos success ($filePath)")
              }

              ans
            }
        }
    }

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
