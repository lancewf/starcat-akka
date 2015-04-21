package org.finfrock.starcat.util

object CircularQueue {
  private val DEFAULT_QUEUE_LENGTH = 3000
}
/*
 * This is a class for a circular queue. Its primary intended purpose is for
 * supporting worker threads in a producer-consumer pattern. The producer 
 * thread and the consumer thread share a queue. The producer calls 
 * push() to add its output to the queue, while the consumer 
 * calls pop() to get its next job out of the queue.
 *
 * That said, this class is NOT thread-safe. It is typically 
 * used as a base class by SynchronizedCircularQueue which 
 * is thread-safe.
 *
 * This class works by maintaining a circular list of entries. 
 * You push() to one end and pop() from the other. 
 * If the number of entries you push() exceeds the number 
 * you have pop()'ed by more than the queue length 
 * (a construction parameter), then length of the queue
 *
 */
class CircularQueue[T](queueLength: Int = CircularQueue.DEFAULT_QUEUE_LENGTH) {

  if (queueLength < 1) {
    throw new IllegalArgumentException
    ("Cannot form a queue of non-positive length");
  }
  private var queue: Array[Option[T]] = Array.fill[Option[T]](queueLength)(None)
  private var head: Int = queueLength - 1
  private var tail: Int = queueLength - 1
  private var sizeInternal: Int = 0

  /**
   * Adds an object to the queue. If the object to be queued is null, the
   * push is ignored.
   *
   */
  def push(o: T) {
    if (o == null) {
      return ;
    }

    //We add at the tail and remove from the head. So...
    //Advance the tail of the queue and check for wrap around.
    //this is complicated a bit by the fact that we might 
    //overwrite the head. If we do, then we need to advance 
    //the head. Fortunately, if
    //we advance the head, then the slot we advance it to is guaranteed
    //not to be null because we forbid enqueueing null objects.
    tail += 1

    if (tail == queueLength) {
      tail = 0;
    }
    //If we are overwriting the head of the queue...
    if (tail == head && queue(head) != None) {
      head += 1
      if (head == queueLength) {
        head = 0;
      }
      sizeInternal -= 1
    }
    queue(tail) = Some(o)

    //There is a special case here if the queue was empty. 
    //For instance,
    //when we first create it. If the queue was empty, then the 
    //head needs to be advanced so we can pop the right guy. 
    //If the queue was not empty, then the head is unchanged.
    if (queue(head) == None) {
      head = tail;
    }
    sizeInternal += 1
  }

  /**
   * Removes an object from the queue.
   *
   * @return The oldest object on the queue, or null
   * if the queue is empty.
   */
  def pop(): Option[T] = {
    //  Grab the result and empty the slot it was in.
    val result = queue(head)
    queue(head) = None

    //  Advance the head and check for wrap around.
    head += 1
    if (head == queueLength) {
      head = 0;
    }

    if (isEmpty()) {
      sizeInternal = 0;
    } else {
      sizeInternal -= 1
    }
    return result;
  }

  /**
   * If the queue was
   * overwritten at any time, the size of the queue for the overwritten
   * push(Object) operation remains unchanged, as one
   * object was lost while another was put in its place.
   */
  def size(): Int = sizeInternal

  def isEmpty(): Boolean = queue(head).isEmpty
}