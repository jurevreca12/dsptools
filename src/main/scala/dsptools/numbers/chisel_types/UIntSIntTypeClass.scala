// SPDX-License-Identifier: Apache-2.0

package dsptools.numbers

import chisel3.{fromDoubleToLiteral => _, fromIntToBinaryPoint => _, _}
import chisel3.util.{Cat, ShiftRegister}
import dsptools.{hasContext, DspContext, DspException, Grow, Saturate, Wrap}
import fixedpoint._

import scala.language.implicitConversions

/**
  * Defines basic math functions for UInt
  */
trait UIntSIntRingEx extends Any with RingEx[UInt, SInt] with hasContext {
  def plusContextEx(f: UInt, g: SInt): SInt = {
    // TODO: Saturating mux should be outside of ShiftRegister
    val sum = context.overflowType match {
      case Grow => f.asSInt +& g
      case Wrap => f.asSInt +% g
      case _    => throw DspException("Saturating add hasn't been implemented")
    }
    ShiftRegister(sum, context.numAddPipes)
  }
  def minusContextEx(f: UInt, g: SInt): SInt = {
    val diff = context.overflowType match {
      case Grow => throw DspException("OverflowType Grow is not supported for UInt subtraction")
      case Wrap => f.asSInt -% g
      case _    => throw DspException("Saturating subtractor hasn't been implemented")
    }
    ShiftRegister(diff.asSInt, context.numAddPipes)
  }
  def timesContextEx(f: UInt, g: SInt): SInt = {
    ShiftRegister(f * g, context.numMulPipes)
  }
}
object UIntSIntImpl {
  implicit object UIntSIntIntegerImpl extends UIntInteger with UIntSIntRingEx
}