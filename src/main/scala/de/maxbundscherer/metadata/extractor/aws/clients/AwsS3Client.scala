package de.maxbundscherer.metadata.extractor.aws.clients

import de.maxbundscherer.metadata.extractor.utils.Configuration

import org.slf4j.Logger

class AwsS3Client()(implicit log: Logger) extends Configuration {

  import com.amazonaws.services.s3.model.{
    CannedAccessControlList,
    GetObjectRequest,
    PutObjectRequest
  }
  import scala.util.{ Failure, Success, Try }
  import com.amazonaws.auth.{ AWSStaticCredentialsProvider, BasicAWSCredentials }
  import com.amazonaws.services.s3.{ AmazonS3, AmazonS3ClientBuilder }
  import com.amazonaws.services.s3.model.Bucket
  import com.amazonaws.services.s3.model.{ ObjectListing, S3ObjectSummary }
  import scala.jdk.CollectionConverters._

  private lazy val awsS3Client: Try[AmazonS3] = Try {
    AmazonS3ClientBuilder
      .standard()
      .withCredentials(
        new AWSStaticCredentialsProvider(
          new BasicAWSCredentials(Config.AwsClients.S3.accessKey, Config.AwsClients.S3.secretKey)
        )
      )
      .build()
  }

  def listBuckets: Try[Vector[Bucket]] =
    this.awsS3Client match {
      case Failure(exception) => throw exception
      case Success(awsS3Client) =>
        Try {
          awsS3Client.listBuckets().asScala.toVector
        }
    }

  /**
    * Get S3ObjectSummary from s3 bucket
    * @param bucketName String
    * @param cachedItems If set return cache
    * @return S3ObjectSummary
    */
  def listFiles(
      bucketName: String,
      cachedItems: Option[Vector[S3ObjectSummary]]
  ): Try[Vector[S3ObjectSummary]] =
    cachedItems match {
      case Some(cache) => Try(cache)
      case None =>
        this.awsS3Client match {
          case Failure(exception) => throw exception
          case Success(awsS3Client) =>
            Try {

              var listing: ObjectListing             = awsS3Client.listObjects(bucketName)
              var summaries: Vector[S3ObjectSummary] = listing.getObjectSummaries.asScala.toVector

              while (listing.isTruncated) {
                listing = awsS3Client.listNextBatchOfObjects(listing)
                summaries = summaries ++ listing.getObjectSummaries.asScala.toVector
                log.debug(s"Pagination in progress... (${summaries.size} items already loaded)")
              }

              summaries
            }
        }

    }

}
