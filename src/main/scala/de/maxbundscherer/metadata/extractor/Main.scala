package de.maxbundscherer.metadata.extractor

import de.maxbundscherer.metadata.extractor.utils.Configuration

object Main extends App with Configuration {

  import de.maxbundscherer.metadata.extractor.services.{ AwsS3Service, FileService }

  import org.slf4j.{ Logger, LoggerFactory }

  private implicit val log: Logger = LoggerFactory.getLogger("Main-Logger")

  private val fileService: FileService   = new FileService()
  private val awsS3Service: AwsS3Service = new AwsS3Service()

  log.info(s"Application started (${Config.Global.startUpMessage})")
}
