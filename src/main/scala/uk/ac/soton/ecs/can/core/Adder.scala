// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._

class Adder extends MultiIOModule {
  val lhs = IO(Input(UInt(512.W)))
  val rhs = IO(Input(UInt(512.W)))
  val out = IO(Output(UInt(512.W)))

  out := lhs + rhs
}
