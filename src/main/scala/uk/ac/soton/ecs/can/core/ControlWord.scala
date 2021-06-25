// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._

class ControlWord(addrWidth: Int, immWidth: Int = 8) extends Bundle {
  val immediate = UInt(immWidth.W)
  val absoluteBranch = Bool()
  val relativeBranch = Bool()
  val ramReadAddress = Vec(2, UInt(addrWidth.W))
  val ramWriteAddress = UInt(addrWidth.W)
  val fillConstant = Bool()
  val incrementBlockCount = Bool()
  val roundLoop = Bool()
  val addFrom = Bool()
  val xorFrom = Bool()
  val writeBackFromInit = Bool()
  val writeBackFromRound = Bool()
}

object ControlWord {
  def apply(addrWidth: Int, immWidth: Int = 8) =
    new ControlWord(addrWidth, immWidth)
}
