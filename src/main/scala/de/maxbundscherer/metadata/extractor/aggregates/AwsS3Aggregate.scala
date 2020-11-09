package de.maxbundscherer.metadata.extractor.aggregates

object AwsS3Aggregate {

  case class FileInfo(fileKey: String, sizeInByte: Long)
  case class Bucket(name: String)

}
