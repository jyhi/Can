// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._

class ColumnRound extends MultiIOModule {
  val in = IO(Input(Vec(16, UInt(32.W))))
  val out = IO(Output(Vec(16, UInt(32.W))))

  Seq(
    Seq(0, 4, 8, 12),
    Seq(1, 5, 9, 13),
    Seq(2, 6, 10, 14),
    Seq(3, 7, 11, 15)
  ).foreach { roundWires =>
    val quarterRound = Module(new QuarterRound)
    roundWires.zipWithIndex.foreach { roundWire =>
      quarterRound.in(roundWire._2) := in(roundWire._1)
      out(roundWire._1) := quarterRound.out(roundWire._2)
    }
  }
}
