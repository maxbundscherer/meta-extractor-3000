package de.maxbundscherer.metadata.extractor.utils

trait Configuration {

  object Config {

    object Global {
      val startUpMessage: String = "Hello world!"
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
