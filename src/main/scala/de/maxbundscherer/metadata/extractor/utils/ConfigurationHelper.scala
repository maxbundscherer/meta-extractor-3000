package de.maxbundscherer.metadata.extractor.utils

trait ConfigurationHelper {

  import com.typesafe.config.ConfigFactory

  object Config {

    private val conf = ConfigFactory.load()

    object Global {
      private val c              = conf.getConfig("global")
      val message: String        = c.getString("message")
      val cacheDirectory: String = c.getString("cache-directory")
    }

    object AwsClients {

      private val cAws = conf.getConfig("aws-clients")
      object S3 {
        private val c          = cAws.getConfig("s3")
        val accessKey: String  = c.getString("access-key")
        val secretKey: String  = c.getString("secret-key")
        val bucketName: String = c.getString("bucket-name")
      }

    }

  }

}
