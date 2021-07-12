// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.types

import chisel3.util.log2Ceil

case object CanCoreALUFunction {
  val blockInitialize = 1
  val columnarRound = 2
  val diagonalRound = 3
  val add = 4
  val xor = 5
  val a = 6
  val b = 7

  def requiredWidth = log2Ceil(b)
}
