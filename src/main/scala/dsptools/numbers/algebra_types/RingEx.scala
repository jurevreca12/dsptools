// SPDX-License-Identifier: Apache-2.0

package dsptools.numbers

import chisel3.Data

/* Needs to be redefined from spire */
object RingEx {
  def apply[A <: Data: Ring, B <: Data](implicit A: RingEx[A, B]): RingEx[A,B] = A
}

trait RingEx[A, B] extends Any {
  def plusContextEx(f:   A, g: B): B
  def minusContextEx(f:  A, g: B): B
  def timesContextEx(f:  A, g: B): B
}
