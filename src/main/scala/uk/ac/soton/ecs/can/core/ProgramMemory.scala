// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._
import chisel3.util.log2Ceil
import uk.ac.soton.ecs.can.config.CanCoreConfiguration
import uk.ac.soton.ecs.can.types._

class ProgramMemory(implicit cfg: CanCoreConfiguration) extends MultiIOModule {
  private val addrWidth = log2Ceil(cfg.programMemoryWords)
  private val cwWidth = (new CanCoreControlWord).getWidth

  val br = IO(new Bundle {
    val abs = Input(Bool())
    val rel = Input(Bool())
    val addr = Input(UInt(addrWidth.W))
  })
  val take = IO(Input(Bool()))

  val read = IO(new MemoryReadIO(addrWidth, cwWidth))
  val write = IO(new MemoryWriteIO(addrWidth, cwWidth))

  private val mem =
    if (cfg.syncReadMemory)
      SyncReadMem(
        cfg.programMemoryWords,
        UInt(cwWidth.W),
        SyncReadMem.ReadFirst
      )
    else
      Mem(cfg.programMemoryWords, UInt(cwWidth.W))

  private val pc = RegInit(0.U(addrWidth.W))

  pc := Mux(
    take,
    pc,
    Mux(
      br.abs,
      br.addr.asUInt(),
      Mux(
        br.rel,
        (pc.asSInt() + br.addr.asSInt()).asUInt(),
        pc + 1.U
      )
    )
  )

  read.data := mem(Mux(take, read.addr, pc))

  when(write.en) {
    mem(write.addr) := write.data
  }
}
