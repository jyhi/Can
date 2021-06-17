// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._

class ChaChaInnerBlock(regBetweenRounds: Boolean) extends MultiIOModule {
  val in = IO(Input(Vec(16, UInt(32.W))))
  val out = IO(Output(Vec(16, UInt(32.W))))

  val betweenRounds =
    if (regBetweenRounds)
      Reg(Vec(16, UInt(32.W)))
    else
      Wire(Vec(16, UInt(32.W)))

  val columnRound = Module(new ColumnRound)
  val diagonalRound = Module(new DiagonalRound)

  columnRound.in := in
  betweenRounds := columnRound.out
  diagonalRound.in := betweenRounds
  out := diagonalRound.out
}
