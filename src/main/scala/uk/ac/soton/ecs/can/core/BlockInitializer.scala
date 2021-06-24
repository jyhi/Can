package uk.ac.soton.ecs.can.core

import chisel3._

class BlockInitializer extends MultiIOModule {
  val fillConstants = IO(Input(Bool()))
  val incrementBlockCount = IO(Input(Bool()))
  val in = IO(Input(Vec(16, UInt(32.W))))
  val out = IO(Output(Vec(16, UInt(32.W))))

  private val constants = VecInit(
    "h61707865".U(32.W),
    "h3320646e".U(32.W),
    "h79622d32".U(32.W),
    "h6b206574".U(32.W)
  )
  private val incrementedBlockCount = in(12) + 1.U(32.W)

  private val io = in.zip(out)

  io.take(4).zip(constants).foreach { case ((i, o), c) =>
    o := Mux(fillConstants, c, i)
  }

  io.slice(4, 12).foreach { case (i, o) => o := i }

  io(12) match {
    case (i, o) => o := Mux(incrementBlockCount, incrementedBlockCount, i)
  }

  io.takeRight(3).foreach { case (i, o) => o := i }
}
