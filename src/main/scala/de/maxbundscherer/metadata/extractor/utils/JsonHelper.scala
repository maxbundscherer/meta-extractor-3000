package de.maxbundscherer.metadata.extractor.utils

trait JsonHelper {

  import de.maxbundscherer.metadata.extractor.aggregates.LocalAggregate
  import de.maxbundscherer.metadata.extractor.aggregates.AwsS3Aggregate

  import com.amazonaws.services.s3.model.S3ObjectSummary
  import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
  import scala.util.Try

  object Json {

    object AwsS3 {
      def convertFileInfosToJson(data: Vector[AwsS3Aggregate.FileInfo]): Try[String] =
        Try(data.asJson.noSpaces)

      def convertFileInfosFromJson(json: String): Try[Vector[AwsS3Aggregate.FileInfo]] =
        Try {
          decode[Vector[AwsS3Aggregate.FileInfo]](json) match {
            case Left(error)  => throw error
            case Right(value) => value
          }
        }
    }

    object Local {
      def convertFileInfosToJson(data: Vector[LocalAggregate.FileInfo]): Try[String] =
        Try(data.asJson.noSpaces)
      def convertFileInfosFromJson(json: String): Try[Vector[LocalAggregate.FileInfo]] =
        Try {
          decode[Vector[LocalAggregate.FileInfo]](json) match {
            case Left(error)  => throw error
            case Right(value) => value
          }
        }
    }

  }

}
