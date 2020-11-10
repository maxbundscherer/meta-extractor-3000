package de.maxbundscherer.metadata.extractor.utils

trait JsonHelper {

  import de.maxbundscherer.metadata.extractor.aggregates.AwsS3Aggregate

  import com.amazonaws.services.s3.model.S3ObjectSummary
  import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
  import scala.util.Try

  object Json {

    def convertAwsFileInfosToJSON(data: Vector[AwsS3Aggregate.FileInfo]): Try[String] =
      Try(data.asJson.noSpaces)

    def convertAwsFileInfosFromJSON(json: String): Try[Vector[AwsS3Aggregate.FileInfo]] =
      Try {
        decode[Vector[AwsS3Aggregate.FileInfo]](json) match {
          case Left(error)  => throw error
          case Right(value) => value
        }
      }

  }

}
