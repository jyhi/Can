// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._
import chisel3.util.log2Ceil
import chisel3.util.experimental.loadMemoryFromFile
import uk.ac.soton.ecs.can.types._
import uk.ac.soton.ecs.can.config.CanCoreConfiguration

class DataMemory(implicit cfg: CanCoreConfiguration) extends MultiIOModule {
  private val addrWidth = log2Ceil(cfg.dataMemoryWords)

  val read = IO(new MemoryReadIO(addrWidth, 512))
  val write = IO(new MemoryWriteIO(addrWidth, 512))

  private val mem =
    if (cfg.syncReadMemory)
      SyncReadMem(cfg.dataMemoryWords, UInt(512.W), SyncReadMem.ReadFirst)
    else
      Mem(cfg.dataMemoryWords, UInt(512.W))

  read.data := mem(read.addr)

  when(write.en) {
    mem(write.addr) := write.data
  }

  loadMemoryFromFile(mem, "firmware/test/test.data.hex")
}
