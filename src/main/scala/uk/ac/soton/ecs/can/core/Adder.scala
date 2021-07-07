// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._

class Adder extends MultiIOModule {
  val lhs = IO(Input(UInt(512.W)))
  val rhs = IO(Input(UInt(512.W)))
  val out = IO(Output(UInt(512.W)))

  private val _lhs = lhs.asTypeOf(Vec(16, UInt(32.W)))
  private val _rhs = rhs.asTypeOf(Vec(16, UInt(32.W)))
  private val _out = Wire(Vec(16, UInt(32.W)))
  out := _out.asUInt()

  _lhs.zip(_rhs).zip(_out).foreach { case ((l, r), o) => o := l + r }
}
