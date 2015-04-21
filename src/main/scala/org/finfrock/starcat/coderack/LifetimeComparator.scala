package org.finfrock.starcat.coderack

import org.joda.time.DateTime
import java.util.Comparator

class LifetimeComparator extends Comparator[Any] {
  /**
   * Compares two values. Assumes both values are either Date's or
   * CodeletGroupPair's. In the latter case, it peels the death date from the
   * CodeletGroupPair and uses that for comparison. Other than that, uses
   * standard comparison semantics for longs.
   *
   * @param o1
   *            Had better be a Double or UrgenncyGroup.
   * @param o2
   *            Had also better be a Double or UrgencyGroup.
   *
   * @return 1 if o1<o2, -1 if o1>o2, 0 otherwise.
   */
  def compare(o1: Any, o2: Any): Int = {
    var death1 = 0L
    var death2 = 0L
    var seq1 = Long.MaxValue
    var seq2 = Long.MaxValue

    // We use class equivalence rather than instanceof for performance.

    o1 match {
      case date: DateTime => {
        death1 = date.getMillis
      }
      case cgp: CodeletGroupPair => {
        death1 = cgp.codelet.timeToDie.getMillis
        seq1 = cgp.sequenceNumber
      }
    }

    o2 match {
      case date: DateTime => {
        death2 = date.getMillis
      }
      case cgp: CodeletGroupPair => {
        death2 = cgp.codelet.timeToDie.getMillis
        seq2 = cgp.sequenceNumber
      }
    }

    // Now do the real comparison. Death times trump sequence numbers.
    // The sequence numbers are only here to get uniqueness (no two
    // CodeletGroupPair's with the same death time will have the same
    // sequence number. So if the death times are different, that's
    // enough to make the call. If the death times are the same, decide
    // based on sequence number.
    if (death1 < death2) {
      return -1;
    } else if (death1 > death2) {
      return 1;
    }
    // At this point, we know that the death times are the same. So
    // compare sequence numbers.
    if (seq1 < seq2) {
      return -1;
    } else if (seq1 > seq2) {
      return 1;
    } else {
      return 0;
    }
  }
}