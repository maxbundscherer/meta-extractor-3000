package de.maxbundscherer.metadata.extractor.aws.aggregates

//TODO: Rename it to s3
object AwsAggregate {

  case class FileInfo(fileKey: String, sizeInByte: Long)
  case class Bucket(name: String)

}
