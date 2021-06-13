package uk.ac.soton.ecs.can.core

import chisel3._

class ProgramMemory(addrWidth: Int, cwWidth: Int, size: Int) extends Module {
  val io = IO(new Bundle {
    val br = new Bundle {
      val abs = Input(Bool())
      val rel = Input(Bool())
      val addr = Input(UInt(addrWidth.W))
    }
    val cw = Output(UInt(cwWidth.W))
    val write = new Bundle {
      val en = Input(Bool())
      val addr = Input(UInt(addrWidth.W))
      val data = Input(UInt(cwWidth.W))
    }
  })

  val mem = SyncReadMem(size, UInt(cwWidth.W))
  val pc = RegInit(0.U(addrWidth.W))

  when(io.write.en) {
    mem(io.write.addr) := io.write.data
  }

  when(io.br.abs) {
    pc := io.br.addr.asUInt()
  }.elsewhen(io.br.rel) {
    pc := (pc.asSInt() + io.br.addr.asSInt()).asUInt()
  }.otherwise {
    pc := pc + 1.U
  }

  io.cw := mem(pc)
}
