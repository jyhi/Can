// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._

class Adder extends MultiIOModule {
  val lhs = IO(Input(Vec(16, UInt(32.W))))
  val rhs = IO(Input(Vec(16, UInt(32.W))))
  val out = IO(Output(Vec(16, UInt(32.W))))

  out := lhs.zip(rhs).map { case (lhs, rhs) => lhs + rhs }
}
