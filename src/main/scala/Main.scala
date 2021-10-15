import zio.Console.printLine
import zio._
import zio.stream.{ZSink, ZStream, ZTransducer}

import java.io.IOException

object Main extends App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    (for {
        queue    <- ZQueue.bounded[Int](32)
        producer <- ZStream
          .iterate(1)(_ + 1)
          .fixed(1200.millis)
          .run(ZSink.fromQueue(queue))
          .fork
        consumer <- queue.take.flatMap(printLine(_)).forever
        _        <- producer.zip(consumer).join
      } yield ()).exitCode
  }
}