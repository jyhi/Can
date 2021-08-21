// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.types

import chisel3._

class CanCoreIO(
    programMemoryAddressWidth: Int,
    programMemoryDataWidth: Int,
    dataMemoryAddressWidth: Int,
    dataMemoryDataWidth: Int
) extends Bundle {
  val take = Input(Bool())
  val halted = Output(Bool())

  val programMemory =
    new MemoryReadWriteIO(programMemoryAddressWidth, programMemoryDataWidth)
  val dataMemory =
    new MemoryReadWriteIO(dataMemoryAddressWidth, dataMemoryDataWidth)
}
