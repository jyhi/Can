package uk.ac.soton.ecs.can.core

import chisel3._

class ColumnRound extends Module {
  val io = IO(new Bundle {
    val in = Input(Vec(16, UInt(32.W)))
    val out = Output(Vec(16, UInt(32.W)))
  })

  Seq(
    Seq(0, 4, 8, 12),
    Seq(1, 5, 9, 13),
    Seq(2, 6, 10, 14),
    Seq(3, 7, 11, 15)
  ).foreach { roundWires =>
    val quarterRound = Module(new QuarterRound)
    roundWires.zipWithIndex.foreach { roundWire =>
      quarterRound.io.in(roundWire._2) := io.in(roundWire._1)
      io.out(roundWire._1) := quarterRound.io.out(roundWire._2)
    }
  }
}
