// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._
import chisel3.util.MuxLookup
import uk.ac.soton.ecs.can.config.CanCoreConfiguration
import uk.ac.soton.ecs.can.types.CanCoreALUFunction

class ALU(implicit cfg: CanCoreConfiguration) extends MultiIOModule {
  val fillConstant = IO(Input(Bool()))
  val incrementBlockCount = IO(Input(Bool()))
  val f = IO(Input(UInt(CanCoreALUFunction.requiredWidth.W)))
  val a = IO(Input(UInt(512.W)))
  val b = IO(Input(UInt(512.W)))
  val y = IO(Output(UInt(512.W)))

  private val blockInitializer = Module(new BlockInitializer)
  private val columnarRound = Module(new ColumnarRound)
  private val diagonalRound = Module(new DiagonalRound)
  private val adder = Module(new Adder)
  private val xorer = Module(new Xorer)

  // Module inputs
  blockInitializer.fillConstants := fillConstant
  blockInitializer.incrementBlockCount := incrementBlockCount
  blockInitializer.in := a
  columnarRound.in := a
  diagonalRound.in := a
  adder.lhs := a
  adder.rhs := b
  xorer.lhs := a
  xorer.rhs := b

  // Module selection and outputs
  y := MuxLookup(
    f,
    0.U(512.W),
    Array(
      CanCoreALUFunction.blockInitialize.U -> blockInitializer.out,
      CanCoreALUFunction.columnarRound.U -> columnarRound.out,
      CanCoreALUFunction.diagonalRound.U -> diagonalRound.out,
      CanCoreALUFunction.add.U -> adder.out,
      CanCoreALUFunction.xor.U -> xorer.out
    )
  )
}
