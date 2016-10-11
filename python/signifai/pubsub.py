# Copyright 2016 SignifAI, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import sys
if sys.version >= "3":
    from io import BytesIO
else:
    from StringIO import StringIO

from pyspark.serializers import PairDeserializer, NoOpSerializer, UTF8Deserializer, read_int
from pyspark.streaming import DStream




class PubsubUtils:


    def utf8_decoder(s):
        """ Decode the unicode as UTF-8 """
        if s is None:
            return None
        return s.decode('utf-8')


    @staticmethod
    def _toPythonDStream(ssc, jstream, bodyDecoder):
        ser = PairDeserializer(NoOpSerializer(), NoOpSerializer())
        stream = DStream(jstream, ssc, ser)

        return stream

    @staticmethod
    def createStream(ssc, subscription, batchSize, decodeData, bodyDecoder=utf8_decoder):
        jstream = ssc._sc._jvm.io.signifai.pubsub_spark.receiver.PubsubReceiverInputDStream(ssc._jssc, subscription, batchSize, decodeData)
        return PubsubUtils._toPythonDStream(ssc, jstream, bodyDecoder)
