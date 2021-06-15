package uk.ac.soton.ecs.can.core

import chisel3._

class DiagonalRound extends Module {
  val io = IO(new Bundle {
    val in = Input(Vec(16, UInt(32.W)))
    val out = Output(Vec(16, UInt(32.W)))
  })

  Seq(
    Seq(0, 5, 10, 15),
    Seq(1, 6, 11, 12),
    Seq(2, 7, 8, 13),
    Seq(3, 4, 9, 14)
  ).foreach { roundWires =>
    val quarterRound = Module(new QuarterRound)
    roundWires.zipWithIndex.foreach { roundWire =>
      quarterRound.io.in(roundWire._2) := io.in(roundWire._1)
      io.out(roundWire._1) := quarterRound.io.out(roundWire._2)
    }
  }
}
