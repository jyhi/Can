// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._
import uk.ac.soton.ecs.can.config.CanCoreConfiguration

class Round(implicit cfg: CanCoreConfiguration) extends MultiIOModule {
  val roundSelect = IO(Input(Bool()))
  val in = IO(Input(UInt(512.W)))
  val out = IO(Output(UInt(512.W)))

  private val columnarRound = Module(new ColumnarRound)
  private val diagonalRound = Module(new DiagonalRound)

  columnarRound.in := in
  diagonalRound.in := in
  out := Mux(roundSelect, columnarRound.out, diagonalRound.out)
}
