package de.maxbundscherer.metadata.extractor.services

import de.maxbundscherer.metadata.extractor.utils.JsonHelper

class LocalFileService(cacheService: CacheService) extends AbstractService with JsonHelper {

  import de.maxbundscherer.metadata.extractor.aggregates.LocalAggregate

  import scala.util.Try
  import better.files._
  import java.io.{ File => JFile }
  import scala.util.{ Failure, Success }

  /**
    * Get FileInfos from local dir (updates cache too)
    * @param useCache Boolean
    * @return FileInfos
    */
  def getFileInfos(useCache: Boolean): Try[Vector[LocalAggregate.FileInfo]] = {

    val cache: Option[Vector[LocalAggregate.FileInfo]] =
      if (!useCache) None
      else
        this.cacheService.getCachedLocalFileInfos match {
          case Failure(exception) =>
            log.warn(s"Cache read getFileInfos exception (${exception.getLocalizedMessage})")
            None
          case Success(value) => Some(value)
        }

    cache match {
      case Some(value) =>
        log.info(s"Use cache for getFileInfos ${value.length} items found")
        Try(value)
      case None =>
        log.info("No cache for getFileInfos. Process now")
        Try {
          val ans: Vector[LocalAggregate.FileInfo] = {
            val dir = File(Config.Runners.DebugRunner.localWorkDir)
            dir.listRecursively.toVector
              .map(f => LocalAggregate.FileInfo(filePath = f.toString()))
          }
          this.cacheService.writeCachedLocalFileInfos(ans) match {
            case Failure(exception) =>
              log.error(s"Error in write cache (${exception.getLocalizedMessage})")
            case Success(filePath) => log.info(s"Write cache success ($filePath)")
          }
          ans
        }
    }

  }

}
