// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._
import chisel3.util._
import uk.ac.soton.ecs.can.types._
import uk.ac.soton.ecs.can.config.CanCoreConfiguration

class RegisterFile(implicit cfg: CanCoreConfiguration) extends MultiIOModule {
  private val addrWidth = log2Ceil(cfg.registerFileWords)

  val read = IO(Vec(2, new MemoryReadIO(addrWidth, 512)))
  val write = IO(new MemoryWriteIO(addrWidth, 512))

  private val reg = Reg(Vec(cfg.registerFileWords, UInt(512.W)))

  read(0).data := reg(read(0).addr)
  read(1).data := reg(read(1).addr)

  when(write.en) {
    reg(write.addr) := write.data
  }
}
