package de.maxbundscherer.metadata.extractor.services

import de.maxbundscherer.metadata.extractor.utils.Configuration

import org.slf4j.Logger
import scala.util.{ Failure, Success }

class AwsS3Service()(implicit log: Logger) extends Configuration {

  import de.maxbundscherer.metadata.extractor.aws.clients.AwsS3Client

  private val awsS3Client: AwsS3Client = new AwsS3Client()

  log.debug("AwsS3Service started")

  this.awsS3Client.listBuckets match {
    case Failure(exception) =>
      log.error(s"Got exception in listBuckets (${exception.getLocalizedMessage})")
    case Success(data) =>
      data.foreach(d => log.info(s"Got item in listBuckets (${d.getName})"))
  }

  this.awsS3Client.listFiles(Config.AwsClients.S3.bucketName) match {
    case Failure(exception) =>
      log.error(s"Got exception in listFiles (${exception.getLocalizedMessage})")
    case Success(data) =>
      log.info(s"Got ${data.size} items in listFiles")
    //data.foreach(d => log.info(s"Got item in listFiles (${d.getKey})"))
  }

}
