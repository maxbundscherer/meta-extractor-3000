package de.maxbundscherer.metadata.extractor.runners

import de.maxbundscherer.metadata.extractor.services.AwsS3Service
import de.maxbundscherer.metadata.extractor.utils.{ ConfigurationHelper, LoggerHelper }

abstract class AbstractRunner(awsS3Service: AwsS3Service)
    extends ConfigurationHelper
    with LoggerHelper {

  def run: Unit

}
