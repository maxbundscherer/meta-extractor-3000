package de.maxbundscherer.metadata.extractor

import de.maxbundscherer.metadata.extractor.utils.{ ConfigurationHelper, LoggingHelper }

object Main extends App with ConfigurationHelper with LoggingHelper {

  import de.maxbundscherer.metadata.extractor.runners.{ AbstractRunner, DebugRunner }
  import de.maxbundscherer.metadata.extractor.services.{ AwsS3Service, FileService }

  log.info(s"Application started (${Config.Global.message})")

  private val fileService: FileService   = new FileService()
  private val awsS3Service: AwsS3Service = new AwsS3Service(fileService)

  private val runner: AbstractRunner = new DebugRunner(awsS3Service = awsS3Service)

  runner.run

  log.info(s"Application ended (${Config.Global.message})")
}
