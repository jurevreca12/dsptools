// SPDX-License-Identifier: Apache-2.0

package examples

import chisel3.{fromDoubleToLiteral => _, fromIntToBinaryPoint => _, _}
import chisel3.experimental.VecLiterals._
import fixedpoint._
import chiseltest._
import chiseltest.simulator.WriteVcdAnnotation
import chiseltest.iotesters.PeekPokeTester
//import dsptools.misc.PeekPokeDspExtensions
import dsptools.numbers._
//import dsptools.numbers.implicits._
import org.scalatest.flatspec.AnyFlatSpec


class ParameterizedNeuron[I <: Bits: CustomConvertableFrom, W <: Bits: CustomConvertableTo](genI: I, genW: W)(implicit c: Ring[W]) extends Module {
  val inputs = IO(Input(Vec(3, genI)))
  val weights = IO(Input(Vec(3, genW)))
  val out = IO(Output(genW))

  val products: Seq[W] = (inputs zip weights).map { case (i, w) =>
    val t: W = implicitly[CustomConvertableTo[W]].fromType(i)
    c.timesContext(w, t)
  }
  out := VecInit(products).reduceTree(c.plusContext(_, _))
}

class ParameterizedNeuronSpec extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "parameterized neuron" 
    implicit final val CustomConvertableFromUInt: CustomConvertableFrom[UInt] = new CustomConvertableFromUInt {}
    implicit final val CustomConvertableToSInt: CustomConvertableTo[SInt] = new CustomConvertableToSInt {}
    def getUInt: UInt = UInt(4.W)
    def getSInt: SInt = SInt(3.W)
    def getOut: UInt = UInt(5.W)
    def getFP: FixedPoint = FixedPoint(12.W, 0.BP)
    def relu(act: SInt, thresh: SInt): UInt = Mux((act - thresh) > 0.S, (act - thresh).asUInt, 0.U)
    it should "work with UInt and SInt" in {
      test(new ParameterizedNeuron(getUInt, getSInt)) { dut =>
        println(dut.inputs)
        println(dut.weights)
        println(dut.out)
      }
    }
}

