package de.maxbundscherer.metadata.extractor.services

import de.maxbundscherer.metadata.extractor.utils.Configuration

import org.slf4j.Logger

class AwsS3Service()(implicit log: Logger) extends Configuration {

  import de.maxbundscherer.metadata.extractor.aws.clients.AwsS3Client

  private val awsS3Client: AwsS3Client = new AwsS3Client()

  log.debug("AwsS3Service started")

}
