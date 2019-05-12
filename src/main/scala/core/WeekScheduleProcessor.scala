package me.alaz.interview.wolt.core

import java.time.DayOfWeek
import scala.util.{Failure, Success, Try}
import me.alaz.interview.wolt.api.WeekSchedule

// Keeping it simple for now: only one kind of error. However the algorithm has
// some preparations for more elaborate error processing/recovery.
sealed trait WeekSchedulerProcessorException extends Exception
case object MalformedInputException extends WeekSchedulerProcessorException

// @see me.alaz.interview.wolt.api.WeekSchedule
// @see InputMalformedWarning
case class WeekScheduleProcessor(schedule: WeekSchedule) {
  // The idea is to normalize the input keeping record of errors inside. This makes it possible
  // to recover from some errors and/or return meaningful information to a user as a response.
  lazy val normalized: Map[DayOfWeek, Option[Seq[Try[Range]]]] = {
    def rotateLeft[T](s: Seq[T]): Seq[T] = s.tail :+ s.head

    // The algorithm below is a good quick way to re-arrange closing end ranges spilling
    // to the next day. It will work OK in the case of failing fast, e.g. throwing an
    // exception as soon as the malformed data is found. However, it will complicate things
    // in the case more human-readable warnings need to be output, because it detects problem
    // after the rearrangement. For example, in the case of a "closing" hour, it could be
    // already transferred to the previous day and the problem will be reported on that day too.
    //
    // If the accurate attribution of errors is needed, then the algorithm should be changed
    // or there should be a way to track which input day a `OpenHours` belongs to for correct
    // error reporting.
    //
    // Another option would be to better check the input's shape: a "Validator".

    val matrix = schedule.matrix
    val (closing, tails) = matrix
      .map(_.getOrElse(Seq.empty))
      .map {
        case seq if seq.nonEmpty && seq.head.isClosing => (Seq(seq.head), seq.tail)
        case seq => (Seq.empty, seq)
      }
      .unzip

    val ranges = for {
      day <- tails.zip(rotateLeft(closing)).map { t => t._1 ++ t._2 }
    } yield {
      day.grouped(2).map {
        case Seq(begin, end) if begin.isOpening && end.isClosing =>
          Success(Range.inclusive(begin.value, end.value))

        case _ =>
          // Possible to store more information in errors:
          // (1) duplicate types;
          // (2) a single end.
          Failure(MalformedInputException)
      }.toSeq
    }

    // Original `matrix` contains information on those days the venue is closed.
    // Other days do not matter.
    val recollectClosed = for {
      (seq, index) <- ranges.zipWithIndex if matrix(index).nonEmpty
    } yield {
      DayOfWeek.of(index+1) -> (if (seq.nonEmpty) Some(seq) else None)
    }

    recollectClosed.toMap
  }

  // Future enhancements might provide more information about the severity (instead of
  // binary `Success` / `Failure`), so that a controller can decide what to do and call
  // either `errors` or `recover`.
  def isSuccess = normalized.values.flatten.flatten.forall(_.isSuccess)
  def isFailure = !isSuccess

  def apply(): Map[DayOfWeek, Option[Seq[Range]]] =
    normalized.mapValues(_.map { _.collect { case Success(range) => range } })

  def errors: Map[DayOfWeek, Seq[Throwable]] =
    normalized.mapValues(_.getOrElse(Seq.empty).collect { case Failure(f) => f })

  // Potentially it is possible to recover from some malformed input, but impossible from other.
  // (1) if the error is the last in day's sequence, maybe it could be safely ignored;
  // (2) ???
  def recover: Try[Map[DayOfWeek, Seq[Range]]] = ???
}
