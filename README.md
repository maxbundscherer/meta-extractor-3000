# Metadata Extractor 3000

Metadata-Extractor written in scala. Extract metadata from videos, audios, files and more local and by using aws-cloud-services

``metadata - extractor - local - aws - scala``

Tested on macOS (with openJDK-11)

[![shields.io](http://img.shields.io/badge/license-Apache2-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)

Author: [Maximilian Bundscherer](https://bundscherer-online.de)

## Features

- ✅ Get s3 buckets
- ✅ Get s3 buckets fileKeys (json-cached included)
- ✅ Different runners included
- ...

## I still have things to do...

- ➡️ tbd
- ...

## Let's get started

### Required

- ``java``
- ``sbt`` (only as developer)

### Let's go (developer)

- Read [config](src/main/scala/de/maxbundscherer/metadata/extractor/utils/ConfigurationHelper.scala)
- Run with ``sbt run``
- Clear cache with ``rm -rf cache/``
- Triggered Restart (recommend) ``sbt ~reStart``
