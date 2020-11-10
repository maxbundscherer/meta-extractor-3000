package de.maxbundscherer.metadata.extractor.services

import de.maxbundscherer.metadata.extractor.utils.{ ConfigurationHelper, JsonHelper }

class FileService() extends ConfigurationHelper with JsonHelper {

  import de.maxbundscherer.metadata.extractor.aggregates.AwsS3Aggregate

  import com.amazonaws.services.s3.model.S3ObjectSummary
  import scala.util.Try
  import better.files._
  import java.io.{ File => JFile }
  import scala.util.{ Failure, Success }

  private val fileKeysFilename: String = "aws_fileInfos.json"

  /**
    * Get file info from cache
    * @return FileInfos
    */
  def getCachedAwsFileInfos: Try[Vector[AwsS3Aggregate.FileInfo]] =
    Try {
      val jsonData: String =
        File(s"${Config.Global.cacheDirectory}$fileKeysFilename").contentAsString
      Json.AwsS3.convertFileInfosFromJson(jsonData) match {
        case Failure(exception) => throw exception
        case Success(fileKeys)  => fileKeys
      }
    }

  /**
    * Write file info to cache
    * @param data FileInfos
    * @return Try with filePath
    */
  def writeCachedAwsFileInfos(data: Vector[AwsS3Aggregate.FileInfo]): Try[String] =
    Try {
      Json.AwsS3.convertFileInfosToJson(data) match {
        case Failure(exception) => throw exception
        case Success(jsonContent) =>
          File(s"${Config.Global.cacheDirectory}")
            .createDirectoryIfNotExists()
          File(s"${Config.Global.cacheDirectory}$fileKeysFilename")
            .createFileIfNotExists()
            .write(jsonContent)
            .pathAsString
      }
    }

}
