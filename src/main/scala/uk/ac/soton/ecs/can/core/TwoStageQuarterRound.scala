// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._

class TwoStageQuarterRound extends BaseQuarterRound {
  private val a0 = in(0)
  private val b0 = in(1)
  private val c0 = in(2)
  private val d0 = in(3)

  private val a1 = a0 + b0
  private val d1 = rotateLeft(d0 ^ a1, 16)
  private val c1 = c0 + d1
  private val b1 = rotateLeft(b0 ^ c1, 12)

  private val reg = Reg(Vec(4, UInt(32.W)))
  reg(0) := a1
  reg(1) := b1
  reg(2) := c1
  reg(3) := d1

  private val a2 = reg(0) + reg(1)
  private val d2 = rotateLeft(reg(3) ^ a2, 8)
  private val c2 = reg(2) + d2
  private val b2 = rotateLeft(reg(1) ^ c2, 7)

  out(0) := a2
  out(1) := b2
  out(2) := c2
  out(3) := d2
}
