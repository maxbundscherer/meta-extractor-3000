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

  private lazy val awsS3Client: AmazonS3 = AmazonS3ClientBuilder
    .standard()
    .withCredentials(
      new AWSStaticCredentialsProvider(
        new BasicAWSCredentials(Config.AwsClients.S3.accessKey, Config.AwsClients.S3.secretKey)
      )
    )
    //.withRegion(awsS3ClientRegion)
    .build()

}
