package de.maxbundscherer.metadata.extractor.utils

trait ConfigurationHelper {

  object Config {

    object Global {
      val message: String        = "Hello world!"
      val cacheDirectory: String = "cache/"
    }

    object AwsClients {

      object S3 {
        val accessKey: String  = "tbd"
        val secretKey: String  = "tbd"
        val bucketName: String = "tbd"
      }

    }

  }

}
