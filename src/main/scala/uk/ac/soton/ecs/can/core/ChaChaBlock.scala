// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._

class ChaChaBlock extends MultiIOModule {
  val muxIn = IO(Input(Bool()))
  val in = IO(Input(Vec(16, UInt(32.W))))
  val out = IO(Output(Vec(16, UInt(32.W))))

  val initialState = Reg(Vec(16, UInt(32.W)))
  val doubleRound = Module(new ChaChaInnerBlock(regBetweenRounds = true))
  val doubleRoundState = Reg(Vec(16, UInt(32.W)))

  initialState := in
  doubleRound.in := Mux(muxIn, initialState, doubleRoundState)
  doubleRoundState := doubleRound.out

  val addedState = doubleRoundState.zip(initialState).map(t => t._1 + t._2)

  out := addedState
}
