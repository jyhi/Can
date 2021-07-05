// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._

class DataMemory(
    addrWidth: Int,
    dataWidth: Int,
    size: Int,
    syncMem: Boolean
) extends MultiIOModule {
  val read = IO(
    Vec(
      2,
      new Bundle {
        val addr = Input(UInt(addrWidth.W))
        val data = Output(UInt(dataWidth.W))
      }
    )
  )
  val write = IO(new Bundle {
    val en = Input(Bool())
    val addr = Input(UInt(addrWidth.W))
    val data = Input(UInt(dataWidth.W))
  })

  private val mem =
    if (syncMem) SyncReadMem(size, UInt(dataWidth.W))
    else Mem(size, UInt(dataWidth.W))

  read.foreach(p => p.data := mem(p.addr))

  when(write.en) {
    mem(write.addr) := write.data
  }
}
