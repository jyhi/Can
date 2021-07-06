// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._

abstract class BaseQuarterRound extends MultiIOModule {
  val in = IO(Input(Vec(4, UInt(32.W))))
  val out = IO(Output(Vec(4, UInt(32.W))))

  protected def rotateLeft(v: UInt, b: Int): UInt =
    v(31 - b, 0) ## v(31, 32 - b)
}
