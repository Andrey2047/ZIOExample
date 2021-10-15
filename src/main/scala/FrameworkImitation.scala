import zio.Console.printLine
import zio.stream.ZStream
import zio.{App, Clock, Console, ExitCode, Schedule, Task, URIO, ZIO, durationInt}

object FrameworkImitation extends App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    run2
  }

  private def run1 = {
    ZStream.fromIterable(Seq(1, 2, 3, 4)).foreach(n => {
      ZIO.raceAll(processingTask(n), Seq(heartBeatTask(n)))
    }).exitCode
  }

  private def run2 = {
    ZStream.fromIterable(Seq(1, 2, 3, 4)).foreach(n => {
      for {
        processTask <- processingTask(n).fork
        heartBeatTask <- heartBeatTask(n).catchAll(ex => {
          Console.printLine(s"exception during heartbeat ${ex.getMessage}") *> processTask.interrupt
        }).fork
        ll <- processTask.join
        _ <- heartBeatTask.interrupt
      } yield {
        ll
      }
    }).exitCode
  }

  def processingTask(number: Int): Task[Unit] = {
    printLine(println(s"execution task $number. Thread - ${Thread.currentThread()}"))
      .delay(1 second)
      .provideLayer(Clock.live ++ Console.live)
  }

  def heartBeatTask(number: Int): Task[Unit] = {
    printLine(s"tick $number. Thread - ${Thread.currentThread()}")
      .repeat(Schedule.spaced(500 millisecond) && Schedule.forever)
      .flatMap(v => printLine(s" ticks number ${v._1}"))
        .provideLayer(Clock.live ++ Console.live)
  }
}
