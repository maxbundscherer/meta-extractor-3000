package de.maxbundscherer.metadata.extractor.services

import de.maxbundscherer.metadata.extractor.utils.{ ConfigurationHelper, LoggerHelper }

class AwsS3Service(fileService: FileService) extends ConfigurationHelper with LoggerHelper {

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

}
