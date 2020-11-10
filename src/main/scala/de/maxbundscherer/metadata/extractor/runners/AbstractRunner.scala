package de.maxbundscherer.metadata.extractor.runners

import de.maxbundscherer.metadata.extractor.services.AwsS3Service
import de.maxbundscherer.metadata.extractor.utils.{ ConfigurationHelper, LoggingHelper }

abstract class AbstractRunner(awsS3Service: AwsS3Service)
    extends ConfigurationHelper
    with LoggingHelper {

  def run: Unit

}
