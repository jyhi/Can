// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._

class DiagonalRound extends MultiIOModule {
  val in = IO(Input(Vec(16, UInt(32.W))))
  val out = IO(Output(Vec(16, UInt(32.W))))

  Seq(
    Seq(0, 5, 10, 15),
    Seq(1, 6, 11, 12),
    Seq(2, 7, 8, 13),
    Seq(3, 4, 9, 14)
  ).foreach { roundWires =>
    val quarterRound = Module(new QuarterRound)
    roundWires.zipWithIndex.foreach { roundWire =>
      quarterRound.in(roundWire._2) := in(roundWire._1)
      out(roundWire._1) := quarterRound.out(roundWire._2)
    }
  }
}
