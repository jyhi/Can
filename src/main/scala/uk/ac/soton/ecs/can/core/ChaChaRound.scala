// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._

class ChaChaRound(wires: Seq[Seq[Int]]) extends MultiIOModule {
  val in = IO(Input(Vec(16, UInt(32.W))))
  val out = IO(Output(Vec(16, UInt(32.W))))

  wires.foreach { qRWires =>
    val quarterRound = Module(new QuarterRound)

    qRWires.zipWithIndex.foreach { case (qRWire, index) =>
      quarterRound.in(index) := in(qRWire)
      out(qRWire) := quarterRound.out(index)
    }
  }
}

object ChaChaRound {
  def columnar = new ChaChaRound(
    Seq(
      Seq(0, 4, 8, 12),
      Seq(1, 5, 9, 13),
      Seq(2, 6, 10, 14),
      Seq(3, 7, 11, 15)
    )
  )

  def diagonal = new ChaChaRound(
    Seq(
      Seq(0, 5, 10, 15),
      Seq(1, 6, 11, 12),
      Seq(2, 7, 8, 13),
      Seq(3, 4, 9, 14)
    )
  )
}
