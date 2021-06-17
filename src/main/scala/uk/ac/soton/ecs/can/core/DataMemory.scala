// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._

class DataMemory(addrWidth: Int, dataWidth: Int, size: Int)
    extends MultiIOModule {
  val read = IO(new Bundle {
    val addr = Input(UInt(addrWidth.W))
    val data = Output(UInt(dataWidth.W))
  })
  val write = IO(new Bundle {
    val en = Input(Bool())
    val addr = Input(UInt(addrWidth.W))
    val data = Input(UInt(dataWidth.W))
  })

  val mem = SyncReadMem(size, UInt(dataWidth.W))

  read.data := mem(read.addr)

  when(write.en) {
    mem(write.addr) := write.data
  }
}
