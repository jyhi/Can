// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._
import chisel3.util.log2Ceil
import uk.ac.soton.ecs.can.config.CanCoreConfiguration

class DataMemory(implicit cfg: CanCoreConfiguration) extends MultiIOModule {
  private val addrWidth = log2Ceil(cfg.dataMemoryWords)

  val read = IO(
    Vec(
      2,
      new Bundle {
        val addr = Input(UInt(addrWidth.W))
        val data = Output(UInt(512.W))
      }
    )
  )
  val write = IO(new Bundle {
    val en = Input(Bool())
    val addr = Input(UInt(addrWidth.W))
    val data = Input(UInt(512.W))
  })

  private val mem =
    if (cfg.syncReadMemory)
      SyncReadMem(cfg.dataMemoryWords, UInt(512.W))
    else
      Mem(cfg.dataMemoryWords, UInt(512.W))

  read.foreach(p => p.data := mem(p.addr))

  when(write.en) {
    mem(write.addr) := write.data
  }
}
