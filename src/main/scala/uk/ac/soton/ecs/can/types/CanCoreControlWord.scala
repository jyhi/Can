// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.types

import chisel3._
import chisel3.util.log2Ceil
import uk.ac.soton.ecs.can.config.CanCoreConfiguration

class CanCoreControlWord(implicit val cfg: CanCoreConfiguration)
    extends Bundle {
  private val dataMemoryAddrWidth = log2Ceil(cfg.dataMemoryWords)

  val immediate = UInt(cfg.immediateWidth.W)
  val absoluteBranch = Bool()
  val relativeBranch = Bool()
  val dataMemoryReadAddress = Vec(2, UInt(dataMemoryAddrWidth.W))
  val dataMemoryWriteEnable = Bool()
  val dataMemoryWriteAddress = UInt(dataMemoryAddrWidth.W)
  val fillConstants = Bool()
  val incrementBlockCount = Bool()
  val roundLoop = Bool()
  val addFrom = Bool()
  val xorFrom = Bool()
  val writeBackFromInit = Bool()
  val writeBackFromRound = Bool()
}
