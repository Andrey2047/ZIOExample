trait ZIOt[+A] { self =>
  def run(callback : A => Unit)

  def zip[B](that: ZIOt[B]): ZIOt[(A,B)] = Zip(self, that)

  def flatMap[B](f: A => ZIOt[B]): ZIOt[B] = FlatMap(self, f)
}

case class Succeed[A](value: A) extends ZIOt[A] {
  override def run(callback: A => Unit): Unit = {
    callback(value)
  }
}

case class Effect[A](f: () => A) extends ZIOt[A] {
  override def run(callback: A => Unit): Unit = {
    callback(f())
  }
}

case class Zip[A, B](left: ZIOt[A], right: ZIOt[B]) extends ZIOt[(A, B)] {
  override def run(callback: ((A, B)) => Unit): Unit = left.run{ a =>
    right.run { b =>
      callback((a,b))
    }
  }
}

case class FlatMap[A, B](z: ZIOt[A],f: A => ZIOt[B]) extends ZIOt[B] {
  override def run(callback: B => Unit): Unit = {
    z.run(z => {
      f(z).run(b => callback(b))
    })
  }
}

object ZIOt {
  def succeed[A](a: => A):ZIOt[A] = Effect(() => a)
  def zip[A,B](a: ZIOt[A], b: ZIOt[B]):ZIOt[(A,B)] = Zip(a, b)

  def main(args: Array[String]): Unit = {
    (ZIOt.succeed(10) zip ZIOt.succeed("LO")).run(println(_))
  }
}