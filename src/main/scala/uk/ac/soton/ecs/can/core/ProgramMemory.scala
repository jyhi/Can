// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._
import chisel3.util.log2Ceil
import uk.ac.soton.ecs.can.types.CanCoreControlWord
import uk.ac.soton.ecs.can.config.CanCoreConfiguration

class ProgramMemory(implicit cfg: CanCoreConfiguration) extends MultiIOModule {
  private val addrWidth = log2Ceil(cfg.programMemoryWords)
  private val cwWidth = (new CanCoreControlWord).getWidth

  val br = IO(new Bundle {
    val abs = Input(Bool())
    val rel = Input(Bool())
    val addr = Input(UInt(addrWidth.W))
  })
  val cw = IO(Output(UInt(cwWidth.W)))

  val read = IO(new Bundle {
    val en = Input(Bool())
    val addr = Input(UInt(addrWidth.W))
    val data = Output(UInt(cwWidth.W))
  })
  val write = IO(new Bundle {
    val en = Input(Bool())
    val addr = Input(UInt(addrWidth.W))
    val data = Input(UInt(cwWidth.W))
  })

  private val mem =
    if (cfg.syncReadMemory)
      SyncReadMem(cfg.programMemoryWords, UInt(cwWidth.W))
    else
      Mem(cfg.programMemoryWords, UInt(cwWidth.W))

  private val pc = RegInit(0.U(addrWidth.W))

  when(br.abs) {
    pc := br.addr.asUInt()
  }.elsewhen(br.rel) {
    pc := (pc.asSInt() + br.addr.asSInt()).asUInt()
  }.otherwise {
    pc := pc + 1.U
  }

  cw := mem(pc)

  when(read.en) {
    read.data := mem(read.addr)
  }

  when(write.en) {
    mem(write.addr) := write.data
  }
}
