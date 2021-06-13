// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._

class DataMemory(addrWidth: Int, dataWidth: Int, size: Int) extends Module {
  val io = IO(new Bundle {
    val read = new Bundle {
      val addr = Input(UInt(addrWidth.W))
      val data = Output(UInt(dataWidth.W))
    }
    val write = new Bundle {
      val en = Input(Bool())
      val addr = Input(UInt(addrWidth.W))
      val data = Input(UInt(dataWidth.W))
    }
  })

  val mem = SyncReadMem(size, UInt(dataWidth.W))

  io.read.data := mem(io.read.addr)

  when(io.write.en) {
    mem(io.write.addr) := io.write.data
  }
}
