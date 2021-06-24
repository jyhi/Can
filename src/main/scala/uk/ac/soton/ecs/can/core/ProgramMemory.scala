// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._

class ProgramMemory(
    addrWidth: Int,
    cwWidth: Int,
    size: Int,
    syncMem: Boolean = true
) extends MultiIOModule {
  val br = IO(new Bundle {
    val abs = Input(Bool())
    val rel = Input(Bool())
    val addr = Input(UInt(addrWidth.W))
  })
  val cw = IO(Output(UInt(cwWidth.W)))
  val write = IO(new Bundle {
    val en = Input(Bool())
    val addr = Input(UInt(addrWidth.W))
    val data = Input(UInt(cwWidth.W))
  })

  val mem =
    if (syncMem) SyncReadMem(size, UInt(cwWidth.W))
    else Mem(size, UInt(cwWidth.W))
  val pc = RegInit(0.U(addrWidth.W))

  when(write.en) {
    mem(write.addr) := write.data
  }

  when(br.abs) {
    pc := br.addr.asUInt()
  }.elsewhen(br.rel) {
    pc := (pc.asSInt() + br.addr.asSInt()).asUInt()
  }.otherwise {
    pc := pc + 1.U
  }

  cw := mem(pc)
}
