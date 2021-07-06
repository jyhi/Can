// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

class CombinationalQuarterRound extends BaseQuarterRound {
  private val a0 = in(0)
  private val b0 = in(1)
  private val c0 = in(2)
  private val d0 = in(3)

  private val a1 = a0 + b0
  private val d1 = rotateLeft(d0 ^ a1, 16)
  private val c1 = c0 + d1
  private val b1 = rotateLeft(b0 ^ c1, 12)

  private val a2 = a1 + b1
  private val d2 = rotateLeft(d1 ^ a2, 8)
  private val c2 = c1 + d2
  private val b2 = rotateLeft(b1 ^ c2, 7)

  out(0) := a2
  out(1) := b2
  out(2) := c2
  out(3) := d2
}
