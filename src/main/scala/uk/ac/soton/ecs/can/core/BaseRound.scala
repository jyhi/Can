// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._

abstract class BaseRound extends MultiIOModule {
  val in = IO(Input(UInt(512.W)))
  val out = IO(Output(UInt(512.W)))

  protected val _in = in.asTypeOf(Vec(16, UInt(32.W)))
  protected val _out = Wire(Vec(16, UInt(32.W)))
  out := _out.asUInt()

  protected def wire(wireBox: Seq[Seq[Int]]): Unit = wireBox.foreach {
    wireSeq =>
      val quarterRound = Module(new CombinationalQuarterRound)
      quarterRound.in.zip(quarterRound.out).zip(wireSeq).foreach {
        case ((i, o), w) =>
          i := _in(w)
          _out(w) := o
      }
  }
}
