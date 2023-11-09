// SPDX-License-Identifier: Apache-2.0

package examples

import chisel3.{fromDoubleToLiteral => _, fromIntToBinaryPoint => _, _}
import fixedpoint._
import chiseltest._
import chiseltest.iotesters.PeekPokeTester
import dsptools.misc.PeekPokeDspExtensions
import dsptools.numbers._
import org.scalatest.flatspec.AnyFlatSpec
import dsptools.numbers.UIntSIntImpl._

//noinspection TypeAnnotation
class ParameterizedAdderEx[T <: Data, T2 <: Data](gen1: () => T, gen2: () => T2, gen3: () => T2)(implicit a: RingEx[T,T2]) extends Module {
  val a1: T = IO(Input(gen1().cloneType))
  val a2: T2 = IO(Input(gen2().cloneType))
  val c = IO(Output(gen3().cloneType))

  c := RegNext(a.plusContextEx(a1, a2))
}

class ParameterizedAdderTesterEx[T <: Data: Ring, T2 <: Data](c: ParameterizedAdderEx[T, T2])
    extends PeekPokeTester(c)
    with PeekPokeDspExtensions {
  for {
    i <- (BigDecimal(-2.0) to 1.0 by 0.25).map(_.toDouble)
    j <- (BigDecimal(-2.0) to 4.0 by 0.5).map(_.toDouble)
  } {
    poke(c.a1, i)
    poke(c.a2, j)
    step(1)

    val result = peek(c.c)

    expect(c.c, i + j, s"parameterize adder tester $i + $j => $result should have been ${i + j}")
  }
}

class ParameterizedAdderSpecEx extends AnyFlatSpec with ChiselScalatestTester {

  behavior.of("parameterized adder circuit with RingEx extension")
  
  it should "work with UInt and SInt" in {
    def getUInt: UInt = UInt(12.W)
    def getSInt: SInt = SInt(8.W)
    def getOut: SInt = SInt(13.W)
    test(new ParameterizedAdderEx(() => getUInt, () => getSInt, () => getOut)) { dut =>
      println(dut.a1)
      println(dut.a2)
      println(dut.c)
    }
  }
}
