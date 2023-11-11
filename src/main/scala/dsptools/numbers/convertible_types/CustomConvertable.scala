// SPDX-License-Identifier: Apache-2.0

package dsptools.numbers

import chisel3._

object CustomConvertableTo {
  def apply[A <: Data](implicit c: CustomConvertableTo[A]): CustomConvertableTo[A] = c
}
trait CustomConvertableTo[A <: Data] extends Any {
  def fromUInt(a: UInt): A
  def fromSInt(a: SInt): A
  def fromType[B <: Data: CustomConvertableFrom](b: B): A
}

trait CustomConvertableToSInt extends CustomConvertableTo[SInt] {
  def fromUInt(a: UInt): SInt = a.asSInt
  def fromSInt(a: SInt): SInt = a
  def fromType[B <: Data: CustomConvertableFrom](b: B): SInt = CustomConvertableFrom[B].toSInt(b)
}


object CustomConvertableFrom {
  def apply[A <: Data](implicit c: CustomConvertableFrom[A]): CustomConvertableFrom[A] = c
}

trait CustomConvertableFrom[A <: Data] extends Any {
  def toUInt(a: A): UInt
  def toSInt(a: A): SInt
  def toType[B <: Data: CustomConvertableTo](a: A): B
}


trait CustomConvertableFromUInt extends CustomConvertableFrom[UInt] {
  def toUInt(a: UInt): UInt = a
  def toSInt(a: UInt): SInt = a.asSInt
  def toType[B <: Data: CustomConvertableTo](a: UInt) = CustomConvertableTo[B].fromUInt(a)
}
