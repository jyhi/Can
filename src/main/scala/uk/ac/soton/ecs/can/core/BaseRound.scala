// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._
import chisel3.util.Cat
import uk.ac.soton.ecs.can.config.CanCoreConfiguration

abstract class BaseRound(implicit cfg: CanCoreConfiguration)
    extends MultiIOModule {
  val in = IO(Input(UInt(512.W)))
  val out = IO(Output(UInt(512.W)))

  // NOTE: A conversion between an UInt and an aggregate type reverses the
  // sequence of elements. The `_in` cast below reverses such reversal by
  // manually creating a `Vec` with the elements in the casted `Vec` reversed.
  // Same for the `out` connection, where `Cat`, which concatenates elements
  // from the most significant element to the least significant element, is used
  // instead of `_out.asUInt()`, which puts the first element in the `Vec` to
  // the least significant position of `UInt`.
  //
  // See also:
  // - https://github.com/chipsalliance/chisel3/blob/master/core/src/main/scala/chisel3/Data.scala#L695-L696
  protected val _in = VecInit(in.asTypeOf(Vec(16, UInt(32.W))).reverse)
  protected val _out = Wire(Vec(16, UInt(32.W)))
  out := Cat(_out)

  protected def wire(wireBox: Seq[Seq[Int]]): Unit = wireBox.foreach {
    wireSeq =>
      val quarterRound = cfg.quarterRoundType match {
        case 1 => Module(new CombinationalQuarterRound)
        case 2 => Module(new TwoStageQuarterRound)
        case 8 => Module(new EightStageQuarterRound)
        case _ =>
          throw new Exception("quarterRoundType should be either 1, 2, or 8")
      }
      quarterRound.in.zip(quarterRound.out).zip(wireSeq).foreach {
        case ((i, o), w) =>
          i := _in(w)
          _out(w) := o
      }
  }
}
