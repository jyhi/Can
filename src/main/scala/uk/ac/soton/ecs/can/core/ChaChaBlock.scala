// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._

class ChaChaBlock(val regBetweenRounds: Boolean = true) extends MultiIOModule {
  val roundLoop = IO(Input(Bool()))
  val initialState = IO(Input(Vec(16, UInt(32.W))))
  val in = IO(Input(Vec(16, UInt(32.W))))
  val out = IO(Output(Vec(16, UInt(32.W))))

  private val columnRound = Module(new ColumnRound)
  private val diagonalRound = Module(new DiagonalRound)
  private val betweenRounds =
    if (regBetweenRounds) Reg(Vec(16, UInt(32.W)))
    else Wire(Vec(16, UInt(32.W)))
  private val afterRounds = Reg(Vec(16, UInt(32.W)))

  private val muxRoundLoop = Mux(roundLoop, afterRounds, in)
  private val sumInRound = in.zip(afterRounds).map { case (i, r) => i + r }

  columnRound.in := muxRoundLoop
  betweenRounds := columnRound.out
  diagonalRound.in := betweenRounds
  afterRounds := diagonalRound.out
  out := sumInRound
}
