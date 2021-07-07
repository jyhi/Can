// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._
import uk.ac.soton.ecs.can.config.CanCoreConfiguration

abstract class BaseRound(implicit cfg: CanCoreConfiguration)
    extends MultiIOModule {
  val in = IO(Input(UInt(512.W)))
  val out = IO(Output(UInt(512.W)))

  protected val _in = in.asTypeOf(Vec(16, UInt(32.W)))
  protected val _out = Wire(Vec(16, UInt(32.W)))
  out := _out.asUInt()

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
