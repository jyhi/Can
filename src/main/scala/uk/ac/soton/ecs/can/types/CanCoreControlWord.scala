// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.types

import chisel3._
import chisel3.util.log2Ceil
import uk.ac.soton.ecs.can.config.CanCoreConfiguration

class CanCoreControlWord(implicit val cfg: CanCoreConfiguration)
    extends Bundle {
  private val dataMemoryAddressWidth = log2Ceil(cfg.dataMemoryWords)
  private val registerFileAddressWidth = log2Ceil(cfg.registerFileWords)

  val immediate = UInt(cfg.immediateWidth.W)
  val absoluteBranch = Bool()
  val relativeBranch = Bool()

  val dataMemoryReadAddress = UInt(dataMemoryAddressWidth.W)
  val dataMemoryWriteEnable = Bool()
  val dataMemoryWriteAddress = UInt(dataMemoryAddressWidth.W)

  val registerFileReadAddress = Vec(2, UInt(registerFileAddressWidth.W))
  val registerFileWriteFrom = Bool()
  val registerFileWriteEnable = Bool()
  val registerFileWriteAddress = UInt(registerFileAddressWidth.W)

  val fillConstant = Bool()
  val incrementBlockCount = Bool()
  val aluFunction = UInt(CanCoreALUFunction.requiredWidth.W)
}
