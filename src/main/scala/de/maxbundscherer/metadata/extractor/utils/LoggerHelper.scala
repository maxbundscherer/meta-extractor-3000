package de.maxbundscherer.metadata.extractor.utils

trait LoggerHelper {

  import org.slf4j.{ Logger, LoggerFactory }

  val log: Logger = LoggerFactory.getLogger(this.getClass.getSimpleName)

}
