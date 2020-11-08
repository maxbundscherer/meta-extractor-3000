package de.maxbundscherer.metadata.extractor.utils

class JSON {

  import com.amazonaws.services.s3.model.S3ObjectSummary
  import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
  import scala.util.Try

  def convertCacheToJSON(data: Vector[S3ObjectSummary]): Try[String] =
    Try(data.asJson.noSpaces)

  def convertCacheFromJSON(data: String): Try[Vector[S3ObjectSummary]] =
    Try {
      decode[Vector[S3ObjectSummary]](data) match {
        case Left(error)  => throw error
        case Right(value) => value
      }
    }

}
