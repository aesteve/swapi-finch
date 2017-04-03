package com.github.aesteve.finch.examples

package object swapi {
  class Pipe[A](a: A) {
    def |>[B](f: A => B): B = f(a)
  }

  object Pipe {
    def apply[A](v: A) = new Pipe(v)
  }

  object PipeOps {
    implicit def toPipe[A](a: A): Pipe[A] = Pipe(a)
  }
}
