package de.maxbundscherer.metadata.extractor.aws.clients

import de.maxbundscherer.metadata.extractor.utils.Configuration

import com.amazonaws.services.s3.model.S3ObjectSummary
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
  import scala.jdk.CollectionConverters._

  private lazy val awsS3Client: AmazonS3 = AmazonS3ClientBuilder
    .standard()
    .withCredentials(
      new AWSStaticCredentialsProvider(
        new BasicAWSCredentials(Config.AwsClients.S3.accessKey, Config.AwsClients.S3.secretKey)
      )
    )
    .build()

  def listBuckets(): Unit = {
    val data: Vector[Bucket] = this.awsS3Client.listBuckets().asScala.toVector
    data.foreach(d => log.info(s"Got s3 bucket (${d.getName})"))
  }

  def listFiles(bucketName: String): Unit = {
    val data: Vector[S3ObjectSummary] =
      this.awsS3Client.listObjects(bucketName).getObjectSummaries.asScala.toVector
    data.foreach(d => log.info(s"Got s3 bucket (${d.getKey}) (${d.getSize})"))
  }

}
