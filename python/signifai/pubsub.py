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
