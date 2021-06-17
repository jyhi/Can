// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._

class QuarterRound extends Module {
  val io = IO(new Bundle {
    val in = Input(Vec(4, UInt(32.W)))
    val out = Output(Vec(4, UInt(32.W)))
  })

  private def rotateLeft(v: UInt, b: Int): UInt =
    v(31 - b, 0) ## v(31, 32 - b)

  val a0 = io.in(0)
  val b0 = io.in(1)
  val c0 = io.in(2)
  val d0 = io.in(3)

  val a1 = a0 + b0
  val d1 = rotateLeft(d0 ^ a1, 16)
  val c1 = c0 + d1
  val b1 = rotateLeft(b0 ^ c1, 12)

  val a2 = a1 + b1
  val d2 = rotateLeft(d1 ^ a2, 8)
  val c2 = c1 + d2
  val b2 = rotateLeft(b1 ^ c2, 7)

  io.out(0) := a2
  io.out(1) := b2
  io.out(2) := c2
  io.out(3) := d2
}
