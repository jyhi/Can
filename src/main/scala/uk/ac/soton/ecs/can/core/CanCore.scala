// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._
import scala.math.{ceil, log}

class CanCore(implicit cfg: CanCoreConfiguration) extends MultiIOModule {

  // ========== Calculated Parameters ========== //

  private val programMemoryAddressWidth = ceil(
    log(cfg.programMemoryWords) / log(2)
  ).toInt
  private val controlWordWidth = ControlWord(programMemoryAddressWidth).getWidth
  private val dataMemoryAddressWidth = ceil(
    log(cfg.dataMemoryWords) / log(2)
  ).toInt
  private val blockWidth = 512

  // ========== External I/O Ports ========== //

  val io = IO(new Bundle {
    val programMemory = new Bundle {
      val read = new Bundle {
        val addr = Input(UInt(programMemoryAddressWidth.W))
        val data = Output(UInt(controlWordWidth.W))
      }
      val write = new Bundle {
        val en = Input(Bool())
        val addr = Input(UInt(programMemoryAddressWidth.W))
        val data = Input(UInt(controlWordWidth.W))
      }
    }
    val dataMemory = new Bundle {
      val take = Input(Bool())
      val read = new Bundle {
        val addr = Input(UInt(dataMemoryAddressWidth.W))
        val data = Output(UInt(blockWidth.W))
      }
      val write = new Bundle {
        val en = Input(Bool())
        val addr = Input(UInt(dataMemoryAddressWidth.W))
        val data = Input(UInt(blockWidth.W))
      }
    }
  })

  // ========== Modules ========== //

  private val programMemory = Module(
    new ProgramMemory(
      programMemoryAddressWidth,
      controlWordWidth,
      cfg.programMemoryWords,
      cfg.syncReadMemory
    )
  )
  private val dataMemory = Module(
    new DataMemory(
      dataMemoryAddressWidth,
      blockWidth,
      cfg.dataMemoryWords,
      cfg.syncReadMemory
    )
  )
  private val blockInitializer = Module(new BlockInitializer)
  private val columnarRound = Module(ChaChaRound.columnar)
  private val diagonalRound = Module(ChaChaRound.diagonal)
  private val adder = Module(new Adder)
  private val xorer = Module(new Xorer)

  // ========== Non-Module Components ========== //

  private val afterBlockInitializer =
    if (cfg.regAfterBlockInitializer)
      Reg(Vec(16, UInt(32.W)))
    else
      Wire(Vec(16, UInt(32.W)))
  private val betweenRounds =
    if (cfg.regBetweenRounds)
      Reg(Vec(16, UInt(32.W)))
    else
      Wire(Vec(16, UInt(32.W)))
  private val afterRounds = Reg(Vec(16, UInt(32.W)))
  private val afterAdder =
    if (cfg.regAfterAdder)
      Reg(Vec(16, UInt(32.W)))
    else
      Wire(Vec(16, UInt(32.W)))
  private val afterXorer = Wire(Vec(16, UInt(32.W)))

  // ========== Buses (Port Aliases) ========== //

  private val ctrl =
    programMemory.cw.asTypeOf(ControlWord(programMemoryAddressWidth))
  private val data =
    dataMemory.read.map(_.data.asTypeOf(Vec(16, UInt(32.W))))

  // ========== Multiplexers ========== //

  private val muxRoundLoop =
    Mux(ctrl.roundLoop, afterRounds, afterBlockInitializer)
  private val muxAddFrom = Mux(ctrl.addFrom, data(0), data(1))
  private val muxXorFrom = Mux(ctrl.xorFrom, data(0), data(1))
  private val muxWbFromRound =
    Mux(ctrl.writeBackFromRound, afterAdder, afterXorer)
  private val muxWbFromInit =
    Mux(ctrl.writeBackFromInit, blockInitializer.out, muxWbFromRound)

  // ========== Wiring Between Modules / Components ========== //

  programMemory.br.abs := ctrl.absoluteBranch
  programMemory.br.rel := ctrl.relativeBranch
  programMemory.br.addr := ctrl.immediate
  programMemory.read <> io.programMemory.read
  programMemory.write <> io.programMemory.write

  dataMemory.read(0).addr := ctrl.ramReadAddress(0)
  dataMemory.read(1).addr := Mux(
    io.dataMemory.take,
    io.dataMemory.read.addr,
    ctrl.ramReadAddress(1)
  )
  io.dataMemory.read.data := dataMemory.read(1).data
  dataMemory.write.en := Mux(
    io.dataMemory.take,
    io.dataMemory.write.en,
    ctrl.ramWriteEnable
  )
  dataMemory.write.addr := Mux(
    io.dataMemory.take,
    io.dataMemory.write.addr,
    ctrl.ramWriteAddress
  )
  dataMemory.write.data := Mux(
    io.dataMemory.take,
    io.dataMemory.write.data,
    muxWbFromInit.asUInt
  )

  blockInitializer.fillConstants := ctrl.fillConstants
  blockInitializer.incrementBlockCount := ctrl.incrementBlockCount
  blockInitializer.in := data(0)
  afterBlockInitializer := blockInitializer.out

  diagonalRound.in := muxRoundLoop
  betweenRounds := diagonalRound.out
  columnarRound.in := betweenRounds
  afterRounds := columnarRound.out

  adder.lhs := afterRounds
  adder.rhs := muxAddFrom
  afterAdder := adder.out

  xorer.lhs := afterAdder
  xorer.rhs := muxXorFrom
  afterXorer := xorer.out

}
