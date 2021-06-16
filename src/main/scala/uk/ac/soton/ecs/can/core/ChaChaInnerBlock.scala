package uk.ac.soton.ecs.can.core

import chisel3._

class ChaChaInnerBlock(regBetweenRounds: Boolean) extends Module {
  val io = IO(new Bundle {
    val in = Input(Vec(16, UInt(32.W)))
    val out = Output(Vec(16, UInt(32.W)))
  })

  val betweenRounds =
    if (regBetweenRounds)
      Reg(Vec(16, UInt(32.W)))
    else
      Wire(Vec(16, UInt(32.W)))

  val columnRound = Module(new ColumnRound)
  val diagonalRound = Module(new DiagonalRound)

  columnRound.io.in := io.in
  betweenRounds := columnRound.io.out
  diagonalRound.io.in := betweenRounds
  io.out := diagonalRound.io.out
}
