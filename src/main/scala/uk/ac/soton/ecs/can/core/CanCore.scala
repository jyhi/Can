// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._
import chisel3.util.log2Ceil
import uk.ac.soton.ecs.can.types._
import uk.ac.soton.ecs.can.config.CanCoreConfiguration

class CanCore(implicit cfg: CanCoreConfiguration) extends MultiIOModule {

  //////////////////// Calculated parameters ////////////////////

  private val programMemoryAddressWidth = log2Ceil(cfg.programMemoryWords)
  private val programMemoryDataWidth = (new CanCoreControlWord).getWidth
  private val dataMemoryAddressWidth = log2Ceil(cfg.dataMemoryWords)
  private val dataMemoryDataWidth = 512

  //////////////////// Ports ////////////////////

  val io = IO(new Bundle {
    val programMemory = new Bundle {
      val read =
        new MemoryReadIO(programMemoryAddressWidth, programMemoryDataWidth)
      val write =
        new MemoryWriteIO(programMemoryAddressWidth, programMemoryDataWidth)
    }
    val dataMemory = new Bundle {
      val take = Input(Bool())
      val read =
        new MemoryReadIO(dataMemoryAddressWidth, dataMemoryDataWidth)
      val write =
        new MemoryWriteIO(dataMemoryAddressWidth, dataMemoryDataWidth)
    }
  })

  //////////////////// Modules ////////////////////

  private val programMemory = Module(new ProgramMemory)
  private val dataMemory = Module(new DataMemory)
  private val registerFile = Module(new RegisterFile)
  private val alu = Module(new ALU)

  //////////////////// Control Paths ////////////////////

  private val ctrl = programMemory.cw.asTypeOf(new CanCoreControlWord)

  programMemory.br.abs := ctrl.absoluteBranch
  programMemory.br.rel := ctrl.relativeBranch
  programMemory.br.addr := ctrl.immediate

  dataMemory.read.addr := Mux(
    io.dataMemory.take,
    io.dataMemory.read.addr,
    ctrl.dataMemoryReadAddress
  )
  dataMemory.write.en := Mux(
    io.dataMemory.take,
    io.dataMemory.write.en,
    ctrl.dataMemoryWriteEnable
  )
  dataMemory.write.addr := Mux(
    io.dataMemory.take,
    io.dataMemory.write.addr,
    ctrl.dataMemoryWriteAddress
  )

  registerFile.read(0).addr := ctrl.registerFileReadAddress(0)
  registerFile.read(1).addr := ctrl.registerFileReadAddress(1)

  registerFile.write.en := ctrl.registerFileWriteEnable
  registerFile.write.addr := ctrl.registerFileWriteAddress

  alu.fillConstant := ctrl.fillConstant
  alu.incrementBlockCount := ctrl.incrementBlockCount
  alu.f := ctrl.aluFunction

  //////////////////// Data Paths ////////////////////

  programMemory.read <> io.programMemory.read
  programMemory.write <> io.programMemory.write

  io.dataMemory.read.data := dataMemory.read.data
  dataMemory.write.data := Mux(
    io.dataMemory.take,
    io.dataMemory.write.data,
    alu.y
  )

  alu.a := registerFile.read(0).data
  alu.b := registerFile.read(1).data
  registerFile.write.data := Mux(
    ctrl.registerFileWriteFrom,
    dataMemory.read.data,
    alu.y
  )

}
