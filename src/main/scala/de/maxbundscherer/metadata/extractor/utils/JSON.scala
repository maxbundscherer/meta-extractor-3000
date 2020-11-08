package de.maxbundscherer.metadata.extractor.utils

class JSON {

  import de.maxbundscherer.metadata.extractor.aggregates.AwsAggregate

  import com.amazonaws.services.s3.model.S3ObjectSummary
  import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
  import scala.util.Try

  def convertCacheToJSON(data: Vector[AwsAggregate.FileKey]): Try[String] =
    Try(data.asJson.noSpaces)

  def convertCacheFromJSON(data: String): Try[Vector[AwsAggregate.FileKey]] =
    Try {
      decode[Vector[AwsAggregate.FileKey]](data) match {
        case Left(error)  => throw error
        case Right(value) => value
      }
    }

}
