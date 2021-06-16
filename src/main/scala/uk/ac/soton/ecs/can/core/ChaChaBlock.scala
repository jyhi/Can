package uk.ac.soton.ecs.can.core

import chisel3._

class ChaChaBlock extends Module {
  val io = IO(new Bundle {
    val muxIn = Input(Bool())
    val in = Input(Vec(16, UInt(32.W)))
    val out = Output(Vec(16, UInt(32.W)))
  })

  val initialState = Reg(Vec(16, UInt(32.W)))
  val doubleRound = Module(new ChaChaInnerBlock(regBetweenRounds = true))
  val doubleRoundState = Reg(Vec(16, UInt(32.W)))

  initialState := io.in
  doubleRound.io.in := Mux(io.muxIn, initialState, doubleRoundState)
  doubleRoundState := doubleRound.io.out

  val addedState = doubleRoundState.zip(initialState).map(t => t._1 + t._2)

  io.out := addedState
}
