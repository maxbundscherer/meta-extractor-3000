package de.maxbundscherer.metadata.extractor.runners

import de.maxbundscherer.metadata.extractor.services.AwsS3Service
import de.maxbundscherer.metadata.extractor.utils.Configuration

import org.slf4j.Logger

abstract class AbstractRunner(awsS3Service: AwsS3Service)(implicit log: Logger)
    extends Configuration {

  def run: Unit

}
