package de.maxbundscherer.metadata.extractor

import de.maxbundscherer.metadata.extractor.utils.Configuration

object Main extends App with Configuration {

  import org.slf4j.{ Logger, LoggerFactory }

  private val log: Logger = LoggerFactory.getLogger("Main-Logger")

  log.info(s"Application started (${Config.helloMsg})")

}
